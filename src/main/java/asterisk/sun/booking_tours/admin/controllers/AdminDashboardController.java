package asterisk.sun.booking_tours.admin.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("admin/dashboard")
public class AdminDashboardController extends BaseAdminController {

    @GetMapping
    public String showDashboard() {
        return "pages/dashboard";
    }

    @Override
    protected String getDefaultRedirectPath() {
        return "redirect:/admin/dashboard";
    }

    @Override
    protected void addCommonAttributes(ModelAndView mav) {
        throw new UnsupportedOperationException("Unimplemented method 'addCommonAttributes'");
    }
}
