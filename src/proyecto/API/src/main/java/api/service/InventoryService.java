package api.service;

import api.domain.Inventory;
import api.domain.Product;
import api.dto.InventoryRequest;
import api.dto.InventoryResponse;
import api.mappers.InventoryMapper;
import api.mappers.ProductMapper;
import api.repository.InventoryRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository){
        this.inventoryRepository = inventoryRepository;
    }

    public InventoryResponse findById(int id) throws Exception {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow();
        return InventoryMapper.toResponse(inventory);
    }

    public InventoryResponse createInventory(@NotNull InventoryRequest inventoryRequest) throws Exception {
        Product product = new Product();
        ProductMapper.updateProductFromRequest(product, inventoryRequest.product());
        inventoryRepository.findByProduct(product).orElseThrow();

        Inventory inventory = new Inventory();
        InventoryMapper.updateInventoryFromRequest(inventory, inventoryRequest);
        Inventory savedInventory = inventoryRepository.save(inventory);

        return InventoryMapper.toResponse(savedInventory);
    }


    public InventoryResponse updateInventory(@NotNull InventoryRequest inventoryRequest, int id) throws Exception {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow();

        InventoryMapper.updateInventoryFromRequest(inventory,inventoryRequest);
        return InventoryMapper.toResponse(inventoryRepository.save(inventory));
    }


    public void deleteInventory(int id) throws Exception {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow();
        inventoryRepository.delete(inventory);
    }


}