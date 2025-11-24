// package asterisk.sun.booking_tours.admin.controllers;

// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.WebDataBinder;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.InitBinder;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import asterisk.sun.booking_tours.admin.dto.category.FormCreateCategoryDTO;
// import asterisk.sun.booking_tours.admin.dto.category.FormEditCategoryDTO;
// import asterisk.sun.booking_tours.admin.services.AdminCategoryService;
// import asterisk.sun.booking_tours.admin.validator.category.FormCreateCategoryValidator;
// import asterisk.sun.booking_tours.admin.validator.category.FormEditCategoryValidator;
// import jakarta.validation.Valid;


// @Controller
// @RequestMapping("/admin/categories")
// public class AdminCategoryController extends BaseAdminController<AdminCategoryService> {
//     private final FormCreateCategoryValidator formCreateCategoryValidator;
//     private final FormEditCategoryValidator formEditCategoryValidator;

//     public AdminCategoryController(AdminCategoryService adminCategoryService,
//             FormCreateCategoryValidator formCreateCategoryValidator,
//             FormEditCategoryValidator formEditCategoryValidator) {
//         super(adminCategoryService, "pages/category/");
//         this.formCreateCategoryValidator = formCreateCategoryValidator;
//         this.formEditCategoryValidator = formEditCategoryValidator;
//     }

//     @InitBinder("formCreateCategoryDTO")
//     protected void initCreateBinder(WebDataBinder binder) {
//         binder.addValidators(formCreateCategoryValidator);
//     }

//     @InitBinder("formEditCategoryDTO")
//     protected void initEditBinder(WebDataBinder binder) {
//         binder.addValidators(formEditCategoryValidator);
//     }

//     @GetMapping
//     public String index(Model model, @RequestParam(required = false) String keyword) {

//         model.addAttribute("categories", service.queryCategoriesByKeyword(keyword));
//         model.addAttribute("keyword", keyword);

//         return view("index");
//     }

//     @Override
//     protected String getDefaultRedirectPath() {
//         return "redirect:/admin/categories";
//     }

//     @GetMapping("/create")
//     public String showCreateForm(Model model) {
//         model.addAttribute("formCreateCategoryDTO", new FormCreateCategoryDTO());
//         return view("create");
//     }

//     @PostMapping("/create")
//     public String createCategory(
//             @Valid @ModelAttribute("formCreateCategoryDTO") FormCreateCategoryDTO formCreateCategoryDTO,
//             BindingResult bindingResult,
//             RedirectAttributes redirectAttributes) {

//         if (bindingResult.hasErrors()) {
//             return getDefaultRedirectPath();
//         }

//         service.createCategory(formCreateCategoryDTO);
//         return handleSuccess(redirectAttributes, "Category created successfully!");
//     }

//     @GetMapping("/edit/{id}")
//     public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
//         FormEditCategoryDTO formEditCategoryDTO = service.getCategoryById(id);
//         model.addAttribute("formEditCategoryDTO", formEditCategoryDTO);
//         return view("edit");
//     }

//     @PostMapping("/edit/{id}")
//     public String editCategory(
//             @PathVariable("id") Long id,
//             @Valid @ModelAttribute("formEditCategoryDTO") FormEditCategoryDTO formEditCategoryDTO,
//             BindingResult bindingResult,
//             Model model,
//             RedirectAttributes redirectAttributes) {

//         formEditCategoryDTO.setId(id);

//         if (bindingResult.hasErrors()) {
//             return handleValidationErrors("edit", bindingResult.getFieldError().getDefaultMessage());
//         }

//         service.updateCategory(formEditCategoryDTO);
//         return handleSuccess(redirectAttributes, "Category updated successfully!");
//     }

//     @PostMapping("/delete/{id}")
//     public String postMethodName(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
//         service.deleteCategory(id);
//         return handleSuccess(redirectAttributes, "Category deleted successfully!");
//     }
// }
