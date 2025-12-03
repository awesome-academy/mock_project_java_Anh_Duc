package asterisk.sun.booking_tours.application.admin.coupon;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import asterisk.sun.booking_tours.application.admin.common.BaseAdminController;
import asterisk.sun.booking_tours.application.admin.coupon.dto.CouponRequestDTO;
import asterisk.sun.booking_tours.application.admin.coupon.dto.CouponResponseDTO;
import asterisk.sun.booking_tours.application.admin.coupon.dto.CouponSearchRequestDTO;
import asterisk.sun.booking_tours.core.coupon.CouponStatus;
import asterisk.sun.booking_tours.core.coupon.CouponType;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/coupons")
public class CouponAdminController extends BaseAdminController<CouponAdminService> {

    public CouponAdminController(CouponAdminService couponAdminService) {
        super(couponAdminService, "pages/coupon/");
    }

    @Override
    protected String getDefaultRedirectPath() {
        return "redirect:/admin/coupons";
    }

    @GetMapping
    public String index(Model model, CouponSearchRequestDTO request) {
        Page<CouponResponseDTO> couponPage = service.searchCoupons(request);

        model.addAttribute("coupons", couponPage);
        model.addAttribute("searchRequest", request);
        model.addAttribute("couponStatuses", CouponStatus.values());

        return view("index");
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("couponRequest", new CouponRequestDTO());
        model.addAttribute("couponTypes", CouponType.values());
        model.addAttribute("couponStatuses", CouponStatus.values());
        return view("create");
    }

    @PostMapping("/create")
    public String createCoupon(
            @Valid @ModelAttribute("couponRequest") CouponRequestDTO couponRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("couponTypes", CouponType.values());
            model.addAttribute("couponStatuses", CouponStatus.values());
            return view("create");
        }

        try {
            service.createCoupon(couponRequest);
            return handleSuccess(redirectAttributes, "Coupon created successfully!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("couponTypes", CouponType.values());
            model.addAttribute("couponStatuses", CouponStatus.values());
            return view("create");
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        CouponResponseDTO coupon = service.getCouponById(id);

        // Convert response to request DTO
        CouponRequestDTO couponRequest = new CouponRequestDTO();
        couponRequest.setCode(coupon.getCode());
        couponRequest.setDescription(coupon.getDescription());
        couponRequest.setType(coupon.getType());
        couponRequest.setDiscountValue(coupon.getDiscountValue());
        couponRequest.setMaxDiscountAmount(coupon.getMaxDiscountAmount());
        couponRequest.setMinPurchaseAmount(coupon.getMinPurchaseAmount());
        couponRequest.setUsageLimit(coupon.getUsageLimit());
        couponRequest.setValidFrom(coupon.getValidFrom());
        couponRequest.setValidTo(coupon.getValidTo());
        couponRequest.setStatus(coupon.getStatus());

        model.addAttribute("couponRequest", couponRequest);
        model.addAttribute("couponId", id);
        model.addAttribute("usedCount", coupon.getUsedCount());
        model.addAttribute("couponTypes", CouponType.values());
        model.addAttribute("couponStatuses", CouponStatus.values());

        return view("edit");
    }

    @PostMapping("/edit/{id}")
    public String updateCoupon(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("couponRequest") CouponRequestDTO couponRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("couponId", id);
            model.addAttribute("couponTypes", CouponType.values());
            model.addAttribute("couponStatuses", CouponStatus.values());
            return view("edit");
        }

        try {
            service.updateCoupon(id, couponRequest);
            return handleSuccess(redirectAttributes, "Coupon updated successfully!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("couponId", id);
            model.addAttribute("couponTypes", CouponType.values());
            model.addAttribute("couponStatuses", CouponStatus.values());
            return view("edit");
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteCoupon(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteCoupon(id);
            return handleSuccess(redirectAttributes, "Coupon deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete coupon: " + e.getMessage());
            return getDefaultRedirectPath();
        }
    }

    @GetMapping("/validate")
    @ResponseBody
    public ResponseEntity<?> validateCoupon(@RequestParam String code) {
        try {
            CouponResponseDTO coupon = service.validateAndGetCoupon(code);
            return ResponseEntity.ok(coupon);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/check-code")
    @ResponseBody
    public ResponseEntity<Boolean> checkCodeExists(@RequestParam String code) {
        boolean exists = service.existsByCode(code);
        return ResponseEntity.ok(exists);
    }
}
