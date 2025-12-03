package asterisk.sun.booking_tours.application.admin.bankaccount;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asterisk.sun.booking_tours.application.admin.bankaccount.dto.CreateBankAccountDTO;
import asterisk.sun.booking_tours.application.admin.bankaccount.dto.UpdateBankAccountDTO;
import asterisk.sun.booking_tours.application.admin.common.BaseServiceController;
import asterisk.sun.booking_tours.core.payment.BankAccount;
import asterisk.sun.booking_tours.core.payment.BankAccountRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class BankAccountAdminService extends BaseServiceController<BankAccountRepository> {

    public BankAccountAdminService(BankAccountRepository bankAccountRepository) {
        super(bankAccountRepository);
    }

    /**
     * Get all bank accounts
     */
    public List<BankAccount> getAllBankAccounts() {
        return repository.findAllOrderByDisplayOrder();
    }

    /**
     * Search bank accounts by keyword
     */
    public List<BankAccount> searchBankAccounts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAllOrderByDisplayOrder();
        }
        return repository.findByKeyword(keyword.trim());
    }

    /**
     * Get bank accounts by active status
     */
    public List<BankAccount> getBankAccountsByStatus(Boolean isActive) {
        return repository.findByIsActive(isActive);
    }

    /**
     * Get active bank accounts for customer display
     */
    public List<BankAccount> getActiveBankAccounts() {
        return repository.findByIsActive(true);
    }

    /**
     * Get bank account by ID
     */
    public BankAccount getBankAccountById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bank account not found with ID: " + id));
    }

    /**
     * Create new bank account
     */
    @Transactional
    public BankAccount createBankAccount(CreateBankAccountDTO dto) {
        // Check if account number already exists
        if (repository.existsByAccountNumber(dto.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists: " + dto.getAccountNumber());
        }

        BankAccount bankAccount = new BankAccount();
        bankAccount.setBankName(dto.getBankName());
        bankAccount.setAccountNumber(dto.getAccountNumber());
        bankAccount.setAccountHolder(dto.getAccountHolder());
        bankAccount.setBranch(dto.getBranch());
        bankAccount.setSwiftCode(dto.getSwiftCode());
        bankAccount.setDescription(dto.getDescription());
        bankAccount.setIsActive(dto.getIsActive());
        bankAccount.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);

        return repository.save(bankAccount);
    }

    /**
     * Update bank account
     */
    @Transactional
    public BankAccount updateBankAccount(UpdateBankAccountDTO dto) {
        BankAccount bankAccount = getBankAccountById(dto.getId());

        // Check if account number changed and already exists
        if (!bankAccount.getAccountNumber().equals(dto.getAccountNumber()) &&
            repository.existsByAccountNumber(dto.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists: " + dto.getAccountNumber());
        }

        bankAccount.setBankName(dto.getBankName());
        bankAccount.setAccountNumber(dto.getAccountNumber());
        bankAccount.setAccountHolder(dto.getAccountHolder());
        bankAccount.setBranch(dto.getBranch());
        bankAccount.setSwiftCode(dto.getSwiftCode());
        bankAccount.setDescription(dto.getDescription());
        bankAccount.setIsActive(dto.getIsActive());
        bankAccount.setDisplayOrder(dto.getDisplayOrder());

        return repository.save(bankAccount);
    }

    /**
     * Delete bank account (soft delete)
     */
    @Transactional
    public void deleteBankAccount(Long id) {
        BankAccount bankAccount = getBankAccountById(id);
        repository.delete(bankAccount);
    }

    /**
     * Toggle bank account status
     */
    @Transactional
    public BankAccount toggleBankAccountStatus(Long id) {
        BankAccount bankAccount = getBankAccountById(id);
        bankAccount.setIsActive(!bankAccount.getIsActive());
        return repository.save(bankAccount);
    }
}
