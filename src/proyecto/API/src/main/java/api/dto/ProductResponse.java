package api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link api.domain.Product}
 */
public class ProductResponse implements Serializable {
    private final Integer id;
    @NotNull
    @Size(max = 45)
    private final String name;
    @NotNull
    private final Integer price;
    @Size(max = 45)
    private final String category;
    private final String desc;

    public ProductResponse(Integer id, String name, Integer price, String category, String desc) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductResponse entity = (ProductResponse) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.name, entity.name) &&
                Objects.equals(this.price, entity.price) &&
                Objects.equals(this.category, entity.category) &&
                Objects.equals(this.desc, entity.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, category, desc);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "price = " + price + ", " +
                "category = " + category + ", " +
                "desc = " + desc + ")";
    }
}