package api.mappers;

import api.domain.Product;
import api.dto.ProductRequest;
import api.dto.ProductResponse;

public abstract class ProductMapper {

    /**
     * Create a new {@link Product} from the incoming DTO.  This method is
     * used when initially persisting a product record.
     *
     * @param productRequest the DTO containing name, price, category and description
     * @return a new {@link Product}
     */
    public static Product getProductFromRequest(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.name());
        product.setPrice(productRequest.price());
        product.setCategory(productRequest.category());
        product.setDesc(productRequest.desc());
        return product;
    }

    /**
     * Update an existing {@link Product} in place using values from the DTO.
     * This overload allows the caller to mutate an entity rather than
     * constructing a new instance.
     *
     * @param product        the entity to update
     * @param productRequest the DTO containing new values
     * @return the updated {@link Product}
     */
    public static Product getProductFromRequest(Product product, ProductRequest productRequest) {
        if (product == null) {
            product = new Product();
        }
        product.setName(productRequest.name());
        product.setPrice(productRequest.price());
        product.setCategory(productRequest.category());
        product.setDesc(productRequest.desc());
        return product;
    }

    public static ProductResponse toResponse(Product product) {
        if (product == null) return null;
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory(),
                product.getDesc()
        );
    }
}

