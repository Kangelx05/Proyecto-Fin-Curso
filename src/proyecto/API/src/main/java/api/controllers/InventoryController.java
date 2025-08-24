package api.controllers;

import api.dto.InventoryRequest;
import api.domain.Inventory;
import api.dto.InventoryResponse;
import api.service.InventoryService;
import api.mappers.InventoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(
            operationId = "getInventoryById",
            summary     = "Get inventory item by ID",
            description = "Retrieve an inventory record by its unique identifier."
    )
    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getById(@PathVariable int id) throws Exception {
        return ResponseEntity.ok(InventoryMapper.toResponse(inventoryService.findById(id)));
    }

    @Operation(
            operationId = "createInventory",
            summary     = "Create a new inventory record",
            description = "Create a new inventory entry with the provided details."
    )
    @PostMapping
    public ResponseEntity<InventoryResponse> create(@RequestBody @Valid InventoryRequest request) throws Exception {
        // map the DTO into a domain entity
        Inventory inventory = InventoryMapper.getInventoryFromRequest(request);
        Inventory saved = inventoryService.createInventory(inventory);
        return ResponseEntity.ok(InventoryMapper.toResponse(saved));
    }

    @Operation(
            operationId = "updateInventory",
            summary     = "Update inventory item by ID",
            description = "Update the inventory record identified by its ID with new data."
    )
    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> update(
            @PathVariable int id,
            @RequestBody @Valid InventoryRequest request
    ) throws Exception {
        Inventory updated = InventoryMapper.getInventoryFromRequest(request);
        Inventory saved = inventoryService.updateInventory(updated, id);
        return ResponseEntity.ok(InventoryMapper.toResponse(saved));
    }

    @Operation(
            operationId = "deleteInventory",
            summary     = "Delete inventory item by ID",
            description = "Remove the inventory record identified by its ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) throws Exception {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}
