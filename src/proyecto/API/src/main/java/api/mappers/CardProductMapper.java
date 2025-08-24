package api.mappers;

import api.domain.CardProduct;
import api.dto.CardProductRequest;
import api.dto.CardProductResponse;

public abstract class CardProductMapper {

    /**
     * Create a new {@link CardProduct} instance from a {@link CardProductRequest}.
     *
     * <p>
     * This method copies all of the simple scalar fields from the incoming
     * request DTO on to a brand new {@link CardProduct} entity. It is used
     * when creating a brand new card product.
     * </p>
     *
     * @param cardProductRequest the DTO containing user supplied values
     * @return a new populated {@link CardProduct}
     */
    public static CardProduct getInventoryFromRequest(CardProductRequest cardProductRequest) {
        CardProduct cardProduct = new CardProduct();
        // populate a new entity from the request
        cardProduct.setData(cardProductRequest.getData());
        cardProduct.setName(cardProductRequest.getName());
        cardProduct.setPrice(cardProductRequest.getPrice());
        cardProduct.setDescription(cardProductRequest.getDescription());
        cardProduct.setCategory(cardProductRequest.getCategory());
        return cardProduct;
    }

    /**
     * Update an existing {@link CardProduct} instance using values from a
     * {@link CardProductRequest}.
     *
     * <p>
     * Some services were incorrectly passing both the entity and the DTO into
     * this mapper expecting it to mutate the existing entity. To support
     * that pattern this overloaded method was added. It will copy the values
     * from the request on to the provided entity. The entity reference
     * itself is returned for convenience.
     * </p>
     *
     * @param cardProduct        the entity to update
     * @param cardProductRequest the DTO containing new values
     * @return the updated {@link CardProduct}
     */
    public static CardProduct getInventoryFromRequest(CardProduct cardProduct, CardProductRequest cardProductRequest) {
        if (cardProduct == null) {
            cardProduct = new CardProduct();
        }
        cardProduct.setData(cardProductRequest.getData());
        cardProduct.setName(cardProductRequest.getName());
        cardProduct.setPrice(cardProductRequest.getPrice());
        cardProduct.setDescription(cardProductRequest.getDescription());
        cardProduct.setCategory(cardProductRequest.getCategory());
        return cardProduct;
    }

    public static CardProductResponse toResponse(CardProduct cardProduct){
        if (cardProduct == null) return null;
        return new CardProductResponse(
                cardProduct.getId(),
                cardProduct.getName(),
                cardProduct.getData(),
                cardProduct.getDescription(),
                cardProduct.getPrice(),
                cardProduct.getCategory()
        );

    }
}
