package api.mappers;

import api.domain.CardProduct;
import api.domain.Inventory;
import api.dto.CardProductRequest;
import api.dto.CardProductResponse;
import api.dto.InventoryRequest;
import api.dto.InventoryResponse;

public abstract class CardProductMapper {

    public static void updateInventoryFromRequest(CardProduct cardProduct, CardProductRequest cardProductRequest) {

        cardProduct.setData(cardProductRequest.getData());
        cardProduct.setName(cardProductRequest.getName());
        cardProduct.setPrice(cardProductRequest.getPrice());
        cardProduct.setDescription(cardProductRequest.getDescription());
        cardProduct.setCategory(cardProductRequest.getCategory());

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
