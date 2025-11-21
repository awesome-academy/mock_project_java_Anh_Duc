package sun.asterisk.booking_tours.admin.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin/dashboard")
public class AdminDashboardController {

    @GetMapping
    public String showDashboard() {
        return "pages/dashboard";
    }

}
