package asterisk.sun.booking_tours.admin.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import asterisk.sun.booking_tours.admin.dto.category.FormCreateCategoryDTO;
import asterisk.sun.booking_tours.admin.services.AdminCategoryService;
import asterisk.sun.booking_tours.admin.validator.category.FormCreateCategoryValidator;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController extends BaseAdminController<AdminCategoryService> {
    private final FormCreateCategoryValidator formCreateCategoryValidator;

    public AdminCategoryController(AdminCategoryService adminCategoryService,
            FormCreateCategoryValidator formCreateCategoryValidator) {
        super(adminCategoryService, "pages/category/");
        this.formCreateCategoryValidator = formCreateCategoryValidator;
    }

    @InitBinder("formCreateCategoryDTO")
    protected void initCreateBinder(WebDataBinder binder) {
        binder.addValidators(formCreateCategoryValidator);
    }

    @GetMapping
    public String index(Model model) {

        model.addAttribute("categories", service.getAllCategoriesForListing());

        return view("index");
    }

    @Override
    protected String getDefaultRedirectPath() {
        return "redirect:/admin/categories";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("formCreateCategoryDTO", new FormCreateCategoryDTO());
        return view("create");
    }

    @PostMapping("/create")
    public String createCategory(
            @Valid @ModelAttribute("formCreateCategoryDTO") FormCreateCategoryDTO formCreateCategoryDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return getDefaultRedirectPath();
        }

        service.createCategory(formCreateCategoryDTO);
        return handleSuccess(redirectAttributes, "Category created successfully!");
    }
}
