package gg.scala.restapi.account;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> getByToken(String token) {
        return accountRepository.findByToken(token);
    }

    public Optional<Account> getByEmail(String email) {
        return accountRepository.findByEmailIgnoreCase(email);
    }

    public Optional<Account> getById(String id) {
        return accountRepository.findById(id);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }
}
