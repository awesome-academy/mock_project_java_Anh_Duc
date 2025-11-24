
package asterisk.sun.booking_tours.application.admin.category;

import java.util.List;

import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.admin.category.dto.FormEditCategoryDTO;
import asterisk.sun.booking_tours.application.admin.category.dto.FormCreateCategoryDTO;
import asterisk.sun.booking_tours.application.admin.category.dto.ListCategoryDTO;
import asterisk.sun.booking_tours.application.admin.common.BaseServiceController;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.category.Category;
import asterisk.sun.booking_tours.core.category.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryAdminService extends BaseServiceController<CategoryRepository> {

    public CategoryAdminService(CategoryRepository categoryRepository) {
        super(categoryRepository);
    }

    public List<ListCategoryDTO> queryCategoriesByKeyword(String keyword) {
        return MapperHelper.mapList(repository.searchByKeyword(keyword), ListCategoryDTO.class);
    }

    public void createCategory(FormCreateCategoryDTO formCreateCategoryDTO) {
        Category category = MapperHelper.map(formCreateCategoryDTO, Category.class);
        repository.save(category);
    }

    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    public boolean existsBySlug(String slug) {
        return repository.existsBySlug(slug);
    }

    public FormEditCategoryDTO getCategoryById(Long id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        return MapperHelper.map(category, FormEditCategoryDTO.class);
    }

    public void updateCategory(FormEditCategoryDTO formEditCategoryDTO) {
        Category category = repository.findById(formEditCategoryDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Category not found with id: " + formEditCategoryDTO.getId()));

        category.setName(formEditCategoryDTO.getName());
        category.setDescription(formEditCategoryDTO.getDescription());
        category.setSlug(formEditCategoryDTO.getSlug());

        repository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));

        repository.delete(category);
    }
}
