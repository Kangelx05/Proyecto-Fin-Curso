package api.dto;

import java.time.LocalDateTime;

/**
 * Represents a single kitchen dish to be prepared.
 *
 * <p>Each instance contains details about an individual line item from an order that
 * is destined for the kitchen (i.e. not a drink).  It includes identifiers for
 * the order detail and the associated product, the product name, a base64-encoded
 * image of the product, the timestamp when the order detail was created, and
 * optional information about the table from which the order originated.</p>
 */
public class KitchenDishResponse {
    private Integer detailId;
    private Integer productId;
    private String productName;
    private String imageData;
    private LocalDateTime createdAt;
    private Integer tableId;
    private Integer tableNumber;

    /*
     * A partir de esta versión, cada instancia de KitchenDishResponse representa
     * una única unidad del producto, por lo que no se incluye un campo de
     * cantidad.  Las vistas cliente podrán agrupar varias instancias
     * idénticas según sea necesario.
     */

    /**
     * Momento en el que el detalle de pedido pasó a estar en preparación.  Si es {@code null},
     * el plato se considera todavía como "nuevo".
     */
    private LocalDateTime prepStartedAt;

    /**
     * Momento en el que el detalle de pedido fue emplatado.  Si es {@code null}, el plato no
     * se ha finalizado.  Este campo se corresponde con el valor de {@code preparedAt} en la
     * entidad {@link api.domain.OrderDetail}.
     */
    private LocalDateTime preparedAt;

    public KitchenDishResponse() {
    }

    public Integer getDetailId() {
        return detailId;
    }

    public void setDetailId(Integer detailId) {
        this.detailId = detailId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }


    public LocalDateTime getPrepStartedAt() {
        return prepStartedAt;
    }

    public void setPrepStartedAt(LocalDateTime prepStartedAt) {
        this.prepStartedAt = prepStartedAt;
    }

    public LocalDateTime getPreparedAt() {
        return preparedAt;
    }

    public void setPreparedAt(LocalDateTime preparedAt) {
        this.preparedAt = preparedAt;
    }
}