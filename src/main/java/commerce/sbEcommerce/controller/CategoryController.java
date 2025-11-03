package commerce.sbEcommerce.controller;

import commerce.sbEcommerce.config.AppConstants;
import commerce.sbEcommerce.payload.CategoryDTO;
import commerce.sbEcommerce.payload.CategoryResponse;
import commerce.sbEcommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;


    //    @RequestMapping(value = "/api/public/categories", method = RequestMethod.GET )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/public/categories")
    public ResponseEntity<CategoryResponse> getCategoryList(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE_ALL) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY_CATEGORYID) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER_TANG) String sortOrder
    ) {
        CategoryResponse categoryList = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryList, HttpStatus.OK);
    }

    @PostMapping("/api/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO saveCategoryDTO=  categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(saveCategoryDTO, HttpStatus.CREATED);
    }

    @PostMapping("/api/public/categories/batch")
    public ResponseEntity<List<CategoryDTO>> createCategories(@Valid @RequestBody List<CategoryDTO> categoryDTOs) {
        List<CategoryDTO> savedCategories = categoryService.createbatchCategories(categoryDTOs);
        return new ResponseEntity<>(savedCategories, HttpStatus.CREATED);
    }

    @DeleteMapping("/api/admin/categories/{categoryid}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryid) {
            String flag = categoryService.deleteCategory(categoryid);
            return  new ResponseEntity<>(flag, HttpStatus.OK);
    }

    @PutMapping("/api/admin/categories/{categoryid}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                      @PathVariable("categoryid") Long categoryId) {
        CategoryDTO updatedCategoryDTO = categoryService.updateCategory(categoryDTO, categoryId);
        return new ResponseEntity<>(updatedCategoryDTO, HttpStatus.OK);
    }



}
