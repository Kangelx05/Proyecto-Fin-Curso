package api.domain;

import jakarta.persistence.Table;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "card_products")
public class CardProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Column(name = "Data")
    private byte[] data;

    @Size(max = 100)
    @Column(name = "Description", length = 100)
    private String description;

    @Column(name = "Price")
    private Float price;

    @Column(name = "Category")
    private String category;
}