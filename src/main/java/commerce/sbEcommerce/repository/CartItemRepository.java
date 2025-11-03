package commerce.sbEcommerce.repository;

import commerce.sbEcommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Kiểm tra sản phẩm đã có trong giỏ chưa
    Optional<CartItem> findByProduct_ProductIdAndCart_CartId(Long productId, Long cartId);
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.product.productId = :productId")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);
}
