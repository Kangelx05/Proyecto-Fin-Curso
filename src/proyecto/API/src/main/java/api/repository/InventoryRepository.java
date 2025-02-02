package api.repository;

import api.domain.Inventory;
import api.domain.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    Optional<Inventory> findByProduct(@NotNull Product product);
}