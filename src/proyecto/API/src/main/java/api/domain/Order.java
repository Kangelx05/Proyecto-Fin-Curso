package api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Entity
// Escape the table name because "order" is a reserved keyword in MySQL.  Using backticks
// ensures Hibernate generates SQL like `order` instead of unquoted identifiers.
@jakarta.persistence.Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "table_id", nullable = false)
    private Table table;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @Size(max = 45)
    @NotNull
    @Column(name = "state", nullable = false, length = 45)
    private String state;

    /**
     * Timestamp when the order has been delivered to the customer.  This field
     * is populated when the order is marked as finished.  It may be null
     * until the order is paid and closed.
     */
    @Column(name = "delivered_at")
    private java.time.Instant deliveredAt;

    /**
     * Total amount of the order in the currency units (EUR).  The total is
     * calculated by summing the unit prices of all {@link OrderDetail}
     * entries associated with this order when it is marked as finished.
     */
    @Column(name = "total_amount")
    private Float totalAmount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * Returns the timestamp when the order was delivered to the customer.
     *
     * @return the delivery instant or {@code null} if not yet delivered
     */
    public java.time.Instant getDeliveredAt() {
        return deliveredAt;
    }

    /**
     * Sets the delivery timestamp for this order.  Typically this is set
     * when the order is marked as finished.  Passing {@code null} will
     * clear the existing value.
     *
     * @param deliveredAt the instant when the order was delivered
     */
    public void setDeliveredAt(java.time.Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    /**
     * Returns the total amount of the order.  The total amount is the sum of
     * the unit prices of all associated {@link OrderDetail} records.  It may
     * be {@code null} until the order is finalized.
     *
     * @return the total amount or {@code null}
     */
    public Float getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the total amount for this order.  Typically this value is
     * calculated when the order is finished and should not be manually
     * modified thereafter.
     *
     * @param totalAmount the total amount to assign
     */
    public void setTotalAmount(Float totalAmount) {
        this.totalAmount = totalAmount;
    }

}