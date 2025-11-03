package commerce.sbEcommerce.service;

import commerce.sbEcommerce.model.*;
import commerce.sbEcommerce.payload.AddressDTO;
import commerce.sbEcommerce.payload.OrderDTO;
import commerce.sbEcommerce.payload.OrderItemDTO;
import commerce.sbEcommerce.payload.QRPaymentResponseDTO;
import commerce.sbEcommerce.repository.*;
import commerce.sbEcommerce.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.Timestamp;

@Service
public class OrderServiceIml implements  OrderService{
    @Autowired
    CartRepository cartRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartService cartService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    AuthUtil authUtil;
    @Override
    @Transactional
    public OrderDTO placeOrderWithCOD(String emailId, Long addressId) {
        return createAndSaveOrder(emailId, addressId, "COD", null, null, null, null);
    }

    @Override
    @Transactional
    public OrderDTO placeOrderWithOnlinePayment(String emailId, Long addressId, String paymentMethod,
                                                String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
        return createAndSaveOrder(emailId, addressId, paymentMethod, pgName, pgPaymentId, pgStatus, pgResponseMessage);
    }



    private OrderDTO createAndSaveOrder(String emailId, Long addressId, String paymentMethod,
                                        String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {

        Cart cart = cartRepository.findByUser_Email(emailId);
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart rỗng");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        Order order = new Order();
        order.setEmail(emailId);
        order.setDateOrder(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        if ("COD".equalsIgnoreCase(paymentMethod)) {
            order.setPaymentStatus(PaymentStatus.UNPAID);
            order.setDeliveryStatus(DeliveryStatus.PENDING); // chưa giao
        } else {
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setDeliveryStatus(DeliveryStatus.PENDING); // vẫn chưa giao
        }

        order.setAddress(address);
        order.setCode(generateOrderCode());

        order.setPaymentMethod(paymentMethod);
        if (!paymentMethod.equalsIgnoreCase("COD")) {
            Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
            payment.setOrder(order);
            paymentRepository.save(payment);
            order.setPayment(payment);
        }

        Order savedOrder = orderRepository.save(order);

        List<CartItem> cartItems = new ArrayList<>(cart.getCartItems());

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setDiscount(cartItem.getDiscount());
            item.setOrderProductPrice(cartItem.getProductPrice());
            return item;
        }).collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);

