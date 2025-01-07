package api.repository;

import api.domain.Inventory;
import org.springframework.data.repository.ListCrudRepository;

public interface InventoryRepository extends ListCrudRepository<Inventory, Integer> {
  }