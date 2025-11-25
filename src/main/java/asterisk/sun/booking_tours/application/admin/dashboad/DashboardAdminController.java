package asterisk.sun.booking_tours.application.admin.dashboad;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import asterisk.sun.booking_tours.application.admin.common.BaseAdminController;

@Controller
@RequestMapping("/admin/dashboard")
public class DashboardAdminController extends BaseAdminController<DashboardAdminService> {
    public DashboardAdminController(DashboardAdminService service) {
        super(service, "pages/admin/dashboard/");
    }

    @Override
    protected String getDefaultRedirectPath() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping
    public String showDashboard() {
        return "pages/dashboard";
    }
}
