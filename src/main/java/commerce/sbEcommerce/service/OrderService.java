package commerce.sbEcommerce.service;

import commerce.sbEcommerce.model.DeliveryStatus;
import commerce.sbEcommerce.payload.OrderDTO;
import commerce.sbEcommerce.payload.QRPaymentResponseDTO;
import org.springframework.data.domain.Page;


public interface OrderService {

    OrderDTO placeOrderWithCOD(String emailId, Long addressId);

    OrderDTO placeOrderWithOnlinePayment(String emailId,
                                         Long addressId,
                                         String paymentMethod,
                                         String pgName,
                                         String pgPaymentId,
                                         String pgStatus,
                                         String pgResponseMessage);

    OrderDTO adminMarkShipped(Long orderId);

    OrderDTO delivererMarkDelivered(Long orderId);

    QRPaymentResponseDTO createOrderWithQR(String email, Long addressId);

    void markOrderPaidByCode(String code, Long transferAmount, String referenceCode);

    Page<OrderDTO> getPENDING(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);






    Page<OrderDTO> getOrderSHIPPED(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    OrderDTO serviceMarkRejected(Long orderId);

    Page<OrderDTO> getPagedOrderDetails(int pageNumber, int pageSize, String sortBy, String sortOrder, DeliveryStatus status, String key);

    Page<OrderDTO> getUserOrderPaginated(int page, int size, String sortBy, String sortDir);
}
