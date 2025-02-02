package api.service;

import api.domain.Product;
import api.dto.ProductRequest;
import api.dto.ProductResponse;
import api.mappers.ProductMapper;
import api.repository.ProductRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public ProductResponse findById(int id) throws Exception {
        Product product = productRepository.findById(id).orElseThrow();
        return ProductMapper.toResponse(product);
    }

    public ProductResponse createProduct(@NotNull ProductRequest requestedProduct) throws Exception {
        if (productRepository.findByNameIgnoreCase(requestedProduct.name()).isPresent()) {
            throw new Exception("Product with name " + requestedProduct.name() + " already exists.");
        }
        Product product = new Product();
        ProductMapper.updateProductFromRequest(product, requestedProduct);
        Product savedProduct = productRepository.save(product);

        return ProductMapper.toResponse(savedProduct);
    }

    public ProductResponse updateProduct(@NotNull ProductRequest requestedProduct, int id) throws Exception {
        Product product = productRepository.findById(id).orElseThrow();
        ProductMapper.updateProductFromRequest(product, requestedProduct);
        return ProductMapper.toResponse(productRepository.save(product));
    }

    public void deleteProduct(int id) throws Exception {
        Product product = productRepository.findById(id).orElseThrow();
        productRepository.delete(product);
    }
}
