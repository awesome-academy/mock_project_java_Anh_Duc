package asterisk.sun.booking_tours.application.admin.bankaccount;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import asterisk.sun.booking_tours.application.admin.bankaccount.dto.CreateBankAccountDTO;
import asterisk.sun.booking_tours.application.admin.bankaccount.dto.UpdateBankAccountDTO;
import asterisk.sun.booking_tours.application.admin.common.BaseAdminController;
import asterisk.sun.booking_tours.core.payment.BankAccount;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/bank-accounts")
public class BankAccountAdminController extends BaseAdminController<BankAccountAdminService> {

    public BankAccountAdminController(BankAccountAdminService bankAccountAdminService) {
        super(bankAccountAdminService, "pages/bank-account/");
    }

    @Override
    protected String getDefaultRedirectPath() {
        return "redirect:/admin/bank-accounts";
    }

    /**
     * List all bank accounts with search and filter
     */
    @GetMapping
    public String index(
            Model model,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive) {

        if (isActive != null) {
            model.addAttribute("bankAccounts", service.getBankAccountsByStatus(isActive));
            model.addAttribute("filterStatus", isActive);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("bankAccounts", service.searchBankAccounts(keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("bankAccounts", service.getAllBankAccounts());
        }

        return view("index");
    }

    /**
     * Show create form
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("bankAccountDTO", new CreateBankAccountDTO());
        return view("create");
    }

    /**
     * Process create form
     */
    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("bankAccountDTO") CreateBankAccountDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            return view("create");
        }

        try {
            service.createBankAccount(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Bank account created successfully!");
            return getDefaultRedirectPath();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return view("create");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while creating bank account!");
            return view("create");
        }
    }

    /**
     * Show edit form
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            BankAccount bankAccount = service.getBankAccountById(id);

            UpdateBankAccountDTO dto = new UpdateBankAccountDTO();
            dto.setId(bankAccount.getId());
            dto.setBankName(bankAccount.getBankName());
            dto.setAccountNumber(bankAccount.getAccountNumber());
            dto.setAccountHolder(bankAccount.getAccountHolder());
            dto.setBranch(bankAccount.getBranch());
            dto.setSwiftCode(bankAccount.getSwiftCode());
            dto.setDescription(bankAccount.getDescription());
            dto.setIsActive(bankAccount.getIsActive());
            dto.setDisplayOrder(bankAccount.getDisplayOrder());

            model.addAttribute("bankAccountDTO", dto);
            return view("edit");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bank account not found!");
            return getDefaultRedirectPath();
        }
    }

    /**
     * Process edit form
     */
    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("bankAccountDTO") UpdateBankAccountDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            return view("edit");
        }

        try {
            dto.setId(id);
            service.updateBankAccount(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Bank account updated successfully!");
            return getDefaultRedirectPath();
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return view("edit");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while updating bank account!");
            return view("edit");
        }
    }

    /**
     * Delete bank account
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteBankAccount(id);
            redirectAttributes.addFlashAttribute("successMessage", "Bank account deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting bank account!");
        }
        return getDefaultRedirectPath();
    }

    /**
     * Toggle bank account status
     */
    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            BankAccount bankAccount = service.toggleBankAccountStatus(id);
            String status = bankAccount.getIsActive() ? "activated" : "deactivated";
            redirectAttributes.addFlashAttribute("successMessage", "Bank account " + status + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while changing status!");
        }
        return getDefaultRedirectPath();
    }

    /**
     * Show detail
     */
    @GetMapping("/detail/{id}")
    public String showDetail(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            BankAccount bankAccount = service.getBankAccountById(id);
            model.addAttribute("bankAccount", bankAccount);
            return view("detail");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bank account not found!");
            return getDefaultRedirectPath();
        }
    }
}
