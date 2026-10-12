package ru.bobgroup.simpleapi.account;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountServiceTest {

    @Test
    void createsAccountWithZeroBalance() {
        AccountService service = new AccountService(repository(ignored -> Optional.empty()));

        AccountResponse response = service.create();

        assertThat(response.id()).isNotNull();
        assertThat(response.balance()).isEqualByComparingTo("0.00");
    }

    @Test
    void depositsMoney() {
        UUID id = UUID.randomUUID();
        Account account = new Account(id);
        AccountService service = new AccountService(repository(ignored -> Optional.of(account)));

        AccountResponse response = service.deposit(id, new BigDecimal("125.50"));

        assertThat(response.balance()).isEqualByComparingTo("125.50");
    }

    @Test
    void returnsAccountBalance() {
        UUID id = UUID.randomUUID();
        Account account = new Account(id);
        account.deposit(new BigDecimal("75.25"));
        AccountService service = new AccountService(repository(ignored -> Optional.of(account)));

        AccountResponse response = service.getById(id);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.balance()).isEqualByComparingTo("75.25");
    }

    @Test
    void rejectsDepositForUnknownAccount() {
        UUID id = UUID.randomUUID();
        AccountService service = new AccountService(repository(ignored -> Optional.empty()));

        assertThatThrownBy(() -> service.deposit(id, BigDecimal.TEN))
                .isInstanceOf(AccountNotFoundException.class);
    }

    private AccountRepository repository(Function<UUID, Optional<Account>> accountLookup) {
        return (AccountRepository) Proxy.newProxyInstance(
                AccountRepository.class.getClassLoader(),
                new Class<?>[]{AccountRepository.class},
                (proxy, method, arguments) -> switch (method.getName()) {
                    case "save" -> arguments[0];
                    case "findById", "findByIdForUpdate" -> accountLookup.apply((UUID) arguments[0]);
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }
}
