package api.repository;

import api.domain.OrderStateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderStateHistoryRepository extends JpaRepository<OrderStateHistory, Integer> {
}