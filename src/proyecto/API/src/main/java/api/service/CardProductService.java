package api.service;

import api.domain.CardProduct;
import api.domain.Inventory;
import api.domain.Product;
import api.dto.CardProductRequest;
import api.dto.CardProductResponse;
import api.dto.InventoryRequest;
import api.dto.InventoryResponse;
import api.mappers.CardProductMapper;
import api.mappers.InventoryMapper;
import api.mappers.ProductMapper;
import api.repository.CardProductRepository;
import api.repository.InventoryRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardProductService {


        private final CardProductRepository cardProductRepository;

        public CardProductService(CardProductRepository cardProductRepository){
            this.cardProductRepository = cardProductRepository;
        }

    public CardProductResponse findById(int id) throws Exception{
            CardProduct cardProduct = cardProductRepository.findById(id).orElseThrow();

        return CardProductMapper.toResponse(cardProduct);
    }

        public List<CardProductResponse> findAll() throws Exception{
            List<CardProduct> cardProduct = cardProductRepository.findAll();

            return cardProduct.stream()
                    .map(CardProductMapper::toResponse).toList();
        }

        public CardProductResponse createCardProduct(@NotNull CardProductRequest cardProductRequest) throws Exception {

            CardProduct cardProduct = new CardProduct();
            CardProductMapper.updateInventoryFromRequest(cardProduct, cardProductRequest);

            CardProduct savedCardProduct =cardProductRepository.save(cardProduct);

            return CardProductMapper.toResponse(savedCardProduct);
        }

        public CardProductResponse updateCardProduct(@NotNull CardProductRequest cardProductRequest, int id) throws Exception {

            CardProduct cardProduct = cardProductRepository.findById(id).orElseThrow();

            CardProductMapper.updateInventoryFromRequest(cardProduct, cardProductRequest);
            return CardProductMapper.toResponse(cardProductRepository.save(cardProduct));

        }

        public void deleteCardProduct(int id) throws Exception {
            CardProduct cardProduct = cardProductRepository.findById(id).orElseThrow();
            cardProductRepository.delete(cardProduct);
        }


}
