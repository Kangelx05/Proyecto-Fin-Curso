package api.service;

import api.domain.CardProduct;
// DTOs and mappers are intentionally not used in the service layer.  They
// remain imported elsewhere for controller mapping only.
import api.repository.CardProductRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardProductService {


        private final CardProductRepository cardProductRepository;

        public CardProductService(CardProductRepository cardProductRepository){
            this.cardProductRepository = cardProductRepository;
        }

    /**
     * Retrieve a card product by id.
     *
     * @param id the primary key
     * @return the {@link CardProduct} entity
     * @throws Exception if no record exists
     */
    public CardProduct findById(int id) throws Exception {
        return cardProductRepository.findById(id).orElseThrow();
    }

    /**
     * Retrieve all card products.
     *
     * @return a list of {@link CardProduct} entities
     */
    public List<CardProduct> findAll() {
        return cardProductRepository.findAll();
    }

    /**
     * Persist a new card product.
     *
     * @param cardProduct entity containing the data to persist
     * @return the persisted {@link CardProduct}
     */
    public CardProduct createCardProduct(@NotNull CardProduct cardProduct) {
        return cardProductRepository.save(cardProduct);
    }

    /**
     * Update an existing card product with new values.
     *
     * @param updated contains the new values
     * @param id      identifier of the record to update
     * @return the updated {@link CardProduct}
     * @throws Exception if the record does not exist
     */
    public CardProduct updateCardProduct(@NotNull CardProduct updated, int id) throws Exception {
        CardProduct existing = cardProductRepository.findById(id).orElseThrow();
        existing.setName(updated.getName());
        existing.setData(updated.getData());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setCategory(updated.getCategory());
        return cardProductRepository.save(existing);
    }

    /**
     * Remove a card product by id.
     *
     * @param id the primary key
     * @throws Exception if the record does not exist
     */
    public void deleteCardProduct(int id) throws Exception {
        CardProduct cardProduct = cardProductRepository.findById(id).orElseThrow();
        cardProductRepository.delete(cardProduct);
    }


}
