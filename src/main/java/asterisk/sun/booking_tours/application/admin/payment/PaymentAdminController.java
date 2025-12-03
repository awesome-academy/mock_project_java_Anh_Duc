package asterisk.sun.booking_tours.application.admin.payment;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import asterisk.sun.booking_tours.application.admin.common.BaseAdminController;
import asterisk.sun.booking_tours.core.payment.PaymentMethod;
import asterisk.sun.booking_tours.core.payment.PaymentStatus;

@Controller
@RequestMapping("/admin/payments")
public class PaymentAdminController extends BaseAdminController<PaymentAdminService> {

    public PaymentAdminController(PaymentAdminService paymentAdminService) {
        super(paymentAdminService, "pages/payment/");
    }

    @Override
    protected String getDefaultRedirectPath() {
        return "redirect:/admin/payments";
    }

    @GetMapping
    public String index(
            Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) PaymentStatus status) {

        if (status != null) {
            model.addAttribute("payments", service.queryPaymentsByStatus(status));
            model.addAttribute("filterStatus", status);
        } else {
            model.addAttribute("payments", service.queryPaymentsByKeyword(keyword));
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("paymentStatuses", PaymentStatus.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());

        return view("index");
    }

    @GetMapping("/detail/{id}")
    public String showDetail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("payment", service.getPaymentById(id));
        model.addAttribute("paymentStatuses", PaymentStatus.values());
        return view("detail");
    }

    @PostMapping("/update-status/{id}")
    public String updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") PaymentStatus status,
            RedirectAttributes redirectAttributes) {

        service.updatePaymentStatus(id, status);
        return handleSuccess(redirectAttributes, "Payment status updated successfully!");
    }

    @PostMapping("/delete/{id}")
    public String deletePayment(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        service.deletePayment(id);
        return handleSuccess(redirectAttributes, "Payment deleted successfully!");
    }
}
