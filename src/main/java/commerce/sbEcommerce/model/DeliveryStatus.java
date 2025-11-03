package commerce.sbEcommerce.model;

public enum DeliveryStatus {
    PENDING,    // Chờ xử lý/giao
    REJECTED,      // Bị từ chối giao hàng
    SHIPPED,    // Đang giao
    DELIVERED,  // Giao thành công
    CANCELLED   // Đã bị hủy
}
