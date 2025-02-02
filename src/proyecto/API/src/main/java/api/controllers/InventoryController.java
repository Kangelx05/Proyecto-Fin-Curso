package api.controllers;

import api.dto.InventoryRequest;
import api.dto.InventoryResponse;
import api.service.InventoryService;
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

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getById(@PathVariable int id) throws Exception {
        return ResponseEntity.ok(inventoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> create(@RequestBody @Valid InventoryRequest request) throws Exception{
        return ResponseEntity.ok(inventoryService.createInventory(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> update(@PathVariable int id, @RequestBody @Valid InventoryRequest request) throws Exception {
        return ResponseEntity.ok(inventoryService.updateInventory(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) throws Exception {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}