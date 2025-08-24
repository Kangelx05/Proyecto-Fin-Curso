package api.controllers;

import api.dto.OrderDetailRequest;
import api.dto.OrderDetailResponse;
import api.service.OrderDetailService;
import api.mappers.OrderDetailMapper;
import api.domain.OrderDetail;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-detail")
public class OrderDetailController {
    private final OrderDetailService orderDetailService;
    private final api.service.BarOrderPublisher barOrderPublisher;

    public OrderDetailController(OrderDetailService orderDetailService, api.service.BarOrderPublisher barOrderPublisher) {
        this.orderDetailService = orderDetailService;
        this.barOrderPublisher = barOrderPublisher;
    }

    @Operation(
            operationId = "getOrderDetailById",
            summary     = "Get order detail by ID",
            description = "Retrieve an order detail record by its unique identifier."
    )
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getById(@PathVariable int id) throws Exception {
        return ResponseEntity.ok(OrderDetailMapper.toResponse(orderDetailService.findById(id)));
    }

    @Operation(
            operationId = "createOrderDetail",
            summary     = "Create a new order detail",
            description = "Create a new order detail entry with the provided details."
    )
    @PostMapping
    public ResponseEntity<OrderDetailResponse> create(@RequestBody @Valid OrderDetailRequest request) {
        // map DTO to domain entity
        OrderDetail orderDetail = OrderDetailMapper.getOrderDetailFromRequest(request);
        OrderDetail saved = orderDetailService.createOrderDetail(orderDetail);
        return ResponseEntity.ok(OrderDetailMapper.toResponse(saved));
    }

    @Operation(
            operationId = "updateOrderDetail",
            summary     = "Update order detail by ID",
            description = "Update the order detail identified by its ID with new data."
    )
    @PutMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> update(
            @PathVariable int id,
            @RequestBody @Valid OrderDetailRequest request
    ) throws Exception {
        OrderDetail updated = OrderDetailMapper.getOrderDetailFromRequest(request);
        OrderDetail saved = orderDetailService.updateOrderDetail(updated, id);
        return ResponseEntity.ok(OrderDetailMapper.toResponse(saved));
    }

