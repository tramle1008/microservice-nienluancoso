package commerce.sbEcommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QRPaymentResponseDTO {
    private Long orderId;
    private String orderCode;
    private Double totalAmount;
    private String qrUrl;
}
