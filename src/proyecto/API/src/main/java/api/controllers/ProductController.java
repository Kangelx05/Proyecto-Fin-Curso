package api.controllers;

import api.domain.Product;
import api.dto.ProductRequest;
import api.dto.ProductResponse;
import api.mappers.ProductMapper;
import api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(
            operationId = "getProductById",
            summary     = "Get product by ID",
            description = "Retrieve a product record by its unique identifier."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable int id) throws Exception {
        Product product = productService.findById(id);
        return ResponseEntity.ok(ProductMapper.toResponse(product));
    }

    @Operation(
            operationId = "createProduct",
            summary     = "Create a new product",
            description = "Create a new product entry with the provided details."
    )
    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody @Valid ProductRequest request) throws Exception {
        // map incoming DTO into a domain entity
        Product product = ProductMapper.getProductFromRequest(request);
        Product created = productService.createProduct(product);
        return ResponseEntity.ok(ProductMapper.toResponse(created));
    }

    @Operation(
            operationId = "updateProduct",
            summary     = "Update product by ID",
            description = "Update the product record identified by its ID with new data."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable int id,
            @RequestBody @Valid ProductRequest request
    ) throws Exception {
        // map incoming DTO to a domain entity
        Product updated = ProductMapper.getProductFromRequest(request);
        Product result = productService.updateProduct(updated, id);
        return ResponseEntity.ok(ProductMapper.toResponse(result));
    }

    @Operation(
            operationId = "deleteProduct",
            summary     = "Delete product by ID",
            description = "Remove the product record identified by its ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) throws Exception {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}