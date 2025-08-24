package api.service;

import api.domain.OrderDetail;
import api.domain.Order;
// DTOs and mappers are used in controllers, not the service layer
import api.repository.OrderDetailRepository;
import api.repository.CardProductRepository;
import api.repository.TableRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final CardProductRepository cardProductRepository;
    private final TableRepository tableRepository;
    private final BarOrderPublisher barOrderPublisher;
    /**
     * Repository used to resolve orders for a given table and state.  Injected
     * so that the service can locate the currently open order (state
     * {@code IN_SERVICE}) for a table when retrieving order details.
     */
    private final api.repository.OrderRepository orderRepository;

    /**
     * Ejecutores programados para cambiar el estado de la mesa después de un tiempo determinado.
     * Usamos un ejecutor de un solo hilo porque las operaciones de cambio de estado son ligeras.
     */
    private final java.util.concurrent.ScheduledExecutorService scheduler = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();

    public OrderDetailService(OrderDetailRepository orderDetailRepository,
                              CardProductRepository cardProductRepository,
                              TableRepository tableRepository,
                              BarOrderPublisher barOrderPublisher,
                              api.repository.OrderRepository orderRepository) {
        this.orderDetailRepository = orderDetailRepository;
        this.cardProductRepository = cardProductRepository;
        this.tableRepository = tableRepository;
        this.barOrderPublisher = barOrderPublisher;
        this.orderRepository = orderRepository;
    }

    public OrderDetail findById(int id) throws Exception {
        return orderDetailRepository.findById(id).orElseThrow();
    }

    /**
     * Persist a new order detail.
     *
     * Si el producto pertenece a la categoría de bebidas (drinks/bebidas), se programa una tarea que
     * cambiará el estado de la mesa a WAITING a los 5 minutos si la mesa sigue ocupada.
     *
     * @param orderDetail the domain entity to persist
     * @return the saved {@link OrderDetail}
     */
    public OrderDetail createOrderDetail(@NotNull OrderDetail orderDetail) {
        // Asegura que se establece la fecha de creación si aún no se ha asignado
        if (orderDetail.getCreatedAt() == null) {
            orderDetail.setCreatedAt(java.time.LocalDateTime.now());
        }
        OrderDetail saved = orderDetailRepository.save(orderDetail);
        // Comprueba si el detalle corresponde a una bebida
        Integer productId = null;
        if (saved.getProduct() != null) {
            productId = saved.getProduct().getId();
        }
        if (productId != null) {
            // Obtenemos el producto para verificar la categoría
            cardProductRepository.findById(productId).ifPresent(product -> {
                String category = product.getCategory();
                if (category != null) {
                    String c = category.trim().toLowerCase();
                    if (c.equals("drinks") || c.equals("drink") || c.equals("bebidas") || c.equals("bebida")) {
                        // Se detecta una bebida. Programamos el cambio de estado.
                        scheduleWaitingState(saved);
                        // Publicar lista de pedidos de barra actualizada para notificar a los clientes
                        try {
                            java.util.List<api.dto.BarOrderResponse> currentOrders = getPendingBarOrders();
                            barOrderPublisher.publishOrders(currentOrders);
                        } catch (Exception ignored) {
                            // Si falla la obtención, no enviamos evento
                        }
                    }
                }
            });
        }
        return saved;
    }

    /**
     * Programa una tarea que, al cabo de cinco minutos, establece el estado de la mesa como WAITING
     * siempre y cuando la mesa siga en estado BUSSY y no se haya modificado a otro estado.
     *
     * @param orderDetail Detalle de pedido que originó la programación
     */
    private void scheduleWaitingState(OrderDetail orderDetail) {
        // Obtenemos el identificador de la mesa a partir del pedido asociado
        Order order = orderDetail.getOrder();
        if (order == null) return;
        Integer tableId = null;
        try {
            tableId = order.getTable().getId();
        } catch (Exception ex) {
            // Lazy loading o null
            return;
        }
        if (tableId == null) return;
        final Integer finalTableId = tableId;
        final Integer detailId = orderDetail.getId();
        // Programamos la tarea. 5 minutos = 300 segundos.
        scheduler.schedule(() -> {
            // Vuelve a cargar el detalle del pedido para comprobar si ya se ha servido
            api.domain.OrderDetail currentDetail = null;
            try {
                currentDetail = orderDetailRepository.findById(detailId).orElse(null);
            } catch (Exception ex) {
                // Si no se puede cargar, no se realiza ninguna acción
            }
            if (currentDetail == null) return;
            // Solo mostrar alerta si todavía no se ha establecido servedAt
            if (currentDetail.getServedAt() != null) {
                // La bebida ya ha sido entregada, no se cambia el estado
                return;
            }
            // Busca la mesa de nuevo para evitar el uso de entidades desactualizadas
            api.domain.Table table = tableRepository.findTableById(finalTableId);
            if (table != null) {
                String current = table.getState();
                // Solo actualiza si sigue ocupada (BUSSY)
                if (current != null && current.equalsIgnoreCase("BUSSY")) {
                    table.setState("WAITING");
                    tableRepository.save(table);
                }
            }
        }, 5, java.util.concurrent.TimeUnit.MINUTES);
    }

    /**
     * Update an existing order detail.
     *
     * @param updated contains the new values
     * @param id      identifier of the record to update
     * @return the updated {@link OrderDetail}
     * @throws Exception if the record does not exist
     */
    public OrderDetail updateOrderDetail(@NotNull OrderDetail updated, int id) throws Exception {
        OrderDetail existing = orderDetailRepository.findById(id).orElseThrow();
        // La cantidad ya no se gestiona; cada OrderDetail representa una unidad, por lo que no se actualiza ningún campo de cantidad.
        existing.setOrder(updated.getOrder());
        existing.setProduct(updated.getProduct());
        existing.setProductName(updated.getProductName());
        existing.setUnitPrice(updated.getUnitPrice());
        OrderDetail saved = orderDetailRepository.save(existing);
        // Tras actualizar, emitir el listado actualizado de pedidos de barra
        try {
            java.util.List<api.dto.BarOrderResponse> currentOrders = getPendingBarOrders();
            barOrderPublisher.publishOrders(currentOrders);
        } catch (Exception ignored) {}
        return saved;
    }

    public void deleteOrderDetail(int id) throws Exception {
        OrderDetail orderDetail = orderDetailRepository.findById(id).orElseThrow();
        orderDetailRepository.delete(orderDetail);
    }

    /**
     * Retrieves a list of bar orders, each representing an order that contains one or more
     * unserved drink items.  The result groups all pending drink details by their parent
     * order and includes basic table information.
     *
     * @return a list of {@link api.dto.BarOrderResponse} objects describing pending bar orders
     */
    public java.util.List<api.dto.BarOrderResponse> getPendingBarOrders() {
        // Recupera los detalles pendientes de bebidas, ya ordenados por fecha de pedido ascendente
        java.util.List<OrderDetail> pendingDetails = orderDetailRepository.findPendingDrinkDetails();
        // Agrupa las líneas de bebida pendientes por pedido y producto.  Como cada OrderDetail
        // ahora representa una unidad, la cantidad se calcula contando cuántas veces aparece
        // el mismo producto en el mismo pedido.
        java.util.Map<Integer, api.dto.BarOrderResponse> grouped = new java.util.LinkedHashMap<>();
        for (OrderDetail detail : pendingDetails) {
            if (detail.getOrder() == null) continue;
            Integer orderId = detail.getOrder().getId();
            if (orderId == null) continue;
            api.dto.BarOrderResponse orderResponse = grouped.get(orderId);
            if (orderResponse == null) {
                // Extrae información de la mesa asociada al pedido
                Integer tableId = null;
                Integer tableNumber = null;
                try {
                    api.domain.Table table = detail.getOrder().getTable();
                    if (table != null) {
                        tableId = table.getId();
                        tableNumber = table.getNumTable();
                    }
                } catch (Exception ex) {
                    // ignora posibles excepciones de lazy loading
                }
                orderResponse = new api.dto.BarOrderResponse();
                orderResponse.setOrderId(orderId);
                orderResponse.setTableId(tableId);
                orderResponse.setTableNumber(tableNumber);
                orderResponse.setItems(new java.util.ArrayList<>());
                grouped.put(orderId, orderResponse);
            }
            // Busca si ya existe un elemento para este producto
            api.dto.BarOrderResponse.BarOrderItem existingItem = null;
            for (api.dto.BarOrderResponse.BarOrderItem it : orderResponse.getItems()) {
                if (it.getProductName() != null && it.getProductName().equals(detail.getProductName())) {
                    existingItem = it;
                    break;
                }
            }
            if (existingItem != null) {
                // Incrementa la cantidad
                Integer current = existingItem.getCount();
                if (current == null) current = 0;
                existingItem.setCount(current + 1);
            } else {
                // Crea un nuevo elemento con cantidad 1
                api.dto.BarOrderResponse.BarOrderItem newItem = new api.dto.BarOrderResponse.BarOrderItem();
                newItem.setProductName(detail.getProductName());
                newItem.setCount(1);
                orderResponse.getItems().add(newItem);
            }
        }
        return new java.util.ArrayList<>(grouped.values());
    }

    /**
     * Retrieves a list of kitchen orders, each representing an order that contains one or more
     * unserved items excluding drinks.  The result groups all pending kitchen details by their parent
     * order and includes basic table information.
     *
     * @return a list of {@link api.dto.KitchenOrderResponse} objects describing pending kitchen orders
     */
    public java.util.List<api.dto.KitchenOrderResponse> getPendingKitchenOrders() {
        java.util.List<OrderDetail> pendingDetails = orderDetailRepository.findPendingKitchenDetails();
        // Agrupa las líneas de cocina pendientes por pedido y producto.  Cada OrderDetail
        // representa una unidad, por lo que la cantidad se calcula contando la
        // repetición de productos por pedido.
        java.util.Map<Integer, api.dto.KitchenOrderResponse> grouped = new java.util.LinkedHashMap<>();
        for (OrderDetail detail : pendingDetails) {
            if (detail.getOrder() == null) continue;
            Integer orderId = detail.getOrder().getId();
            if (orderId == null) continue;
            api.dto.KitchenOrderResponse orderResponse = grouped.get(orderId);
            if (orderResponse == null) {
                Integer tableId = null;
                Integer tableNumber = null;
                try {
                    api.domain.Table table = detail.getOrder().getTable();
                    if (table != null) {
                        tableId = table.getId();
                        tableNumber = table.getNumTable();
                    }
                } catch (Exception ex) {
                    // ignore lazy loading exceptions
                }
                orderResponse = new api.dto.KitchenOrderResponse();
                orderResponse.setOrderId(orderId);
                orderResponse.setTableId(tableId);
                orderResponse.setTableNumber(tableNumber);
                orderResponse.setItems(new java.util.ArrayList<>());
                grouped.put(orderId, orderResponse);
            }
            // Busca si ya existe un elemento para este producto
            api.dto.KitchenOrderResponse.KitchenOrderItem existingItem = null;
            for (api.dto.KitchenOrderResponse.KitchenOrderItem it : orderResponse.getItems()) {
                if (it.getProductName() != null && it.getProductName().equals(detail.getProductName())) {
                    existingItem = it;
                    break;
                }
            }
            if (existingItem != null) {
                Integer current = existingItem.getCount();
                if (current == null) current = 0;
                existingItem.setCount(current + 1);
            } else {
                api.dto.KitchenOrderResponse.KitchenOrderItem newItem = new api.dto.KitchenOrderResponse.KitchenOrderItem();
                newItem.setProductName(detail.getProductName());
                newItem.setCount(1);
                orderResponse.getItems().add(newItem);
            }
        }
        return new java.util.ArrayList<>(grouped.values());
    }

    /**
     * Retrieves a flat list of kitchen dishes pending to be prepared.  Each element of the
     * returned list corresponds to a single order detail that is not a drink and has not
     * been served.  The list is ordered by the creation timestamp of each detail to
     * reflect the arrival order.
     *
     * <p>For each detail, information about the associated product (including its image
     * and identifier), the creation time, and optional table data is provided.  The
     * grouping of identical products should be handled by the caller (e.g. the front-end)</p>.
     *
     * @return list of {@link api.dto.KitchenDishResponse} describing individual kitchen dishes
     */
    public java.util.List<api.dto.KitchenDishResponse> getPendingKitchenDishes() {
        java.util.List<OrderDetail> pendingDetails = orderDetailRepository.findPendingKitchenDetails();
        java.util.List<api.dto.KitchenDishResponse> result = new java.util.ArrayList<>();
        for (OrderDetail detail : pendingDetails) {
            api.dto.KitchenDishResponse dish = new api.dto.KitchenDishResponse();
            // detail id
            dish.setDetailId(detail.getId());
            // product id and name
            if (detail.getProduct() != null) {
                dish.setProductId(detail.getProduct().getId());
                // Use the product's current name if available; fallback to the denormalized name
                String name = detail.getProduct().getName();
                if (name == null || name.trim().isEmpty()) {
                    name = detail.getProductName();
                }
                dish.setProductName(name);
                // Encode image data if present
                byte[] data = detail.getProduct().getData();
                if (data != null && data.length > 0) {
                    try {
                        String base64 = java.util.Base64.getEncoder().encodeToString(data);
                        dish.setImageData(base64);
                    } catch (Exception e) {
                        // ignore encoding errors; leave imageData null
                    }
                }
            } else {
                // Fallback: set name from denormalized field
                dish.setProductName(detail.getProductName());
            }
            // creation timestamp of the detail
            dish.setCreatedAt(detail.getCreatedAt());


            // Ya no se establece una cantidad aquí; cada detalle representa una unidad.  La
            // agrupación y el conteo se realizarán en el cliente.

            // preparation started timestamp (prepStartedAt) and plating timestamp (preparedAt)
            try {
                dish.setPrepStartedAt(detail.getPrepStartedAt());
            } catch (Exception e) {
                // ignore
            }
            try {
                dish.setPreparedAt(detail.getPreparedAt());
            } catch (Exception e) {
                // ignore
            }
            // table information
            try {
                Order order = detail.getOrder();
                if (order != null) {
                    api.domain.Table table = order.getTable();
                    if (table != null) {
                        dish.setTableId(table.getId());
                        dish.setTableNumber(table.getNumTable());
                    }
                }
            } catch (Exception ex) {
                // ignore lazy loading exceptions
            }
            result.add(dish);
        }
        return result;
    }

    /**
     * Marca como servidos todos los detalles de bebidas pendientes de un pedido.  Establece
     * {@code servedAt} a la hora actual para cada línea y posteriormente publica la lista
     * actualizada de pedidos de barra para notificar a los clientes.
     *
     * @param orderId identificador del pedido
     */
    public void markOrderDrinksAsServed(@NotNull Integer orderId) {
        // Obtiene los detalles pendientes de bebidas de este pedido
        java.util.List<OrderDetail> pending = orderDetailRepository.findPendingDrinkDetailsByOrder(orderId);
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        boolean anyUpdated = false;
        for (OrderDetail detail : pending) {
            detail.setServedAt(now);
            detail.setPreparedAt(now);
            detail.setPrepStartedAt(now);
            anyUpdated = true;
        }
        if (anyUpdated) {
            orderDetailRepository.saveAll(pending);
            // Publica la lista actualizada de pedidos de barra
            try {
                java.util.List<api.dto.BarOrderResponse> currentOrders = getPendingBarOrders();
                barOrderPublisher.publishOrders(currentOrders);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Marca un detalle de pedido como en preparación.  Si el detalle aún no tiene
     * registrado su inicio de preparación, establece la fecha actual en el campo
     * {@code prepStartedAt}.  No modifica el detalle si ya se ha marcado
     * previamente.
     *
     * @param detailId identificador del detalle de pedido a actualizar
     */
    public void markDetailAsInPreparation(@NotNull Integer detailId) {
        if (detailId == null) return;
        java.util.Optional<OrderDetail> opt = orderDetailRepository.findById(detailId);
        if (opt.isPresent()) {
            OrderDetail detail = opt.get();
            if (detail.getPrepStartedAt() == null) {
                detail.setPrepStartedAt(java.time.LocalDateTime.now());
                orderDetailRepository.save(detail);
            }
        }
    }

    /**
     * Marca un detalle de pedido como emplatado.  Este método establece la fecha
     * actual en el campo {@code preparedAt} únicamente si aún no se ha fijado,
     * evitando así sobrescribir un valor existente.  No modifica el detalle
     * si ya se ha registrado previamente la fecha de emplatado.
     *
     * @param detailId identificador del detalle de pedido a actualizar
     */
    public void markDetailAsPlated(@NotNull Integer detailId) {
        if (detailId == null) return;
        java.util.Optional<OrderDetail> opt = orderDetailRepository.findById(detailId);
        if (opt.isPresent()) {
            OrderDetail detail = opt.get();
            if (detail.getPreparedAt() == null) {
                detail.setPreparedAt(java.time.LocalDateTime.now());
                orderDetailRepository.save(detail);
            }
        }
    }

    /**
     * Marca un detalle de pedido como servido.  Este método establece la fecha
     * actual en el campo {@code preparedAt} únicamente si aún no se ha fijado,
     * evitando así sobrescribir un valor existente.  No modifica el detalle
     * si ya se ha registrado previamente la fecha de servido.
     *
     * @param detailId identificador del detalle de pedido a actualizar
     */
    public void markDetailAsServed(@NotNull Integer detailId) {
        if (detailId == null) return;
        java.util.Optional<OrderDetail> opt = orderDetailRepository.findById(detailId);
        if (opt.isPresent()) {
            OrderDetail detail = opt.get();
            if (detail.getServedAt() == null) {
                detail.setServedAt(java.time.LocalDateTime.now());
                orderDetailRepository.save(detail);
            }
        }
    }

    /**
     * Retrieves the order details for the current active order of a table.
     *
     * <p>This method looks up the open order for the given table whose state is
     * {@code IN_SERVICE}.  If such an order exists, it returns all of its
     * {@link OrderDetail} lines ordered by creation time.  If no active
     * order is found (for instance, the table is free or its last order has
     * been finished), the method returns an empty list.  Both served and
     * unserved details belonging to the open order are included in the
     * result.</p>
     *
     * @param tableId identifier of the table
     * @return list of order details belonging to the active order of the table,
     *         or an empty list if no active order exists
     */
    public java.util.List<OrderDetail> getOrderDetailsByTable(@jakarta.validation.constraints.NotNull Integer tableId) {
        if (tableId == null) {
            return java.util.Collections.emptyList();
        }
        // First, locate the active order (state IN_SERVICE) for the given table.  Only
        // one such order should exist per table.  If none is found, return an
        // empty list.  If found, fetch the order details belonging to that order
        // ordered by creation timestamp.
        java.util.Optional<Order> maybeOrder = orderRepository.findByTable_IdAndState(tableId, "IN_SERVICE");
        if (maybeOrder.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        Integer orderId = maybeOrder.get().getId();
        if (orderId == null) {
            return java.util.Collections.emptyList();
        }
        return orderDetailRepository.findByOrder_IdOrderByCreatedAtAsc(orderId);
    }
}