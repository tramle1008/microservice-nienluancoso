package commerce.sbEcommerce.util;

import commerce.sbEcommerce.exceptioons.UnauthorizedException;
import commerce.sbEcommerce.model.User;
import commerce.sbEcommerce.repository.UserRepository;
import commerce.sbEcommerce.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    @Autowired
    private UserRepository userRepository;

    // Lấy ID từ UserDetailsImpl trong context
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("Người dùng chưa đăng nhập hoặc phiên đã hết hạn");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw new UnauthorizedException("Không thể xác thực người dùng"); // hoặc dùng lại UnauthorizedException    }

    }
    // Lấy user entity từ DB
    public User getCurrentUserEntity() {
        Long userId = getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
    }

    // Lấy email người dùng đang đăng nhập
    public String getCurrentUserEmail() {

        return getCurrentUserEntity().getEmail();
    }

    // Lấy username người dùng đang đăng nhập
    public String getCurrentUsername() {
        return getCurrentUserEntity().getUserName();
    }



}

