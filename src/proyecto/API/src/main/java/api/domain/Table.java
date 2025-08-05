package api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@jakarta.persistence.Table(name = "service_tables")
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id" )
    private Integer id;

    @NotNull
    @Column(name = "num_Table", nullable = false)
    private Integer numTable;

    @NotNull
    @Column(name = "num_Customers", nullable = false)
    private Integer numCustomers;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "waiter_Id", nullable = false)
    private User waiter;

    @Size(max = 45)
    @NotNull
    @Column(name = "state", nullable = false, length = 45)
    private String state;

    @Column(name = "posx")
    private Integer posX;

    @Column(name = "posy")
    private Integer posY;

    public Table() {
        waiter = new User();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumTable() {
        return numTable;
    }

    public void setNumTable(Integer num_Table) {
        this.numTable = num_Table;
    }

    public Integer getNumCustomers() {
        return numCustomers;
    }

    public void setNumCustomers(Integer num_Customers) {
        this.numCustomers = num_Customers;
    }

    public User getWaiter() {
        return waiter;
    }

    public void setWaiter(User waiter) {
        this.waiter = waiter;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getPosX() { return posX;}

    public void setPosX(Integer posX) {this.posX = posX;}

    public Integer getPosY() { return posY;}
    public void setPosY(Integer posY) {this.posY = posY;}

}