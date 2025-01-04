package gg.scala.restapi.account;

import gg.scala.restapi.profile.ProfileService;
import gg.scala.restapi.server.ServerType;
import gg.scala.restapi.util.UUIDCache;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/forum/account")
public class AccountController {
    private final AccountService accountService;
    private final ProfileService profileService;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${server.type}")
    private ServerType serverType;

    @Value("classpath:email-register.html")
    private Resource emailRegister;

    @Value("classpath:email-reset.html")
    private Resource emailReset;

    public AccountController(AccountService accountService, ProfileService profileService, JavaMailSender javaMailSender, PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.profileService = profileService;
        this.javaMailSender = javaMailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/sendRegistration/{uuid}")
    public ResponseEntity<Void> sendRegistration(@RequestBody InitialRegistrationRequest registrationRequest, @PathVariable String uuid) throws MessagingException, IOException {
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
                 registrationRequest.token(), new Document(), new ArrayList<>());
        accountService.save(account);

        // TODO; send email
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("no-reply@scala.gg");
        helper.setTo(registrationRequest.email());
        helper.setSubject("Welcome to %s".formatted(serverType.getName()));

        helper.setText(
                new String(emailRegister.getInputStream().readAllBytes())
                        .replace("{{siteName}}", serverType.getName())
                        .replace("{{serverName}}", serverType.getName())
                        .replace("{{websiteUrl}}", serverType.getUrl())
                        .replace("{{confirmationUrl}}", serverType.getUrl() + "/register?token=" + registrationRequest.token()), true);

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
        account.setToken(null);
        accountService.save(account);

        System.out.println(account.getPassword());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/login/{username}")
    public ResponseEntity<Account> login(@PathVariable String username, @RequestParam(required = false) String password) {
        if (username.contains("@")) {
            Optional<Account> account = accountService.getByEmail(username);
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

    @PostMapping("/sendResetPassword")
    public ResponseEntity<Void> sendResetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws MessagingException, IOException {
        Optional<Account> account = accountService.getByEmail(resetPasswordRequest.email());
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        account.get().setToken(UUID.randomUUID().toString().substring(0, 8));
        accountService.save(account.get());

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("no-reply@scala.gg");
        helper.setTo(resetPasswordRequest.email());
        helper.setSubject("Password Reset");

        helper.setText(
                new String(emailReset.getInputStream().readAllBytes())
                        .replace("{{siteName}}", serverType.getName())
                        .replace("{{serverName}}", serverType.getName())
                        .replace("{{websiteUrl}}", serverType.getUrl())
                        .replace("{{resetPasswordUrl}}", serverType.getUrl() + "/reset-password?token=" + account.get().getToken()), true);

        javaMailSender.send(message);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/resetPassword/{token}")
    public ResponseEntity<Void> resetPassword(@PathVariable String token, @RequestBody ConfirmPasswordResetRequest confirmPasswordResetRequest) {
        Optional<Account> account = accountService.getByToken(token);
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        account.get().setPassword(passwordEncoder.encode(confirmPasswordResetRequest.password()));
        account.get().setToken(null);
        accountService.save(account.get());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/badge")
    public ResponseEntity<Void> addBadge(@RequestBody BadgeGivenRequest addBadgeRequest) {
        Optional<Account> account = accountService.getById(addBadgeRequest.uuid());
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (account.get().getBadges() == null) {
            account.get().setBadges(new ArrayList<>());
        }

        account.get().getBadges().add(addBadgeRequest.badgeId());
        accountService.save(account.get());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/badge/remove")
    public ResponseEntity<Void> removeBadge(@RequestBody BadgeGivenRequest removeBadgeRequest) {
        Optional<Account> account = accountService.getById(removeBadgeRequest.uuid());
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        account.get().getBadges().remove(removeBadgeRequest.badgeId());
        accountService.save(account.get());

        return ResponseEntity.ok().build();
    }
}
