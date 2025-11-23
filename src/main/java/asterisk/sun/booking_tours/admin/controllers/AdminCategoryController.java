package asterisk.sun.booking_tours.admin.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import asterisk.sun.booking_tours.admin.services.AdminCategoryService;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private static final String ADMIN_CATEGORY_VIEW_PATH = "pages/category/";
    private final AdminCategoryService adminCategoryService;

    public AdminCategoryController(AdminCategoryService adminCategoryService) {
        this.adminCategoryService = adminCategoryService;
    }

    @GetMapping
    public ModelAndView index(Model model) {
        ModelAndView mav = new ModelAndView(ADMIN_CATEGORY_VIEW_PATH + "index");
        mav.addObject("categories", adminCategoryService.getAllCategoriesForListing());

        return mav;
    }
}
