package api.mappers;

import api.domain.Inventory;
import api.domain.Table;
import api.dto.*;

public abstract class InventoryMapper {

    public static void updateInventoryFromRequest(Inventory inventory, InventoryRequest inventoryRequest) {

        inventory.setAmount(inventoryRequest.getAmount());
        ProductMapper.updateProductFromRequest(inventory.getProduct(), inventoryRequest.getProduct());

    }

    public static InventoryResponse toResponse(Inventory inventory){
        if (inventory == null) return null;
        return new InventoryResponse(
                inventory.getId(),
                ProductMapper.toResponse(inventory.getProduct()),
                inventory.getAmount()

        );

    }
}

