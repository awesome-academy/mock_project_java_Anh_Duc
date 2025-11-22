package asterisk.sun.booking_tours.admin.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import asterisk.sun.booking_tours.admin.dto.user.FormCreateUserDTO;
import asterisk.sun.booking_tours.admin.dto.user.FormUpdateUserDTO;
import asterisk.sun.booking_tours.admin.handlers.UserFormErrorHandler;
import asterisk.sun.booking_tours.admin.services.AdminUserService;
import asterisk.sun.booking_tours.admin.validator.user.FormCreateUserValidator;
import asterisk.sun.booking_tours.admin.validator.user.FormUpdateUserValidator;
import asterisk.sun.booking_tours.module.user.Role;
import asterisk.sun.booking_tours.module.user.UserStatus;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController extends BaseAdminController {
    private static final String ADMIN_USER_VIEW_PATH = "pages/user/";
    private final AdminUserService adminUserService;
    private final FormCreateUserValidator formCreateUserValidator;
    private final FormUpdateUserValidator formUpdateUserValidator;
    private final UserFormErrorHandler userFormErrorHandler;

    public AdminUserController(AdminUserService adminUserService,
            FormCreateUserValidator formCreateUserValidator,
            FormUpdateUserValidator formUpdateUserValidator,
            UserFormErrorHandler userFormErrorHandler) {
        this.adminUserService = adminUserService;
        this.formCreateUserValidator = formCreateUserValidator;
        this.formUpdateUserValidator = formUpdateUserValidator;
        this.userFormErrorHandler = userFormErrorHandler;
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

    @Override
    protected void addCommonAttributes(ModelAndView mav) {
        mav.addObject("roles", Role.values());
        mav.addObject("statuses", UserStatus.values());
    }

    @GetMapping
    public ModelAndView index(Model model) {
        ModelAndView mav = new ModelAndView(ADMIN_USER_VIEW_PATH + "index");
        mav.addObject("users", adminUserService.getAllUsersForListing());

        return mav;
    }

    @GetMapping("/create")
    public ModelAndView showCreateForm(Model model) {
        ModelAndView mav = new ModelAndView(ADMIN_USER_VIEW_PATH + "create");
        mav.addObject("formCreateUserDTO", new FormCreateUserDTO());
        addCommonAttributes(mav);

        return mav;
    }

    @PostMapping("/store")
    public String store(@Valid @ModelAttribute("formCreateUserDTO") FormCreateUserDTO formCreateUserDTO,
            BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            return userFormErrorHandler.handleValidationErrors(model, "create");
        }

        try {
            adminUserService.createUser(formCreateUserDTO);
            return handleSuccess(redirectAttributes, "User created successfully!");
        } catch (Exception e) {
            return userFormErrorHandler.handleServiceException(e, redirectAttributes, model, "create");
        }
    }

    @GetMapping("/edit/{id}")
    public ModelAndView showEditForm(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        FormUpdateUserDTO formUpdateUserDTO = adminUserService.getUserById(id);

        ModelAndView mav = new ModelAndView(ADMIN_USER_VIEW_PATH + "edit");
        mav.addObject("formUpdateUserDTO", formUpdateUserDTO);
        addCommonAttributes(mav);

        return mav;
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Long id,
            @Valid @ModelAttribute("formUpdateUserDTO") FormUpdateUserDTO formUpdateUserDTO,
            BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        formUpdateUserDTO.setId(id);

        if (bindingResult.hasErrors()) {
            return userFormErrorHandler.handleValidationErrors(model, "edit");
        }

        try {
            adminUserService.updateUser(formUpdateUserDTO);
            return handleSuccess(redirectAttributes, "User updated successfully!");
        } catch (Exception e) {
            return userFormErrorHandler.handleServiceException(e, redirectAttributes, model, "edit");
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            adminUserService.deleteUser(id);
            return handleSuccess(redirectAttributes, "User deleted successfully!");
        } catch (Exception e) {
            return userFormErrorHandler.handleDeleteException(e, redirectAttributes, getDefaultRedirectPath());
        }
    }
}
