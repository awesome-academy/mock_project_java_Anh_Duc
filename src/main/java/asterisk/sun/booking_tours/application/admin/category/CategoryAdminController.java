package asterisk.sun.booking_tours.application.admin.category;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import asterisk.sun.booking_tours.application.admin.category.validator.FormCategoryValidator;
import asterisk.sun.booking_tours.application.admin.common.BaseAdminController;
import asterisk.sun.booking_tours.application.admin.category.dto.CategorySearchRequestDTO;
import asterisk.sun.booking_tours.application.admin.category.dto.FormCreateCategoryDTO;
import asterisk.sun.booking_tours.application.admin.category.dto.FormEditCategoryDTO;
import asterisk.sun.booking_tours.application.admin.category.dto.ListCategoryDTO;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/categories")
public class CategoryAdminController extends BaseAdminController<CategoryAdminService> {

    public CategoryAdminController(
            CategoryAdminService categoryAdminService,
            FormCategoryValidator formCreateCategoryValidator) {
        super(categoryAdminService, "pages/category/");
    }

    @Override
    protected String getDefaultRedirectPath() {
        return "redirect:/admin/categories";
    }

    @GetMapping
    public String index(Model model, CategorySearchRequestDTO request) {
        Page<ListCategoryDTO> categoryPage = service.queryCategoriesByKeyword(request);
        model.addAttribute("categories", categoryPage);

        model.addAttribute("searchRequest", request);

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

        // If validation fails, return to create form with errors
        if (bindingResult.hasErrors()) {
            return handleValidationErrors("create", bindingResult);
        }

        service.createCategory(formCreateCategoryDTO);
        return handleSuccess(redirectAttributes, "Category created successfully!");
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        FormEditCategoryDTO formEditCategoryDTO = service.getCategoryById(id);
        model.addAttribute("formEditCategoryDTO", formEditCategoryDTO);
        return view("edit");
    }

    @PostMapping("/edit/{id}")
    public String editCategory(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("formEditCategoryDTO") FormEditCategoryDTO formEditCategoryDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        formEditCategoryDTO.setId(id);

        if (bindingResult.hasErrors()) {
            return handleValidationErrors("edit", bindingResult);
        }

        service.updateCategory(formEditCategoryDTO);
        return handleSuccess(redirectAttributes, "Category updated successfully!");
    }

    @PostMapping("/delete/{id}")
    public String postMethodName(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        service.deleteCategory(id);
        return handleSuccess(redirectAttributes, "Category deleted successfully!");
    }
}
