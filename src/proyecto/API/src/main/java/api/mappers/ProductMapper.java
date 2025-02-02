package api.mappers;

import api.domain.Product;
import api.dto.ProductRequest;
import api.dto.ProductResponse;

public abstract class ProductMapper {

    public static void updateProductFromRequest(Product product, ProductRequest productRequest) {

        product.setName(productRequest.name());
        product.setPrice(productRequest.price());
        product.setCategory(productRequest.category());
        product.setDesc(productRequest.desc());

    }

    public static ProductResponse toResponse(Product product){
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

