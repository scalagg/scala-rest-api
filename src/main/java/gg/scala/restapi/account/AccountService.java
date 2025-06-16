package gg.scala.restapi.account;

import gg.scala.restapi.util.AppLogger;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    private final AppLogger logger = AppLogger.getLogger(AccountService.class);
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> getByToken(String token) {
        logger.debug("Fetching account by token: %s", token);
        Optional<Account> account = accountRepository.findByToken(token);
        if (account.isEmpty()) {
            logger.debug("Account not found for token: %s", token);
        } else {
            logger.debug("Account found for token: %s", token);
        }
        return account;
    }

    public Optional<Account> getByEmail(String email) {
        logger.debug("Fetching account by email: %s", email);
        Optional<Account> account = accountRepository.findByEmailIgnoreCase(email);
        if (account.isEmpty()) {
            logger.debug("Account not found for email: %s", email);
        } else {
            logger.debug("Account found for email: %s", email);
        }
        return account;
    }

    public Optional<Account> getById(String id) {
        logger.debug("Fetching account by ID: %s", id);
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            logger.debug("Account not found for ID: %s", id);
        } else {
            logger.debug("Account found for ID: %s", id);
        }
        return account;
    }

    public Account save(Account account) {
        logger.info("Saving account for UUID: %s", account.getUuid());
        return accountRepository.save(account);
    }
}
