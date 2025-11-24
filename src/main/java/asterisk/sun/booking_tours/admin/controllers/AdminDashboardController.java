package asterisk.sun.booking_tours.admin.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin/dashboard")
public class AdminDashboardController extends BaseAdminController<Void> {

    public AdminDashboardController() {
        super(null, "pages/");
    }

    @GetMapping
    public String showDashboard() {
        return view("dashboard");
    }

    @Override
    protected String getDefaultRedirectPath() {
        return "redirect:/admin/dashboard";
    }
}
