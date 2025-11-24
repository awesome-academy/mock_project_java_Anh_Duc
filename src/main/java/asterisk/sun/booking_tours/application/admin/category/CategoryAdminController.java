package asterisk.sun.booking_tours.application.admin.category;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import asterisk.sun.booking_tours.admin.controllers.BaseAdminController;
import asterisk.sun.booking_tours.application.admin.category.dto.FormCreateCategoryDTO;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/categories")
public class CategoryAdminController extends BaseAdminController<CategoryAdminService> {

    public CategoryAdminController(CategoryAdminService categoryAdminService) {
        super(categoryAdminService, "pages/category/");
    }

    @Override
    protected String getDefaultRedirectPath() {
        return "redirect:/admin/categories";
    }

    @GetMapping
    public String index(Model model, @RequestParam(required = false) String keyword) {

        model.addAttribute("categories", service.queryCategoriesByKeyword(keyword));
        model.addAttribute("keyword", keyword);

        return view("index");
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
