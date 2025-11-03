package commerce.sbEcommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class CartDTO {
    private Long CartId;
    private Double totalPrice = 0.0;
    private List<ProductDTO> products = new ArrayList<>();

}
