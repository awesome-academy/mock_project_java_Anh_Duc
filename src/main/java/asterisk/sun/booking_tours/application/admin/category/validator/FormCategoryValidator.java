package asterisk.sun.booking_tours.application.admin.category.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import asterisk.sun.booking_tours.application.admin.category.dto.FormCreateCategoryDTO;
import asterisk.sun.booking_tours.application.admin.category.CategoryAdminService;

@Component
public class FormCategoryValidator implements Validator {
    private final CategoryAdminService service;

    public FormCategoryValidator(CategoryAdminService categoryService) {
        this.service = categoryService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FormCreateCategoryDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        FormCreateCategoryDTO formCreateCategoryDTO = (FormCreateCategoryDTO) target;

        // Implement your validation logic here
        if (formCreateCategoryDTO.getName() != null && !formCreateCategoryDTO.getName().isEmpty()) {
            if (service.existsByName(formCreateCategoryDTO.getName())) {
                errors.rejectValue("name", "error.category", "Category name already exists");
            }
        }

        if (formCreateCategoryDTO.getSlug() != null && !formCreateCategoryDTO.getSlug().isEmpty()) {
            if (service.existsBySlug(formCreateCategoryDTO.getSlug())) {
                errors.rejectValue("slug", "error.category", "Category slug already exists");
            }
        }
    }
}
