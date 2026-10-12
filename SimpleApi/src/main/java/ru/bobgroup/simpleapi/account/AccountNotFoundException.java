package ru.bobgroup.simpleapi.account;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(UUID accountId) {
        super("Account %s was not found".formatted(accountId));
    }
}