    @Operation(
            operationId = "deleteOrderDetail",
            summary     = "Delete order detail by ID",
            description = "Remove the order detail record identified by its ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) throws Exception {
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to retrieve all orders that have drink items pending to be served at the bar.
     *
     * <p>It returns a list of {@link api.dto.BarOrderResponse} objects, each representing a single
     * order with one or more pending drinks.  The response groups the drink items per order and
     * includes basic information about the table associated with the order.
     *
     * @return list of bar orders waiting to be prepared
     */
    @GetMapping("/bar-pending")
    public ResponseEntity<java.util.List<api.dto.BarOrderResponse>> getBarPendingOrders() {
        return ResponseEntity.ok(orderDetailService.getPendingBarOrders());
    }

    /**
     * Endpoint SSE que transmite en tiempo real las listas de pedidos de barra.
     *
     * <p>El cliente mantiene una conexión abierta y recibe eventos cuando la lista de pedidos pendientes
     * de barra cambia.  Cada evento contiene la lista completa de pedidos en formato JSON.
     *
     * @return emisor SSE para suscribirse a actualizaciones
     */
    @GetMapping(value = "/bar-stream", produces = "text/event-stream")
    public org.springframework.web.servlet.mvc.method.annotation.SseEmitter streamBarOrders() {
        return barOrderPublisher.registerEmitter();
    }

    /**
     * Endpoint to retrieve all orders that have non-drink items pending to be prepared in the kitchen.
     *
     * <p>It returns a list of {@link api.dto.KitchenOrderResponse} objects, each representing a single
     * order with one or more pending kitchen items.  The response groups the kitchen items per order and
     * includes basic information about the table associated with the order.
     *
     * @return list of kitchen orders waiting to be prepared
     */
    @GetMapping("/kitchen-pending")
    public ResponseEntity<java.util.List<api.dto.KitchenOrderResponse>> getKitchenPendingOrders() {
        return ResponseEntity.ok(orderDetailService.getPendingKitchenOrders());
    }

    /**
     * Endpoint to retrieve a flat list of kitchen dishes pending preparation.  Each element
     * corresponds to a single order detail (non-drink) that has not been served.  The
     * grouping of identical dishes is left to the client.  The result is sorted by
     * creation time ascending.
     *
     * @return list of kitchen dishes pending preparation
     */
    @GetMapping("/kitchen-dishes")
    public ResponseEntity<java.util.List<api.dto.KitchenDishResponse>> getKitchenPendingDishes() {
        return ResponseEntity.ok(orderDetailService.getPendingKitchenDishes());
    }

    /**
     * Marca como servidas todas las bebidas pendientes de un pedido concreto y las elimina de la cola de barra.
     *
     * <p>Tras actualizar, se envía un evento SSE con la lista actualizada de pedidos de barra.
     *
     * @param orderId identificador del pedido
     * @return respuesta sin contenido si la operación tiene éxito
     */
    @PutMapping("/bar-pending/{orderId}/serve")
    public ResponseEntity<Void> serveBarOrder(@PathVariable Integer orderId) {
        orderDetailService.markOrderDrinksAsServed(orderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marca un detalle de pedido como en preparación.  Este endpoint debe ser
     * invocado cuando el usuario arrastra un plato desde la columna de
     * "nuevos platos" a la columna de "en preparación" en la interfaz de
     * cocina.  Solo se registra la fecha de inicio de preparación si aún no
     * existe, de modo que múltiples invocaciones no sobrescriben el valor
     * original.
     *
     * @param detailId identificador del detalle de pedido que inicia la preparación
     * @return respuesta sin contenido en caso de éxito
     */
    @PutMapping("/{detailId}/start-prep")
    public ResponseEntity<Void> startPreparation(@PathVariable Integer detailId) {
        orderDetailService.markDetailAsInPreparation(detailId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marca un detalle de pedido como emplatado.  Este endpoint debe ser
     * invocado cuando el usuario arrastra un plato desde la columna de
     * "en preparación" a la columna de "emplatado" en la interfaz de
     * cocina.  Solo se registra la fecha de emplatado si aún no existe, de
     * modo que múltiples invocaciones no sobrescriben el valor original.
     *
     * @param detailId identificador del detalle de pedido que pasa a emplatado
     * @return respuesta sin contenido en caso de éxito
     */
    @PutMapping("/{detailId}/plate")
    public ResponseEntity<Void> plate(@PathVariable Integer detailId) {
        orderDetailService.markDetailAsPlated(detailId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marca un detalle de pedido como servido.  Este endpoint debe de ser usado
     * cuando un plato se sirve en una mesa. Solo se registra la fecha en la que se
     * ha servido el plato
     *
     * @param detailId identificador del detalle de pedido que pasa a emplatado
     * @return respuesta sin contenido en caso de éxito
     */
    @PutMapping("/{detailId}/serve")
    public ResponseEntity<Void> serveKitchenDish(@PathVariable Integer detailId) {
        orderDetailService.markDetailAsServed(detailId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieve the order details of the current in-service order for a table.
     *
     * <p>This endpoint returns all {@link api.dto.OrderDetailResponse}s whose parent order
     * is the active order (state {@code IN_SERVICE}) for the table identified by
     * {@code tableId}.  If the table does not currently have an open order, an empty
     * list is returned.  The details are ordered by their creation timestamp to
     * reflect the sequence in which items were added to the ticket.</p>
     *
     * @param tableId the unique identifier of the table
     * @return a list of order detail DTOs for the current order of the given table
     */
    @Operation(
            operationId = "getOrderDetailsByTable",
            summary     = "Get current order details for a table",
            description = "Retrieve all order detail records belonging to the active (IN_SERVICE) order of a specific table."
    )
    @GetMapping("/table/{tableId}")
    public ResponseEntity<java.util.List<OrderDetailResponse>> getOrderDetailsByTable(@PathVariable int tableId) {
        java.util.List<api.domain.OrderDetail> details = orderDetailService.getOrderDetailsByTable(tableId);
        java.util.List<OrderDetailResponse> responses = details.stream().map(OrderDetailMapper::toResponse).toList();
        return ResponseEntity.ok(responses);
    }
}