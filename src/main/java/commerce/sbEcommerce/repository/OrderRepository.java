package commerce.sbEcommerce.repository;

import commerce.sbEcommerce.model.DeliveryStatus;
import commerce.sbEcommerce.model.Order;
import commerce.sbEcommerce.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.paymentStatus = 'PAID'")
    double findTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.deliveryStatus = 'PENDING'")
    long countPendingDeliveries();

    @Query(value = """
    SELECT COUNT(*) FROM orders
    WHERE delivery_status = 'PENDING'
    AND (
        payment_method <> 'QR'
        OR (payment_method = 'QR' AND payment_status <> 'UNPAID')
    )
    """,nativeQuery = true
    )
    long countPendingDeliveriesWithPayment();

    Optional<Order> findByCode(String code);


    Page<Order> findByEmail(String email, Pageable pageable);

    Page<Order> findByDeliveryStatus(DeliveryStatus status, Pageable pageable);


    List<Order> findByEmail(String userEmail);

    @Query("SELECT o.paymentStatus FROM Order o WHERE o.code = :code AND o.email = :email")
    PaymentStatus findPaymentStatusByCodeAndEmail(String code, String email);

}
