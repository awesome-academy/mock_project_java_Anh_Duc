
package asterisk.sun.booking_tours.application.admin.category;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import asterisk.sun.booking_tours.application.admin.category.dto.FormEditCategoryDTO;
import asterisk.sun.booking_tours.application.admin.category.dto.CategorySearchRequestDTO;
import asterisk.sun.booking_tours.application.admin.category.dto.FormCreateCategoryDTO;
import asterisk.sun.booking_tours.application.admin.category.dto.ListCategoryDTO;
import asterisk.sun.booking_tours.application.admin.common.BaseServiceController;
import asterisk.sun.booking_tours.common.helper.MapperHelper;
import asterisk.sun.booking_tours.core.category.Category;
import asterisk.sun.booking_tours.core.category.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;

@Service
public class CategoryAdminService extends BaseServiceController<CategoryRepository> {

    public CategoryAdminService(CategoryRepository categoryRepository) {
        super(categoryRepository);
    }

    public Page<ListCategoryDTO> queryCategoriesByKeyword(CategorySearchRequestDTO request) {
        Pageable pageable = request.getPageable();

        // Tạo Specification (Bộ lọc động)
        Specification<Category> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo Keyword (nếu có)
            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                String likeKey = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), likeKey),
                        cb.like(cb.lower(root.get("description")), likeKey)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Category> pageResult = repository.findAll(spec, pageable);

        return pageResult.map(category -> MapperHelper.map(category, ListCategoryDTO.class));
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
