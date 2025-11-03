package commerce.sbEcommerce.repository;

import commerce.sbEcommerce.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
