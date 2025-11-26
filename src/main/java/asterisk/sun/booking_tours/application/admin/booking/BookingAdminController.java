package asterisk.sun.booking_tours.application.admin.booking;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import asterisk.sun.booking_tours.application.admin.booking.dto.FormCreateBookingDTO;
import asterisk.sun.booking_tours.application.admin.booking.dto.FormEditBookingDTO;
import asterisk.sun.booking_tours.application.admin.booking.validator.FormBookingValidator;
import asterisk.sun.booking_tours.application.admin.common.BaseAdminController;
import asterisk.sun.booking_tours.core.booking.BookingStatus;
import asterisk.sun.booking_tours.core.tourdepartures.TourDeparturesRepository;
import asterisk.sun.booking_tours.core.user.UserRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/bookings")
public class BookingAdminController extends BaseAdminController<BookingAdminService> {

    private final FormBookingValidator formBookingValidator;
    private final UserRepository userRepository;
    private final TourDeparturesRepository tourDeparturesRepository;

    public BookingAdminController(
            BookingAdminService bookingAdminService,
            FormBookingValidator formBookingValidator,
            UserRepository userRepository,
            TourDeparturesRepository tourDeparturesRepository) {
        super(bookingAdminService, "pages/booking/");
        this.formBookingValidator = formBookingValidator;
        this.userRepository = userRepository;
        this.tourDeparturesRepository = tourDeparturesRepository;
    }

    @InitBinder("formCreateBookingDTO")
    protected void initCreateBinder(WebDataBinder binder) {
        binder.addValidators(formBookingValidator);
    }

    @InitBinder("formEditBookingDTO")
    protected void initEditBinder(WebDataBinder binder) {
        binder.addValidators(formBookingValidator);
    }

    @Override
    protected String getDefaultRedirectPath() {
        return "redirect:/admin/bookings";
    }

    /**
     * Display list of bookings
     */
    @GetMapping
    public String index(
            Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BookingStatus status) {

        if (status != null) {
            model.addAttribute("bookings", service.queryBookingsByStatus(status));
            model.addAttribute("filterStatus", status);
        } else {
            model.addAttribute("bookings", service.queryBookingsByKeyword(keyword));
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("bookingStatuses", BookingStatus.values());

        System.out.println("Booking statuses: " + service.queryBookingsByKeyword(keyword).get(0).getStatus());
        return view("index");
    }

    /**
     * Show create booking form
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("formCreateBookingDTO", new FormCreateBookingDTO());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("tourDepartures", tourDeparturesRepository.findAll());
        model.addAttribute("bookingStatuses", BookingStatus.values());
        return view("create");
    }

    /**
     * Create new booking
     */
    @PostMapping("/create")
    public String createBooking(
            @Valid @ModelAttribute("formCreateBookingDTO") FormCreateBookingDTO formCreateBookingDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("tourDepartures", tourDeparturesRepository.findAll());
            model.addAttribute("bookingStatuses", BookingStatus.values());
            return handleValidationErrors("create", bindingResult);
        }

        service.createBooking(formCreateBookingDTO);
        return handleSuccess(redirectAttributes, "Booking created successfully!");
    }

    /**
     * Show edit booking form
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        FormEditBookingDTO formEditBookingDTO = service.getBookingById(id);
        model.addAttribute("formEditBookingDTO", formEditBookingDTO);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("tourDepartures", tourDeparturesRepository.findAll());
        model.addAttribute("bookingStatuses", BookingStatus.values());
        return view("edit");
    }

    /**
     * Update existing booking
     */
    @PostMapping("/edit/{id}")
    public String editBooking(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("formEditBookingDTO") FormEditBookingDTO formEditBookingDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("tourDepartures", tourDeparturesRepository.findAll());
            model.addAttribute("bookingStatuses", BookingStatus.values());
            return handleValidationErrors("edit", bindingResult);
        }

        service.updateBooking(id, formEditBookingDTO);
        return handleSuccess(redirectAttributes, "Booking updated successfully!");
    }

    /**
     * Update booking status
     */
    @PostMapping("/update-status/{id}")
    public String updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") BookingStatus status,
            RedirectAttributes redirectAttributes) {

        service.updateBookingStatus(id, status);
        return handleSuccess(redirectAttributes, "Booking status updated successfully!");
    }

    /**
     * Delete booking
     */
    @PostMapping("/delete/{id}")
    public String deleteBooking(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        service.deleteBooking(id);
        return handleSuccess(redirectAttributes, "Booking deleted successfully!");
    }
}
