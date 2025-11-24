package asterisk.sun.booking_tours.admin.validator.category;

import asterisk.sun.booking_tours.admin.dto.category.FormCreateCategoryDTO;
import asterisk.sun.booking_tours.module.category.CategoryQueryService;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class FormCreateCategoryValidator implements Validator {
    private final CategoryQueryService queryService;

    public FormCreateCategoryValidator(CategoryQueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FormCreateCategoryDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FormCreateCategoryDTO formCreateCategoryDTO = (FormCreateCategoryDTO) target;

        // Validate name uniqueness
        if (formCreateCategoryDTO.getName() != null && !formCreateCategoryDTO.getName().isEmpty()) {
            if (queryService.existsByName(formCreateCategoryDTO.getName())) {
                errors.rejectValue("name", "error.category", "Category name already exists");
            }
        }

        // Validate slug uniqueness
        if (formCreateCategoryDTO.getSlug() != null && !formCreateCategoryDTO.getSlug().isEmpty()) {
            if (queryService.existsBySlug(formCreateCategoryDTO.getSlug())) {
                errors.rejectValue("slug", "error.category", "Category slug already exists");
            }
        }
    }
}
