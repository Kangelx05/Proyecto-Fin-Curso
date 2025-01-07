package api.repository;

import api.domain.OrderStateHistory;
import org.springframework.data.repository.ListCrudRepository;

public interface OrderStateHistoryRepository extends ListCrudRepository<OrderStateHistory, Integer> {
}