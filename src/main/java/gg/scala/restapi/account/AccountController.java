package gg.scala.restapi.account;

import gg.scala.restapi.profile.ProfileRepository;
import gg.scala.restapi.profile.ProfileService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/forum/account")
public class AccountController {
    private final AccountService accountService;
    private final ProfileService profileService;
    private final JavaMailSender javaMailSender;
    private final ProfileRepository profileRepository;

    public AccountController(AccountService accountService, ProfileService profileService, JavaMailSender javaMailSender, ProfileRepository profileRepository) {
        this.accountService = accountService;
        this.profileService = profileService;
        this.javaMailSender = javaMailSender;
        this.profileRepository = profileRepository;
    }

    @PostMapping("/sendRegistration/{uuid}")
    public ResponseEntity<Void> sendRegistration(@RequestBody InitialRegistrationRequest registrationRequest, @PathVariable String uuid) throws MessagingException {
        if (registrationRequest.email() == null || registrationRequest.token() == null) {
            return ResponseEntity.badRequest().build();
        }

        profileRepository.findAll().forEach(profile -> {
            System.out.println(profile.getUniqueId());
        });
        if (profileService.getById(uuid).isEmpty()) {
            System.out.println("Profile not found");
            return ResponseEntity.notFound().build();
        }

        if (accountService.getById(uuid).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        if (accountService.getByEmail(registrationRequest.email()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        if (accountService.getByToken(registrationRequest.token()).isPresent()) {
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
        helper.setFrom("no-reply@mincats.eu");
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

        account.setPassword(registrationRequest.password());
        accountService.save(account);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/login/{username}")
    public ResponseEntity<Account> login(@PathVariable String username, @PathVariable(required = false) String password) {
        if (username.contains("@")) {
            Optional<Account> account = accountService.getByEmail(username);
            if (account.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            if (password != null && !Objects.equals(account.get().getPassword(), password)) {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(account.get());
        }

        // TODO: handle the username, do uuid lookups and sush
        //return ResponseEntity.ok(accountService.getById(UUID.randomUUID()).orElse(null));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/{token}")
    public ResponseEntity<Account> getByToken(@PathVariable String token) {
        return ResponseEntity.ok(accountService.getByToken(token).orElse(null));
    }
}
