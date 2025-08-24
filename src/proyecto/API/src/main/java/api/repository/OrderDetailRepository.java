package api.repository;

import api.domain.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    /**
     * Finds all order details corresponding to drinks that have not yet been served.
     * <p>
     * A drink is identified by the category on its associated {@link api.domain.CardProduct}.
     * This query returns only those details where the product exists, the category matches
     * one of the accepted drink categories (case insensitive), and {@code servedAt} is null.
     *
     * @return list of {@link OrderDetail} entities pending to be served at the bar
     */
    @Query("SELECT d FROM OrderDetail d " +
           "JOIN d.product p " +
           "WHERE d.servedAt IS NULL AND " +
           "LOWER(p.category) IN ('drinks','drink','bebidas','bebida') " +
           "ORDER BY d.createdAt ASC")
    List<OrderDetail> findPendingDrinkDetails();

    /**
     * Obtiene todas las líneas de pedido de un pedido concreto que corresponden a bebidas
     * pendientes de servir. Se filtra por id de pedido y se ordena por la fecha de creación
     * del pedido para mantener el orden de llegada.
     *
     * @param orderId identificador del pedido
     * @return lista de {@link OrderDetail} con bebidas no servidas para el pedido
     */
    @Query("SELECT d FROM OrderDetail d " +
           "JOIN d.product p " +
           "WHERE d.servedAt IS NULL AND d.order.id = :orderId AND " +
           "LOWER(p.category) IN ('drinks','drink','bebidas','bebida') " +
           "ORDER BY d.createdAt ASC")
    List<OrderDetail> findPendingDrinkDetailsByOrder(@org.springframework.data.repository.query.Param("orderId") Integer orderId);

    /**
     * Finds all order details corresponding to non-drink items that have not yet been served.
     *
     * A non-drink is identified by the category on its associated {@link api.domain.CardProduct} not
     * belonging to the drink categories.  This query returns only those details where the product exists,
     * the category does NOT match any accepted drink category (case insensitive), and {@code servedAt} is null.
     *
     * The results are ordered by the line creation timestamp (createdAt) ascending to reflect
     * the arrival order of each detail.
     *
     * @return list of {@link OrderDetail} entities pending to be prepared in the kitchen
     */
    @Query("SELECT d FROM OrderDetail d " +
           "JOIN d.product p " +
           "WHERE d.servedAt IS NULL AND " +
           "LOWER(p.category) NOT IN ('drinks','drink','bebidas','bebida') " +
           "ORDER BY d.createdAt ASC")
    List<OrderDetail> findPendingKitchenDetails();

    /**
     * Retrieves all order details associated with orders belonging to a specific table.
     *
     * <p>This method returns every {@link OrderDetail} whose parent {@link api.domain.Order}
     * references a {@link api.domain.Table} with the provided identifier.  Both served and
     * unserved items are included.  The results are ordered by the creation timestamp
     * ({@code createdAt}) ascending to reflect the sequence in which items were added.</p>
     *
     * @param tableId the unique identifier of the table
     * @return list of {@link OrderDetail} entities associated with orders for the table
     */
    @Query("SELECT d FROM OrderDetail d WHERE d.order.table.id = :tableId ORDER BY d.createdAt ASC")
    java.util.List<OrderDetail> findByTableId(@org.springframework.data.repository.query.Param("tableId") Integer tableId);

    /**
     * Retrieves all order details for a specific order.  The results are
     * ordered by the creation timestamp so that callers receive the details
     * in the order they were added to the ticket.
     *
     * <p>This method leverages Spring Data JPA's query derivation mechanism to
     * generate the appropriate query based on the method name.  It selects
     * {@link OrderDetail} entities whose parent order has the given
     * identifier and orders them ascending by {@code createdAt}.</p>
     *
     * @param orderId identifier of the parent order
     * @return list of {@link OrderDetail} entities belonging to the order
     */
    java.util.List<OrderDetail> findByOrder_IdOrderByCreatedAtAsc(Integer orderId);

    /**
     * Calculates the total amount of an order by summing the unit prices of all
     * {@link OrderDetail} records associated with the given order identifier.
     * Each {@link OrderDetail} represents a single unit of a product, so the
     * sum of unit prices reflects the order total.
     *
     * @param orderId identifier of the order
     * @return the sum of unit prices for the order or {@code null} if the order has no details
     */
    @org.springframework.data.jpa.repository.Query("SELECT SUM(d.unitPrice) FROM OrderDetail d WHERE d.order.id = :orderId")
    Float sumUnitPriceByOrderId(@org.springframework.data.repository.query.Param("orderId") Integer orderId);
}