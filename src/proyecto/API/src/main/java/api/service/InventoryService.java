package api.service;

import api.domain.Inventory;
// InventoryMapper is not used directly in this service layer; mapping is handled in controllers
import api.repository.InventoryRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository){
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory findById(int id) throws Exception {
        return inventoryRepository.findById(id).orElseThrow();
    }

    /**
     * Persist a new inventory record.
     *
     * @param inventory the domain entity to save
     * @return the saved {@link Inventory}
     */
    public Inventory createInventory(@NotNull Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    /**
     * Update an existing inventory record with new values.
     *
     * @param updated the entity containing new values (product and amount)
     * @param id      identifier of the record to update
     * @return the updated {@link Inventory}
     * @throws Exception if the record does not exist
     */
    public Inventory updateInventory(@NotNull Inventory updated, int id) throws Exception {
        Inventory existing = inventoryRepository.findById(id).orElseThrow();
        existing.setAmount(updated.getAmount());
        existing.setProduct(updated.getProduct());
        return inventoryRepository.save(existing);
    }

    public void deleteInventory(int id) throws Exception {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow();
        inventoryRepository.delete(inventory);
    }


}