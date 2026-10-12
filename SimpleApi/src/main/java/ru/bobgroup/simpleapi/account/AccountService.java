package ru.bobgroup.simpleapi.account;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountResponse create() {
        Account account = accountRepository.save(new Account(UUID.randomUUID()));
        return AccountResponse.from(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse getById(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        return AccountResponse.from(account);
    }

    @Transactional
    public AccountResponse deposit(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        account.deposit(amount.setScale(2, RoundingMode.UNNECESSARY));
        return AccountResponse.from(account);
    }
}
