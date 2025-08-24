package api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@jakarta.persistence.Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Referencia opcional al producto de la carta.  Se mantiene para poder enlazar
     * un OrderDetail con un CardProduct, pero se permite NULL para que los
     * registros no dependan de la existencia del producto.  La relación se
     * define como LAZY para no cargar automáticamente la entidad asociada.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private CardProduct product;

    /**
     * Nombre del producto en el momento de la venta.  Se almacena
     * independientemente del CardProduct para poder eliminar productos sin
     * perder el histórico de pedidos.
     */
    @NotNull
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    /**
     * Precio unitario del producto en el momento de la venta.
     */
    @NotNull
    @Column(name = "unit_price", nullable = false)
    private Float unitPrice;


    /*
     * La columna amount se ha eliminado.  A partir de ahora cada OrderDetail
     * representa una única unidad del producto, por lo que no se almacena
     * una cantidad en la base de datos.
     */


    /**
     * Momento en el que el plato o bebida ha sido preparado.  Este campo se rellena cuando
     * el pedido se marca como listo en cocina.
     */
    @Column(name = "prepared_at")
    private java.time.LocalDateTime preparedAt;

    /**
     * Momento en el que el producto pasa a estar en preparación.  Se establece
     * en el instante en que un plato abandona la columna de nuevos platos y
     * entra en preparación en la cocina.  Permite calcular tiempos de
     * preparación de cada producto de manera individual.
     */
    @Column(name = "prep_started_at")
    private java.time.LocalDateTime prepStartedAt;

    /**
     * Momento en el que el plato o bebida ha sido entregado al cliente.  Este campo
     * se utiliza para determinar si deben mostrarse alertas de espera para bebidas.
     */
    @Column(name = "served_at")
    private java.time.LocalDateTime servedAt;

    /**
     * Momento en el que se creó esta línea de pedido. Se utiliza para ordenar los
     * pedidos de barra por orden de llegada, ya que la fecha del pedido padre
     * corresponde al primer producto añadido y no refleja la hora de cada detalle.
     */
    @jakarta.persistence.Column(name = "created_at", nullable = false)
    private java.time.LocalDateTime createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public CardProduct getProduct() {
        return product;
    }

    public void setProduct(CardProduct product) {
        this.product = product;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Float getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Float unitPrice) {
        this.unitPrice = unitPrice;
    }

    // Ya no se incluye getAmount/setAmount porque la cantidad se gestiona
    // a través del número de registros de OrderDetail para un producto concreto.

    public java.time.LocalDateTime getPreparedAt() {
        return preparedAt;
    }

    public void setPreparedAt(java.time.LocalDateTime preparedAt) {
        this.preparedAt = preparedAt;
    }

    /**
     * Devuelve el instante en que el plato pasó a estar en preparación.
     *
     * @return fecha y hora de inicio de preparación o {@code null} si todavía no ha empezado
     */
    public java.time.LocalDateTime getPrepStartedAt() {
        return prepStartedAt;
    }

    /**
     * Establece la fecha y hora en la que el plato ha comenzado a prepararse.
     *
     * @param prepStartedAt instante de inicio de preparación
     */
    public void setPrepStartedAt(java.time.LocalDateTime prepStartedAt) {
        this.prepStartedAt = prepStartedAt;
    }

    public java.time.LocalDateTime getServedAt() {
        return servedAt;
    }

    public void setServedAt(java.time.LocalDateTime servedAt) {
        this.servedAt = servedAt;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}