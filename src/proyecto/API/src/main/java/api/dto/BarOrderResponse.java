package api.dto;

import java.util.List;

/**
 * DTO representing an order that contains pending drink items to be prepared at the bar.
 *
 * <p>This response groups all unserved drink items belonging to a single order.  Each
 * {@link BarOrderItem} entry contains the name of the product and the quantity ordered.
 */
public class BarOrderResponse {
    private Integer orderId;
    private Integer tableId;
    private Integer tableNumber;
    private List<BarOrderItem> items;

    public BarOrderResponse() {}

    public BarOrderResponse(Integer orderId, Integer tableId, Integer tableNumber, List<BarOrderItem> items) {
        this.orderId = orderId;
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.items = items;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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

    public List<BarOrderItem> getItems() {
        return items;
    }

    public void setItems(List<BarOrderItem> items) {
        this.items = items;
    }

    /**
     * Represents a single line in a bar order.  It contains the name of the product
     * ordered and the quantity requested.  The price is omitted because bar staff
     * only need to prepare the drink, not to handle billing.
     */
    public static class BarOrderItem {
        /**
         * Nombre del producto pedido.
         */
        private String productName;
        /**
         * Número de unidades de este producto en el pedido.  Se renombra desde
         * amount para evitar confusiones con el campo eliminado en OrderDetail.
         */
        private Integer count;

        public BarOrderItem() {}

        public BarOrderItem(String productName, Integer count) {
            this.productName = productName;
            this.count = count;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }
}