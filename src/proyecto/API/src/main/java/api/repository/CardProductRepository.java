package api.repository;

import api.domain.CardProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardProductRepository extends JpaRepository<CardProduct, Integer> {
}