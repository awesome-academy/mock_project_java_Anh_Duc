package asterisk.sun.booking_tours.application.admin.common;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import asterisk.sun.booking_tours.application.admin.common.dto.Breadcrumb;

@ControllerAdvice(basePackages = "asterisk.sun.booking_tours.application.admin")
public class GlobalBindingInitializer {
    @ModelAttribute("breadcrumbs")
    public List<Breadcrumb> initializeBreadcrumbs() {
        List<Breadcrumb> breadcrumbs = new ArrayList<>();
        breadcrumbs.add(new Breadcrumb("Users", "/admin/users", "fas fa-users"));
        breadcrumbs.add(new Breadcrumb("Categories", "/admin/categories", "fas fa-list"));
        breadcrumbs.add(new Breadcrumb("Tours", "/admin/tours", "fas fa-plane"));
        breadcrumbs.add(new Breadcrumb("Tour Departures", "/admin/tour-departures", "fas fa-plane-departure"));
        breadcrumbs.add(new Breadcrumb("Booking", "/admin/bookings", "fas fa-book"));
        breadcrumbs.add(new Breadcrumb("Coupons", "/admin/coupons", "fas fa-ticket-alt"));
        breadcrumbs.add(new Breadcrumb("Payments", "/admin/payments", "fas fa-credit-card"));
        breadcrumbs.add(new Breadcrumb("Bank Account", "/admin/bank-accounts", "fas fa-university"));
        breadcrumbs.add(new Breadcrumb("Reviews", "/admin/reviews", "fas fa-star"));

        return breadcrumbs;
    }
}
