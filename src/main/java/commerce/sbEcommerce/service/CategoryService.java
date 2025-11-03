package commerce.sbEcommerce.service;

import commerce.sbEcommerce.model.Category;
import commerce.sbEcommerce.payload.CategoryDTO;
import commerce.sbEcommerce.payload.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    List<CategoryDTO> createbatchCategories(List<CategoryDTO> categoryDTOs);
    String deleteCategory(Long categoryid);
    CategoryDTO updateCategory(CategoryDTO category, Long categoryId);
}
