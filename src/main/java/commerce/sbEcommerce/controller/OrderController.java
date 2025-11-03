package commerce.sbEcommerce.controller;

import commerce.sbEcommerce.model.Order;
import commerce.sbEcommerce.model.PaymentStatus;
import commerce.sbEcommerce.model.Transaction;
import commerce.sbEcommerce.payload.*;
import commerce.sbEcommerce.repository.OrderRepository;
import commerce.sbEcommerce.repository.TransactionRepository;
import commerce.sbEcommerce.service.OrderService;
import commerce.sbEcommerce.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping("/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(
            @PathVariable String paymentMethod,
            @RequestBody OrderRequestDTO orderRequestDTO
    ) {
        String email = authUtil.getCurrentUserEmail();
        OrderDTO orderDTO;
            orderDTO = orderService.placeOrderWithCOD(email, orderRequestDTO.getAddressId());
        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }

    @PostMapping("/payments/qr")
    public ResponseEntity<QRPaymentResponseDTO> createQRPayment(@RequestBody OrderRequestDTO request) {
        String email = authUtil.getCurrentUserEmail();
        QRPaymentResponseDTO response = orderService.createOrderWithQR(email, request.getAddressId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/payments/sepapy-callback")
    public ResponseEntity<?> handleSePayWebhook(@RequestBody SePayWebhookDTO dto) {
        if (!"in".equalsIgnoreCase(dto.getTransferType())) {
            System.out.println("Đã nhận webhook SePay nhưng loại không hỗ trợ: " + dto.getTransferType());
            return ResponseEntity.ok(Map.of("success", true));
        }
        saveTransaction(dto);

        String orderCode = extractOrderCode(dto.getContent());
        if (orderCode == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Không tìm thấy mã đơn hàng"));
        }

        try {
            orderService.markOrderPaidByCode(orderCode, dto.getTransferAmount(), dto.getReferenceCode());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("success", true));
    }
    private void saveTransaction(SePayWebhookDTO dto) {
        Transaction tx = new Transaction();

        tx.setGateway(dto.getGateway());
        tx.setTransactionDate(Timestamp.valueOf(dto.getTransactionDate())); // ví dụ: "2024-07-25 14:02:37"
        tx.setAccountNumber(dto.getAccountNumber());
        tx.setSubAccount(dto.getSubAccount());

        BigDecimal amount = new BigDecimal(dto.getTransferAmount());
        if ("in".equalsIgnoreCase(dto.getTransferType())) {
            tx.setAmountIn(amount);
            tx.setAmountOut(BigDecimal.ZERO);
        } else {
            tx.setAmountOut(amount);
            tx.setAmountIn(BigDecimal.ZERO);
        }

        tx.setAccumulated(new BigDecimal(dto.getAccumulated()));
        tx.setCode(dto.getCode());
        tx.setTransactionContent(dto.getContent());
        tx.setReferenceNumber(dto.getReferenceCode());
        tx.setBody(dto.getDescription());

        transactionRepository.save(tx);
    }


    private String extractOrderCode(String content) {
        // Regex khớp với "DH" theo sau là dãy số (ví dụ: DH123456)
        Pattern pattern = Pattern.compile("DH(\\d+)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return "DH" + matcher.group(1); // Trả về "DH123456"
        }
        return null;
    }

    @GetMapping("/orders/status/{orderCode}")
    public ResponseEntity<PaymentStatus> paymentStatus(@PathVariable String orderCode) {
        String email = authUtil.getCurrentUserEmail();
        PaymentStatus status = orderRepository.findPaymentStatusByCodeAndEmail(orderCode, email);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/user/order")
    public ResponseEntity<Page<OrderDTO>> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size,
            @RequestParam(defaultValue = "orderId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<OrderDTO> orders = orderService.getUserOrderPaginated(page, size, sortBy, sortDir);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
    @PutMapping("/orders/{orderId}/reject")
    public ResponseEntity<OrderDTO> markRejected(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.serviceMarkRejected(orderId), HttpStatus.OK);
    }
}
