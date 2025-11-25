package asterisk.sun.booking_tours.application.admin.tourdepartures;

import asterisk.sun.booking_tours.application.admin.common.BaseAdminController;
import asterisk.sun.booking_tours.application.admin.tourdepartures.dto.FormCreateTourDeparturesDTO;
import asterisk.sun.booking_tours.application.admin.tourdepartures.dto.FormEditTourDeparturesDTO;
import asterisk.sun.booking_tours.application.admin.tourdepartures.validator.FormTourDeparturesValidator;
import asterisk.sun.booking_tours.core.tour.TourRepository;
import asterisk.sun.booking_tours.core.tourdepartures.TourDepartureStatus;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/tour-departures")
public class TourDeparturesAdminController extends BaseAdminController<TourDeparturesAdminService> {

    private final FormTourDeparturesValidator formTourDeparturesValidator;
    private final TourRepository tourRepository;

    public TourDeparturesAdminController(
            TourDeparturesAdminService tourDeparturesAdminService,
            FormTourDeparturesValidator formTourDeparturesValidator,
            TourRepository tourRepository) {
        super(tourDeparturesAdminService, "pages/tour-departures/");
        this.formTourDeparturesValidator = formTourDeparturesValidator;
        this.tourRepository = tourRepository;
    }

    @InitBinder("formCreateTourDeparturesDTO")
    protected void initCreateBinder(WebDataBinder binder) {
        binder.addValidators(formTourDeparturesValidator);
    }

    @InitBinder("formEditTourDeparturesDTO")
    protected void initEditBinder(WebDataBinder binder) {
        binder.addValidators(formTourDeparturesValidator);
    }

    @Override
    protected String getDefaultRedirectPath() {
        return "redirect:/admin/tour-departures";
    }

    @GetMapping
    public String index(Model model, @RequestParam(required = false) String keyword) {
        model.addAttribute("tourDepartures", service.queryTourDeparturesByKeyword(keyword));
        model.addAttribute("keyword", keyword);
        return view("index");
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("formCreateTourDeparturesDTO", new FormCreateTourDeparturesDTO());
        model.addAttribute("tours", tourRepository.findAll());
        model.addAttribute("statuses", TourDepartureStatus.values());
        return view("create");
    }

    @PostMapping("/create")
    public String createTourDeparture(
            @Valid @ModelAttribute("formCreateTourDeparturesDTO") FormCreateTourDeparturesDTO formCreateTourDeparturesDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("tours", tourRepository.findAll());
            model.addAttribute("statuses", TourDepartureStatus.values());
            return handleValidationErrors("create", bindingResult);
        }

        service.createTourDeparture(formCreateTourDeparturesDTO);
        return handleSuccess(redirectAttributes, "Tour departure created successfully!");
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        FormEditTourDeparturesDTO formEditTourDeparturesDTO = service.getTourDepartureById(id);
        model.addAttribute("formEditTourDeparturesDTO", formEditTourDeparturesDTO);
        model.addAttribute("tours", tourRepository.findAll());
        model.addAttribute("statuses", TourDepartureStatus.values());
        return view("edit");
    }

    @PostMapping("/edit/{id}")
    public String editTourDeparture(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("formEditTourDeparturesDTO") FormEditTourDeparturesDTO formEditTourDeparturesDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        formEditTourDeparturesDTO.setId(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("tours", tourRepository.findAll());
            model.addAttribute("statuses", TourDepartureStatus.values());
            return handleValidationErrors("edit", bindingResult);
        }

        service.updateTourDeparture(formEditTourDeparturesDTO);
        return handleSuccess(redirectAttributes, "Tour departure updated successfully!");
    }

    @PostMapping("/delete/{id}")
    public String deleteTourDeparture(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        service.deleteTourDeparture(id);
        return handleSuccess(redirectAttributes, "Tour departure deleted successfully!");
    }
}
