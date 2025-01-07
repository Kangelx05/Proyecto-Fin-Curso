package api.repository;

import api.domain.OrderDetail;
import org.springframework.data.repository.ListCrudRepository;

public interface OrderDetailRepository extends ListCrudRepository<OrderDetail, Integer> {
}