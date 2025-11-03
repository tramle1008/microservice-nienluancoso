package commerce.sbEcommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class CartItemDTO {
    private Long CartItemId;
    private CartDTO cart;
    private ProductDTO productDTO;

    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
