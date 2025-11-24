// package asterisk.sun.booking_tours.admin.validator.category;

// import asterisk.sun.booking_tours.admin.dto.category.FormCreateCategoryDTO;
// import asterisk.sun.booking_tours.domain.category.CategoryService;

// import org.springframework.stereotype.Component;
// import org.springframework.validation.Errors;
// import org.springframework.validation.Validator;

// @Component
// public class FormCreateCategoryValidator implements Validator {
//     private final CategoryService categoryService;

//     public FormCreateCategoryValidator(CategoryService categoryService) {
//         this.categoryService = categoryService;
//     }

//     @Override
//     public boolean supports(Class<?> clazz) {
//         return FormCreateCategoryDTO.class.equals(clazz);
//     }

//     @Override
//     public void validate(Object target, Errors errors) {
//         FormCreateCategoryDTO formCreateCategoryDTO = (FormCreateCategoryDTO) target;

//         // Implement your validation logic here
//         if (formCreateCategoryDTO.getName() != null && !formCreateCategoryDTO.getName().isEmpty()) {
//             if (categoryService.existsByName(formCreateCategoryDTO.getName())) {
//                 errors.rejectValue("name", "error.category", "Category name already exists");
//             }
//         }

//         if (formCreateCategoryDTO.getSlug() != null && !formCreateCategoryDTO.getSlug().isEmpty()) {
//             if (categoryService.existsBySlug(formCreateCategoryDTO.getSlug())) {
//                 errors.rejectValue("slug", "error.category", "Category slug already exists");
//             }
//         }
//     }
// }
