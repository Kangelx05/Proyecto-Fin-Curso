package api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link api.domain.Product}
 */
public class ProductRequest implements Serializable {
    @NotNull
    @Size(max = 45)
    @NotBlank
    private final String name;
    @NotNull
    @PositiveOrZero
    private final Integer price;
    @Size(max = 45)
    private final String category;
    private final String desc;

    public ProductRequest(String name, Integer price, String category, String desc) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.desc = desc;
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
        ProductRequest entity = (ProductRequest) o;
        return Objects.equals(this.name, entity.name) &&
                Objects.equals(this.price, entity.price) &&
                Objects.equals(this.category, entity.category) &&
                Objects.equals(this.desc, entity.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, category, desc);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "name = " + name + ", " +
                "price = " + price + ", " +
                "category = " + category + ", " +
                "desc = " + desc + ")";
    }
}