package asterisk.sun.booking_tours.application.admin.tour;

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

import asterisk.sun.booking_tours.application.admin.common.BaseAdminController;
import asterisk.sun.booking_tours.application.admin.tour.dto.FormCreateTourDTO;
import asterisk.sun.booking_tours.application.admin.tour.dto.FormEditTourDTO;
import asterisk.sun.booking_tours.application.admin.tour.validator.FormTourValidator;
import asterisk.sun.booking_tours.core.category.CategoryRepository;
import asterisk.sun.booking_tours.core.user.UserRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/tours")
public class TourAdminController extends BaseAdminController<TourAdminService> {

    private final FormTourValidator formTourValidator;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public TourAdminController(
            TourAdminService tourAdminService,
            FormTourValidator formTourValidator,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {
        super(tourAdminService, "pages/tour/");
        this.formTourValidator = formTourValidator;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @InitBinder("formCreateTourDTO")
    protected void initCreateBinder(WebDataBinder binder) {
        binder.addValidators(formTourValidator);
    }

    @InitBinder("formEditTourDTO")
    protected void initEditBinder(WebDataBinder binder) {
        binder.addValidators(formTourValidator);
    }

    @Override
    protected String getDefaultRedirectPath() {
        return "redirect:/admin/tours";
    }

    @GetMapping
    public String index(Model model, @RequestParam(required = false) String keyword) {
        model.addAttribute("tours", service.queryToursByKeyword(keyword));
        model.addAttribute("keyword", keyword);
        return view("index");
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("formCreateTourDTO", new FormCreateTourDTO());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        return view("create");
    }

    @PostMapping("/create")
    public String createTour(
            @Valid @ModelAttribute("formCreateTourDTO") FormCreateTourDTO formCreateTourDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("users", userRepository.findAll());
            return handleValidationErrors("create", bindingResult);
        }

        service.createTour(formCreateTourDTO);
        return handleSuccess(redirectAttributes, "Tour created successfully!");
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        FormEditTourDTO formEditTourDTO = service.getTourById(id);
        model.addAttribute("formEditTourDTO", formEditTourDTO);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        return view("edit");
    }

    @PostMapping("/edit/{id}")
    public String editTour(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("formEditTourDTO") FormEditTourDTO formEditTourDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        formEditTourDTO.setId(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("users", userRepository.findAll());
            return handleValidationErrors("edit", bindingResult);
        }

        service.updateTour(formEditTourDTO);
        return handleSuccess(redirectAttributes, "Tour updated successfully!");
    }

    @PostMapping("/delete/{id}")
    public String deleteTour(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        service.deleteTour(id);
        return handleSuccess(redirectAttributes, "Tour deleted successfully!");
    }
}
