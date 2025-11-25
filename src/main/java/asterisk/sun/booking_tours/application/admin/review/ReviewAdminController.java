package asterisk.sun.booking_tours.application.admin.review;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import asterisk.sun.booking_tours.application.admin.common.BaseAdminController;
import asterisk.sun.booking_tours.application.admin.review.dto.FormUpdateReviewDTO;
import asterisk.sun.booking_tours.core.review.ReviewStatus;
import asterisk.sun.booking_tours.core.review.ReviewableType;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/reviews")
public class ReviewAdminController extends BaseAdminController<ReviewAdminService> {

    public ReviewAdminController(ReviewAdminService reviewAdminService) {
        super(reviewAdminService, "pages/review/");
    }

    @Override
    protected String getDefaultRedirectPath() {
        return "redirect:/admin/reviews";
    }

    /**
     * Display list of reviews with search functionality
     */
    @GetMapping
    public String index(Model model, @RequestParam(required = false) String keyword) {
        model.addAttribute("reviews", service.queryReviewsByKeyword(keyword));
        model.addAttribute("keyword", keyword);
        model.addAttribute("reviewStatuses", ReviewStatus.values());
        model.addAttribute("reviewableTypes", ReviewableType.values());
        return view("index");
    }

    /**
     * Show edit form for review status
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        FormUpdateReviewDTO formUpdateReviewDTO = service.getReviewById(id);
        model.addAttribute("formUpdateReviewDTO", formUpdateReviewDTO);
        model.addAttribute("reviewStatuses", ReviewStatus.values());
        model.addAttribute("reviewableTypes", ReviewableType.values());
        return view("edit");
    }

    /**
     * Update review status
     */
    @PostMapping("/edit/{id}")
    public String updateReview(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("formUpdateReviewDTO") FormUpdateReviewDTO formUpdateReviewDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        formUpdateReviewDTO.setId(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("reviewStatuses", ReviewStatus.values());
            model.addAttribute("reviewableTypes", ReviewableType.values());
            return handleValidationErrors("edit", bindingResult);
        }

        service.updateReviewStatus(formUpdateReviewDTO);
        return handleSuccess(redirectAttributes, "Review status updated successfully!");
    }

    /**
     * Quick approve review
     */
    @PostMapping("/approve/{id}")
    public String approveReview(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        service.approveReview(id);
        return handleSuccess(redirectAttributes, "Review approved successfully!");
    }

    /**
     * Quick reject review
     */
    @PostMapping("/reject/{id}")
    public String rejectReview(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        service.rejectReview(id);
        return handleSuccess(redirectAttributes, "Review rejected successfully!");
    }

    /**
     * Quick hide review
     */
    @PostMapping("/hide/{id}")
    public String hideReview(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        service.hideReview(id);
        return handleSuccess(redirectAttributes, "Review hidden successfully!");
    }

    /**
     * Delete review
     */
    @PostMapping("/delete/{id}")
    public String deleteReview(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        service.deleteReview(id);
        return handleSuccess(redirectAttributes, "Review deleted successfully!");
    }
}
