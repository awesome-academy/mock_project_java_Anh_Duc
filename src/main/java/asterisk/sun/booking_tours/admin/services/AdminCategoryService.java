package asterisk.sun.booking_tours.admin.services;

import java.util.List;

import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.admin.dto.category.FormCreateCategoryDTO;
import asterisk.sun.booking_tours.admin.dto.category.FormEditCategoryDTO;
import asterisk.sun.booking_tours.admin.dto.category.ListCategoryDTO;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.module.category.Category;
import asterisk.sun.booking_tours.module.category.CategoryService;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AdminCategoryService {
    private final CategoryService categoryService;

    public AdminCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public List<ListCategoryDTO> queryCategoriesByKeyword(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return MapperHelper.mapList(categoryService.searchByKeyword(keyword), ListCategoryDTO.class);
        }

        return MapperHelper.mapList(categoryService.findAll(), ListCategoryDTO.class);
    }

    public void createCategory(FormCreateCategoryDTO formCreateCategoryDTO) {

        Category category = new Category();
        category.setName(formCreateCategoryDTO.getName());
        category.setDescription(formCreateCategoryDTO.getDescription());
        category.setSlug(formCreateCategoryDTO.getSlug());

        categoryService.save(category);
    }

    public FormEditCategoryDTO getCategoryById(Long id) {
        Category category = categoryService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        return MapperHelper.map(category, FormEditCategoryDTO.class);
    }

    public void updateCategory(FormEditCategoryDTO formEditCategoryDTO) {
        Category category = categoryService.findById(formEditCategoryDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category not found with id: " + formEditCategoryDTO.getId()));

        category.setName(formEditCategoryDTO.getName());
        category.setDescription(formEditCategoryDTO.getDescription());
        category.setSlug(formEditCategoryDTO.getSlug());

        categoryService.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        categoryService.delete(category);
    }
}
