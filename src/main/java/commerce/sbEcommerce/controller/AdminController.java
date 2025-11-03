package commerce.sbEcommerce.controller;

import commerce.sbEcommerce.config.AppConstants;
import commerce.sbEcommerce.model.AppRole;
import commerce.sbEcommerce.model.DeliveryStatus;
import commerce.sbEcommerce.payload.AdminStatsDTO;
import commerce.sbEcommerce.payload.OrderDTO;
import commerce.sbEcommerce.repository.OrderRepository;
import commerce.sbEcommerce.repository.ProductRepository;
import commerce.sbEcommerce.repository.UserRepository;
import commerce.sbEcommerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

     @Autowired
     private OrderService orderService;
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getStatistics() {
        long totalUsers = userRepository.findByRoles_RoleName(AppRole.ROLE_USER).size();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        double totalRevenue = orderRepository.findTotalRevenue();
        long pendingDeliveries = orderRepository.countPendingDeliveriesWithPayment();

        AdminStatsDTO stats = new AdminStatsDTO();
        stats.setTotalUsers(totalUsers);
        stats.setTotalProducts(totalProducts);
        stats.setTotalOrders(totalOrders);
        stats.setTotalRevenue(totalRevenue);
        stats.setPendingDeliveries(pendingDeliveries);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<OrderDTO>> getOrderPENDING(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE_PENDING) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY_ORDERED) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER_TANG) String sortOrder
    ){
        return new ResponseEntity<>(orderService.getPENDING(pageNumber, pageSize,sortBy,sortOrder), HttpStatus.OK);
    }

    @GetMapping("/shipped")
    public ResponseEntity<Page<OrderDTO>> getSHIPPED(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE_PENDING) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY_ORDERED) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER_TANG) String sortOrder
    ){
        return new ResponseEntity<>(orderService.getPENDING(pageNumber, pageSize,sortBy,sortOrder), HttpStatus.OK);
    }


    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDTO>> getPagedOrders(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE_ORDER) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY_ORDERED) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER_TANG) String sortOrder,
            @RequestParam(required = false) DeliveryStatus status,
            @RequestParam(required = false) String key
    ) {
        return new ResponseEntity<>(
                orderService.getPagedOrderDetails(pageNumber, pageSize, sortBy, sortOrder, status, key),
                HttpStatus.OK
        );
    }



    @PutMapping("/orders/{orderId}/ship")
    public ResponseEntity<OrderDTO> adminMarkShipped(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.adminMarkShipped(orderId), HttpStatus.OK);
    }


}
