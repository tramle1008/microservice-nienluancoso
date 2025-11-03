package commerce.sbEcommerce.repository;

//Crud

import commerce.sbEcommerce.model.Category;
import commerce.sbEcommerce.payload.CategoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByCategoryName(String category);
}
