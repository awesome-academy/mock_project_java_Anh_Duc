package asterisk.sun.booking_tours.application.admin.category;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import asterisk.sun.booking_tours.admin.controllers.BaseAdminController;

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
}
