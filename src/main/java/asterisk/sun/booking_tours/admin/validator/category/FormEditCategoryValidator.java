package asterisk.sun.booking_tours.admin.validator.category;

import asterisk.sun.booking_tours.admin.dto.category.FormEditCategoryDTO;
import asterisk.sun.booking_tours.module.category.Category;
import asterisk.sun.booking_tours.module.category.CategoryQueryService;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class FormEditCategoryValidator implements Validator {
    private final CategoryQueryService queryService;

    public FormEditCategoryValidator(CategoryQueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FormEditCategoryDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FormEditCategoryDTO formEditCategoryDTO = (FormEditCategoryDTO) target;

        // Validate name uniqueness (excluding current category)
        if (formEditCategoryDTO.getName() != null && !formEditCategoryDTO.getName().isEmpty()) {
            Optional<Category> existingCategoryByName = queryService.findByName(formEditCategoryDTO.getName());
            if (existingCategoryByName.isPresent() &&
                !existingCategoryByName.get().getId().equals(formEditCategoryDTO.getId())) {
                errors.rejectValue("name", "error.category", "Category name already exists");
            }
        }

        // Validate slug uniqueness (excluding current category)
        if (formEditCategoryDTO.getSlug() != null && !formEditCategoryDTO.getSlug().isEmpty()) {
            Optional<Category> existingCategoryBySlug = queryService.findBySlug(formEditCategoryDTO.getSlug());
            if (existingCategoryBySlug.isPresent() &&
                !existingCategoryBySlug.get().getId().equals(formEditCategoryDTO.getId())) {
                errors.rejectValue("slug", "error.category", "Category slug already exists");
            }
        }
    }
}
