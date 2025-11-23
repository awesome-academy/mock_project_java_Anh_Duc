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
public class AdminCategoryController extends BaseAdminController {
    private static final String ADMIN_CATEGORY_VIEW_PATH = "pages/category/";
    private final AdminCategoryService adminCategoryService;
    private final FormCreateCategoryValidator formCreateCategoryValidator;

    public AdminCategoryController(AdminCategoryService adminCategoryService,
            FormCreateCategoryValidator formCreateCategoryValidator) {
        this.adminCategoryService = adminCategoryService;
        this.formCreateCategoryValidator = formCreateCategoryValidator;
    }

    @InitBinder("formCreateCategoryDTO")
    protected void initCreateBinder(WebDataBinder binder) {
        binder.addValidators(formCreateCategoryValidator);
    }

    @GetMapping
    public ModelAndView index(Model model) {
        ModelAndView mav = new ModelAndView(ADMIN_CATEGORY_VIEW_PATH + "index");
        mav.addObject("categories", adminCategoryService.getAllCategoriesForListing());

        return mav;
    }

    @Override
    protected String getDefaultRedirectPath() {
        return "redirect:/admin/categories";
    }

    @GetMapping("/create")
    public ModelAndView showCreateForm() {
        ModelAndView mav = new ModelAndView(ADMIN_CATEGORY_VIEW_PATH + "create");
        mav.addObject("formCreateCategoryDTO", new FormCreateCategoryDTO());
        return mav;
    }

    @PostMapping("/create")
    public String createCategory(
            @Valid @ModelAttribute("formCreateCategoryDTO") FormCreateCategoryDTO formCreateCategoryDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return ADMIN_CATEGORY_VIEW_PATH + "create";
        }

        try {
            adminCategoryService.createCategory(formCreateCategoryDTO);
            return handleSuccess(redirectAttributes, "Category created successfully!");
        } catch (IllegalArgumentException e) {
            return handleError(redirectAttributes, e.getMessage());
        }
    }
}
