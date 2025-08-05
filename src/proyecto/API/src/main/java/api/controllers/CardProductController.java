package api.controllers;

import api.dto.CardProductRequest;
import api.dto.CardProductResponse;
import api.dto.InventoryRequest;
import api.dto.InventoryResponse;
import api.service.CardProductService;
import api.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
    @RequestMapping("/cardProduct")
    public class CardProductController {

        private final CardProductService cardProductService;

        public CardProductController(CardProductService cardProductService) {
            this.cardProductService = cardProductService;
        }

    @Operation(
            operationId = "createInventory",
            summary     = "Create a new inventory record",
            description = "Create a new inventory entry with the provided details."
    )
    @GetMapping("/{id}")
    public ResponseEntity<CardProductResponse> findById(@PathVariable int id) throws Exception {
        return ResponseEntity.ok(cardProductService.findById(id));
    }

        @Operation(
                operationId = "createInventory",
                summary     = "Create a new inventory record",
                description = "Create a new inventory entry with the provided details."
        )
        @GetMapping
        public ResponseEntity<List<CardProductResponse>> findAll() throws Exception {
            return ResponseEntity.ok(cardProductService.findAll());
        }

        @Operation(
                operationId = "createInventory",
                summary     = "Create a new inventory record",
                description = "Create a new inventory entry with the provided details."
        )
        @PostMapping
        public ResponseEntity<CardProductResponse> create(@RequestBody @Valid CardProductRequest request) throws Exception {
            return ResponseEntity.ok(cardProductService.createCardProduct(request));
        }

        @Operation(
                operationId = "updateInventory",
                summary     = "Update inventory item by ID",
                description = "Update the inventory record identified by its ID with new data."
        )
        @PutMapping("/{id}")
        public ResponseEntity<CardProductResponse> update(
                @PathVariable int id,
                @RequestBody @Valid CardProductRequest request
        ) throws Exception {
            return ResponseEntity.ok(cardProductService.updateCardProduct(request, id));
        }

        @Operation(
                operationId = "deleteInventory",
                summary     = "Delete inventory item by ID",
                description = "Remove the inventory record identified by its ID."
        )
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(@PathVariable int id) throws Exception {
            cardProductService.deleteCardProduct(id);
            return ResponseEntity.noContent().build();
        }
    }
