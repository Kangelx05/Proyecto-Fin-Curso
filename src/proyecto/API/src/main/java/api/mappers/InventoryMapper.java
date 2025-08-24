package api.mappers;

import api.domain.Inventory;
import api.domain.Product;
import api.dto.*;

public abstract class InventoryMapper {

    /**
     * Create a new {@link Inventory} from the incoming request and the associated
     * {@link Product}. This method exists for backwards compatibility with
     * older service code that used to supply the product separately.
     *
     * @param inventoryRequest the DTO with the desired amount
     * @param product          the already mapped {@link Product} to associate
     * @return a new {@link Inventory} instance
     */
    public static Inventory getInventoryFromRequest(InventoryRequest inventoryRequest, Product product) {
        Inventory inventory = new Inventory();
        inventory.setAmount(inventoryRequest.amount());
        inventory.setProduct(product);
        return inventory;
    }

    /**
     * Create a new {@link Inventory} from the incoming request. The contained
     * {@link api.dto.ProductRequest} will be mapped into a {@link Product}
     * automatically via {@link ProductMapper}.
     *
     * @param inventoryRequest the DTO containing the product and amount
     * @return a new {@link Inventory} instance
     */
    public static Inventory getInventoryFromRequest(InventoryRequest inventoryRequest) {
        Inventory inventory = new Inventory();
        // map the nested product request into an entity
        Product product = ProductMapper.getProductFromRequest(inventoryRequest.product());
        inventory.setProduct(product);
        inventory.setAmount(inventoryRequest.amount());
        return inventory;
    }

    /**
     * Update an existing {@link Inventory} instance from an incoming request.
     * This overload will mutate the provided inventory, updating its product
     * association and amount based on the DTO values.
     *
     * @param inventory        the existing entity to update
     * @param inventoryRequest the DTO containing new values
     * @return the updated {@link Inventory}
     */
    public static Inventory getInventoryFromRequest(Inventory inventory, InventoryRequest inventoryRequest) {
        if (inventory == null) {
            inventory = new Inventory();
        }
        // update the associated product
        Product product = ProductMapper.getProductFromRequest(inventoryRequest.product());
        inventory.setProduct(product);
        inventory.setAmount(inventoryRequest.amount());
        return inventory;
    }

    public static InventoryResponse toResponse(Inventory inventory) {
        if (inventory == null) return null;
        return new InventoryResponse(
                inventory.getId(),
                ProductMapper.toResponse(inventory.getProduct()),
                inventory.getAmount()
        );
    }
}

