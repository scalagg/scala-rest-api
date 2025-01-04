package gg.scala.restapi.account;

import gg.scala.restapi.profile.ProfileRepository;
import gg.scala.restapi.profile.ProfileService;
import gg.scala.restapi.util.UUIDCache;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/forum/account")
public class AccountController {
    private final AccountService accountService;
    private final ProfileService profileService;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    public AccountController(AccountService accountService, ProfileService profileService, JavaMailSender javaMailSender, PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.profileService = profileService;
        this.javaMailSender = javaMailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/sendRegistration/{uuid}")
    public ResponseEntity<Void> sendRegistration(@RequestBody InitialRegistrationRequest registrationRequest, @PathVariable String uuid) throws MessagingException {
        if (registrationRequest.email() == null || registrationRequest.token() == null) {
            return ResponseEntity.badRequest().build();
        }

        if (profileService.getById(uuid).isEmpty()) {
            System.out.println("Profile not found");
            return ResponseEntity.notFound().build();
        }

        if (accountService.getById(uuid).isPresent()) {
            System.out.println("Account already exists");
            return ResponseEntity.badRequest().build();
        }

        if (accountService.getByEmail(registrationRequest.email()).isPresent()) {
            System.out.println("Email already exists");
            return ResponseEntity.status(401).build();
        }

        if (accountService.getByToken(registrationRequest.token()).isPresent()) {
            System.out.println("Token already exists");
            return ResponseEntity.badRequest().build();
        }

        Account account = new Account(
                uuid,
                registrationRequest.email(),
                 null,
                 registrationRequest.token(), new Document());
        accountService.save(account);

        // TODO; send email
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("no-reply@scala.gg");
        helper.setTo(registrationRequest.email());
        helper.setSubject("Welcome to Solora");
        helper.setText("Welcome to Solora! Please click the following link to verify your account: http://localhost:8080/register?token=" + registrationRequest.token());

        javaMailSender.send(message);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegistrationRequest registrationRequest) {
        if (accountService.getByToken(registrationRequest.token()).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Account account = accountService.getByToken(registrationRequest.token()).get();
        if (account.getPassword() != null) {
            return ResponseEntity.badRequest().build();
        }

        account.setPassword(passwordEncoder.encode(registrationRequest.password()));
        accountService.save(account);

        System.out.println(account.getPassword());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/login/{username}")
    public ResponseEntity<Account> login(@PathVariable String username, @RequestParam(required = false) String password) {
        System.out.println("Username: " + username);
        if (username.contains("@")) {
            Optional<Account> account = accountService.getByEmail(username);
            System.out.println("Account: " + account.isPresent());
            if (account.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            if (password != null && !passwordEncoder.matches(password, account.get().getPassword())) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(account.get());
        }

        Optional<Account> account  = accountService.getById(UUIDCache.getUuid(username).toString());
        if (account.isEmpty()) {
            System.out.println("Account not found for " + username);
            return ResponseEntity.notFound().build();
        }

        if (password != null && !passwordEncoder.matches(password, account.get().getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(account.get());
    }

    @GetMapping("/token/{token}")
    public ResponseEntity<Account> getByToken(@PathVariable String token) {
        return ResponseEntity.ok(accountService.getByToken(token).orElse(null));
    }

    @PutMapping("/password/{uuid}")
    public ResponseEntity<Void> changePassword(@PathVariable String uuid, @RequestBody ChangePasswordRequest changePasswordRequest) {
        Optional<Account> account = accountService.getById(uuid);
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!passwordEncoder.matches(changePasswordRequest.currentPassword(), account.get().getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        account.get().setPassword(passwordEncoder.encode(changePasswordRequest.password()));
        accountService.save(account.get());

        return ResponseEntity.ok().build();
    }
}
