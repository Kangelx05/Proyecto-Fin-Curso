package api.service;

import api.domain.Product;
// Services should operate on domain entities exclusively.  DTOs and mappers
// are used only in the controller layer for input/output mapping.
import api.repository.ProductRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    /**
     * Retrieve a product by its identifier.
     *
     * @param id the primary key of the product
     * @return the corresponding {@link Product}
     * @throws Exception if no product exists with the provided id
     */
    public Product findById(int id) throws Exception {
        return productRepository.findById(id).orElseThrow();
    }

    /**
     * Persist a new {@link Product}.  Services should supply a fully
     * populated entity; this method will enforce uniqueness on the
     * product name and persist the entity.
     *
     * @param product the product entity to create
     * @return the persisted {@link Product}
     * @throws Exception if a product with the same name already exists
     */
    public Product createProduct(@NotNull Product product) throws Exception {
        if (productRepository.findByNameIgnoreCase(product.getName()).isPresent()) {
            throw new Exception("Product with name " + product.getName() + " already exists.");
        }
        return productRepository.save(product);
    }

    /**
     * Update an existing {@link Product} with new values.  Only simple
     * scalar fields (name, price, category, description) are updated.
     *
     * @param updatedProduct the incoming entity with desired values
     * @param id             the identifier of the product to update
     * @return the updated {@link Product}
     * @throws Exception if no product exists with the given id
     */
    public Product updateProduct(@NotNull Product updatedProduct, int id) throws Exception {
        Product product = productRepository.findById(id).orElseThrow();
        product.setName(updatedProduct.getName());
        product.setPrice(updatedProduct.getPrice());
        product.setCategory(updatedProduct.getCategory());
        product.setDesc(updatedProduct.getDesc());
        return productRepository.save(product);
    }

    /**
     * Delete a product by its identifier.
     *
     * @param id the primary key of the product to delete
     * @throws Exception if the product does not exist
     */
    public void deleteProduct(int id) throws Exception {
        Product product = productRepository.findById(id).orElseThrow();
        productRepository.delete(product);
    }
}
