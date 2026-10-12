package ru.bobgroup.simpleapi.account;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(UUID id, BigDecimal balance) {

    static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getBalance());
    }
}
