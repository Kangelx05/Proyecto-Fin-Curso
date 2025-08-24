package api.dto;

import java.util.List;

/**
 * DTO representing an order that contains pending non-drink items to be prepared in the kitchen.
 *
 * <p>This response groups all unserved items (excluding drinks) belonging to a single order.
 * Each {@link KitchenOrderItem} entry contains the name of the product and the quantity ordered.</p>
 */
public class KitchenOrderResponse {
    private Integer orderId;
    private Integer tableId;
    private Integer tableNumber;
    private List<KitchenOrderItem> items;

    public KitchenOrderResponse() {}

    public KitchenOrderResponse(Integer orderId, Integer tableId, Integer tableNumber, List<KitchenOrderItem> items) {
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

    public List<KitchenOrderItem> getItems() {
        return items;
    }

    public void setItems(List<KitchenOrderItem> items) {
        this.items = items;
    }

    /**
     * Represents a single line in a kitchen order.  It contains the name of the product
     * ordered and the quantity requested.  The price is omitted because kitchen staff
     * only need to prepare the item, not to handle billing.
     */
    public static class KitchenOrderItem {
        /**
         * Nombre del producto solicitado.
         */
        private String productName;
        /**
         * Número de unidades de este producto en el pedido de cocina.  Se renombra
         * desde amount para evitar confusiones con el antiguo campo eliminado.
         */
        private Integer count;

        public KitchenOrderItem() {}

        public KitchenOrderItem(String productName, Integer count) {
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