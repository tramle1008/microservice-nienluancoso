package commerce.sbEcommerce.payload;

import commerce.sbEcommerce.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long paymentId ;

    private String paymentMethod;


    private String pgPaymentId;
    private String pgStatus;
    private String pgResponseMessage;
    private String pgName;

    private String email;
    private Long addressId;
}
