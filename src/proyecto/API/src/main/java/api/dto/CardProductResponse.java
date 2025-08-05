package api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link api.domain.CardProduct}
 */
@Value
public class CardProductResponse implements Serializable {
    Integer id;
    @NotNull
    @Size(max = 100)
    String name;
    byte[] data;
    @Size(max = 100)
    String description;
    Float price;
    String category;
}