        // Update product stock & clear cart
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            cart.getCartItems().remove(item);
            item.setCart(null);
        }

        cart.setTotalPrice(0.0);
        cartRepository.save(cart);

        List<OrderItemDTO> itemDTOs = orderItems.stream()
                .map(item -> modelMapper.map(item, OrderItemDTO.class))
                .collect(Collectors.toList());

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderDTO.setOrderItemList(itemDTOs);
        orderDTO.setAddress(modelMapper.map(savedOrder.getAddress(), AddressDTO.class));
        return orderDTO;
    }
    private String generateOrderCode() {
        return "DH" + System.currentTimeMillis();
    }

    public QRPaymentResponseDTO createOrderWithQR(String email, Long addressId) {
        Order order = new Order();
        order.setEmail(email);
        order.setDateOrder(LocalDate.now());
        order.setPaymentMethod("QR");
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setDeliveryStatus(DeliveryStatus.PENDING);
        order.setAddress(addressRepository.findById(addressId).orElseThrow());

        Cart cart = cartRepository.findByUser_Email(email);
        if (cart == null || cart.getCartItems().isEmpty())
            throw new RuntimeException("Cart rỗng");

        double total = cart.getTotalPrice();
        order.setTotalAmount(total);

        String orderCode = generateOrderCode();
        order.setCode(orderCode);

        Order savedOrder = orderRepository.save(order);

        List<CartItem> cartItems = new ArrayList<>(cart.getCartItems());

        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setDiscount(cartItem.getDiscount());
            item.setOrderProductPrice(cartItem.getProductPrice());
            return item;
        }).collect(Collectors.toList());

        orderItemRepository.saveAll(orderItems);


        String qrUrl = generateQrUrl(savedOrder);
        return new QRPaymentResponseDTO(savedOrder.getOrderId(), orderCode, total, qrUrl);
    }

    private String generateQrUrl(Order order) {
        String account = System.getenv("BANK_ACCOUNT");
        String bank = "VietinBank";
        long amount = Math.round(order.getTotalAmount());
        String des = "SEVQR."+ order.getCode();
                            //https://qr.sepay.vn/img?acc=%s&bank=%s&amount=%d&des=%s&template=compact&download=false
        return String.format("https://qr.sepay.vn/img?acc=%s&bank=%s&amount=%s&des=%s",
                account, bank, amount, des);
    }


    @Override
    @Transactional
    public void markOrderPaidByCode(String code, Long amount, String referenceCode) {
        Order order = orderRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (order.getPaymentStatus() == PaymentStatus.PAID) return;

        // Kiểm tra số tiền khớp
        if (!order.getTotalAmount().equals(amount.doubleValue())) {
            throw new RuntimeException("Số tiền không khớp với đơn hàng");
        }

        // Cập nhật trạng thái đơn hàng
        order.setPaymentStatus(PaymentStatus.PAID);
        //xoa do hang
        Cart cart = cartRepository.findByUser_Email(order.getEmail());
        if (cart != null) {
            List<CartItem> cartItems = new ArrayList<>(cart.getCartItems());

            for (CartItem item : cartItems) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() - item.getQuantity());
                productRepository.save(product);

                cart.getCartItems().remove(item);
                item.setCart(null);
            }

            cart.setTotalPrice(0.0);
            cartRepository.save(cart);
        }
        // Nếu đã có payment thì cập nhật lại
        Payment payment = order.getPayment();
        if (payment == null) {
            payment = new Payment("QR", referenceCode, "success", "Webhook SePay", "SePay");
            payment.setOrder(order);
        } else {
            payment.setPgPaymentId(referenceCode);
            payment.setPgStatus("success");
            payment.setPgResponseMessage("Webhook SePay");
            payment.setPgName("SePay");
        }

        paymentRepository.save(payment);
        order.setPayment(payment);
        orderRepository.save(order);

        Transaction tx = new Transaction();
        tx.setCode(code);
        tx.setTransactionContent(code);
        tx.setAmountIn(BigDecimal.valueOf(amount));
        tx.setTransactionDate(new Timestamp(System.currentTimeMillis()));
        tx.setReferenceNumber(referenceCode);
        tx.setGateway("SePay");
        transactionRepository.save(tx);
    }


    /// //
    @Override
    public Page<OrderDTO> getPENDING(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Order> ordersPage = orderRepository.findByDeliveryStatus(DeliveryStatus.PENDING, pageable);
        List<OrderDTO> dtos = new ArrayList<>();

        for (Order order : ordersPage.getContent()) {
            boolean isValid = (
                    (order.getPaymentMethod().equalsIgnoreCase("COD") && order.getPaymentStatus() == PaymentStatus.UNPAID)
                            ||
                            (!order.getPaymentMethod().equalsIgnoreCase("COD") && order.getPaymentStatus() == PaymentStatus.PAID)
            );

            if (!isValid) continue;

            OrderDTO dto = modelMapper.map(order, OrderDTO.class);

            User user = userRepository.findByEmail(order.getEmail()).orElse(null);
            if (user != null) {
                dto.setUserName(user.getUserName());
            }

            List<OrderItemDTO> itemDTOs = order.getOrderItemList().stream()
                    .map(item -> modelMapper.map(item, OrderItemDTO.class))
                    .collect(Collectors.toList());
            dto.setOrderItemList(itemDTOs);

            dto.setAddress(modelMapper.map(order.getAddress(), AddressDTO.class));

            dtos.add(dto);
        }


        return new PageImpl<>(
                dtos,
                pageable,
                dtos.size()  // ⚠ Nếu cần tổng toàn bộ thì tính lại bằng query khác
        );
    }


    @Override
    public Page<OrderDTO> getPagedOrderDetails(int pageNumber, int pageSize, String sortBy, String sortOrder, DeliveryStatus status, String key) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Specification<Order> spec = Specification.where(
                OrderSpecifications.hasStatus(status)
        ).and(
                OrderSpecifications.containsKey(key)
        );

        Page<Order> orderPage = orderRepository.findAll(spec, pageable);

        return orderPage.map(order -> {
            OrderDTO dto = modelMapper.map(order, OrderDTO.class);

            // Gán username
            userRepository.findByEmail(order.getEmail())
                    .ifPresent(user -> dto.setUserName(user.getUserName()));

            // Gán danh sách item
            List<OrderItemDTO> itemDTOs = order.getOrderItemList().stream()
                    .map(item -> modelMapper.map(item, OrderItemDTO.class))
                    .collect(Collectors.toList());
            dto.setOrderItemList(itemDTOs);

            // Gán địa chỉ
            dto.setAddress(modelMapper.map(order.getAddress(), AddressDTO.class));

            return dto;
        });
    }



    public class OrderSpecifications {
        public static Specification<Order> hasStatus(DeliveryStatus status) {
            return (root, query, cb) -> status == null ? null : cb.equal(root.get("deliveryStatus"), status);
        }

        public static Specification<Order> containsKey(String key) {
            return (root, query, cb) -> {
                if (key == null || key.isEmpty()) return null;
                String pattern = "%" + key.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("orderId")), pattern), // mã đơn
                        cb.like(cb.lower(root.get("userName")), pattern) // email người đặt
                );
            };
        }
    }

    @Override
    @Transactional
    public OrderDTO adminMarkShipped(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (order.getDeliveryStatus() != DeliveryStatus.PENDING) {
            throw new RuntimeException("Hiện trạng thái đơn hàng không là PENDING");
        }

        order.setDeliveryStatus(DeliveryStatus.SHIPPED);
        orderRepository.save(order);

        OrderDTO dto = modelMapper.map(order, OrderDTO.class);

        dto.setOrderItemList(
                order.getOrderItemList().stream()
                        .map(item -> modelMapper.map(item, OrderItemDTO.class))
                        .collect(Collectors.toList())
        );


        dto.setAddress(modelMapper.map(order.getAddress(), AddressDTO.class));
        return dto;
    }

    @Override
    @Transactional
    public OrderDTO delivererMarkDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm được đơn hàng"));

        if (order.getDeliveryStatus() != DeliveryStatus.SHIPPED) {
            throw new RuntimeException("Hiện trạng thái đơn hàng không là SHIPPED");
        }

        // Nếu là COD, cập nhật trạng thái thanh toán
        if (order.getPaymentStatus() == PaymentStatus.UNPAID) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }

        // Cập nhật trạng thái giao hàng
        order.setDeliveryStatus(DeliveryStatus.DELIVERED);
        orderRepository.save(order);

        OrderDTO dto = modelMapper.map(order, OrderDTO.class);

        dto.setOrderItemList(
                order.getOrderItemList().stream()
                        .map(item -> modelMapper.map(item, OrderItemDTO.class))
                        .collect(Collectors.toList())
        );

        dto.setAddress(modelMapper.map(order.getAddress(), AddressDTO.class));
        return dto;
    }

    @Override
    public Page<OrderDTO> getUserOrderPaginated(int page, int size, String sortBy, String sortDir) {
        String userEmail = authUtil.getCurrentUserEmail();

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> ordersPage = orderRepository.findByEmail(userEmail, pageable);

        return ordersPage.map(order -> {
            OrderDTO dto = modelMapper.map(order, OrderDTO.class);
            dto.setUserName(userRepository.findByEmail(userEmail).get().getUserName());
            return dto;
        });
    }


    @Override
    public Page<OrderDTO> getOrderSHIPPED(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Lấy tất cả đơn hàng có trạng thái SHIPPED
        Page<Order> ordersPage = orderRepository.findByDeliveryStatus(DeliveryStatus.SHIPPED, pageable);
        List<OrderDTO> dtos = new ArrayList<>();

        for (Order order : ordersPage.getContent()) {
            OrderDTO dto = modelMapper.map(order, OrderDTO.class);

            User user = userRepository.findByEmail(order.getEmail()).orElse(null);
            if (user != null) {
                dto.setUserName(user.getUserName());
            }

            List<OrderItemDTO> itemDTOs = order.getOrderItemList().stream()
                    .map(item -> modelMapper.map(item, OrderItemDTO.class))
                    .collect(Collectors.toList());
            dto.setOrderItemList(itemDTOs);

            dto.setAddress(modelMapper.map(order.getAddress(), AddressDTO.class));

            dtos.add(dto);
        }

        return new PageImpl<>(dtos, pageable, ordersPage.getTotalElements());
    }

    @Override
    public OrderDTO serviceMarkRejected(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        if (order.getDeliveryStatus() != DeliveryStatus.PENDING) {
            throw new RuntimeException("Hiện trạng thái đơn hàng không là PENDING");
        }

        order.setDeliveryStatus(DeliveryStatus.REJECTED);
        order.setTotalAmount(0.0);
        orderRepository.save(order);

        OrderDTO dto = modelMapper.map(order, OrderDTO.class);

        dto.setOrderItemList(
                order.getOrderItemList().stream()
                        .map(item -> modelMapper.map(item, OrderItemDTO.class))
                        .collect(Collectors.toList())
        );


        dto.setAddress(modelMapper.map(order.getAddress(), AddressDTO.class));
        return dto;
    }


}
