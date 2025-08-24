package api.repository;

import api.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Find an order for a given table and state.  This is used to enforce that
     * there is at most one open (IN_SERVICE) order per table.  The method
     * relies on JPA's query derivation mechanism which translates the method
     * name into a query on the joined table id and state columns.
     *
     * @param tableId the identifier of the table
     * @param state   the order state (e.g. "IN_SERVICE" or "PAID")
     * @return an optional containing the matching order, or empty if none
     */
    java.util.Optional<Order> findByTable_IdAndState(Integer tableId, String state);
}