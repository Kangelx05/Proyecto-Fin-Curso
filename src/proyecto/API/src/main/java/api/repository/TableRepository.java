package api.repository;

import api.domain.Table;
import org.springframework.data.repository.ListCrudRepository;

public interface TableRepository extends ListCrudRepository<Table, Integer> {
}