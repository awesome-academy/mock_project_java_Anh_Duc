package asterisk.sun.booking_tours.application.admin.user;

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
import asterisk.sun.booking_tours.application.admin.user.dto.FormCreateUserDTO;
import asterisk.sun.booking_tours.application.admin.user.dto.FormUpdateUserDTO;
import asterisk.sun.booking_tours.application.admin.user.validator.FormCreateUserValidator;
import asterisk.sun.booking_tours.application.admin.user.validator.FormUpdateUserValidator;
import asterisk.sun.booking_tours.core.user.Role;
import asterisk.sun.booking_tours.core.user.UserStatus;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/users")
public class UserAdminController extends BaseAdminController<UserAdminService> {
    private final FormCreateUserValidator formCreateUserValidator;
    private final FormUpdateUserValidator formUpdateUserValidator;

    protected UserAdminController(UserAdminService service, FormCreateUserValidator formCreateUserValidator,
            FormUpdateUserValidator formUpdateUserValidator) {
        super(service, "pages/user/");
        this.formCreateUserValidator = formCreateUserValidator;
        this.formUpdateUserValidator = formUpdateUserValidator;
    }

    @InitBinder("formCreateUserDTO")
    protected void initCreateBinder(WebDataBinder binder) {
        binder.addValidators(formCreateUserValidator);
    }

    @InitBinder("formUpdateUserDTO")
    protected void initUpdateBinder(WebDataBinder binder) {
        binder.addValidators(formUpdateUserValidator);
    }

    @Override
    protected String getDefaultRedirectPath() {
        return "redirect:/admin/users";
    }

    protected void addCommonAttributes(Model model) {
        model.addAttribute("roles", Role.values());
        model.addAttribute("statuses", UserStatus.values());
    }

    @GetMapping
    public String index(Model model, @RequestParam(required = false) String keyword) {
        model.addAttribute("users", service.queryListUserByKeyword(keyword));
        model.addAttribute("keyword", keyword);
        return view("index");
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("formCreateUserDTO", new FormCreateUserDTO());
        addCommonAttributes(model);
        return view("create");
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("formCreateUserDTO") FormCreateUserDTO formCreateUserDTO,
            BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            addCommonAttributes(model);
            return handleValidationErrors("create", bindingResult);
        }

        service.createUser(formCreateUserDTO);
        return handleSuccess(redirectAttributes, "User created successfully!");
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        FormUpdateUserDTO formUpdateUserDTO = service.getUserById(id);

        model.addAttribute("formUpdateUserDTO", formUpdateUserDTO);
        addCommonAttributes(model);

        return view("edit");
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable("id") Long id,
            @Valid @ModelAttribute("formUpdateUserDTO") FormUpdateUserDTO formUpdateUserDTO,
            BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        formUpdateUserDTO.setId(id);

        if (bindingResult.hasErrors()) {
            addCommonAttributes(model);
            return handleValidationErrors("edit", bindingResult);
        }

        service.updateUser(formUpdateUserDTO);
        return handleSuccess(redirectAttributes, "User updated successfully!");
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        service.deleteUser(id);
        return handleSuccess(redirectAttributes, "User deleted successfully!");
    }
}
