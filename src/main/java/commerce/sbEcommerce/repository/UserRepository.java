package commerce.sbEcommerce.repository;

import commerce.sbEcommerce.model.AppRole;
import commerce.sbEcommerce.model.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long > {
//    dùng Optional để xử lý trường hợp không tìm thấy User
    Optional<User> findByUserName(String userName);
    boolean existsByUserName(String userName);
    boolean existsByEmail( String userName);

    List<User> findByRoles_RoleName(AppRole roleName);

    Optional<User> findByEmail(String email);

}
