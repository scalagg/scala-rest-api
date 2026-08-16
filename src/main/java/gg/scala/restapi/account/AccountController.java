package gg.scala.restapi.account;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import gg.scala.restapi.profile.ProfileService;
import gg.scala.restapi.server.ServerType;
import gg.scala.restapi.util.UUIDCache;
import gg.scala.restapi.util.AppLogger;
import gg.scala.restapi.webconfig.ProfileBannerRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Luna
 */
@RestController
@RequestMapping("/forum/account")
@Tag(name = "Account Management", description = "APIs for managing user accounts")
public class AccountController {
    private final AppLogger logger = AppLogger.getLogger(AccountController.class);
    private final AccountService accountService;
    private final ProfileService profileService;
    private final Resend resend;
    private final PasswordEncoder passwordEncoder;
    private final TotpService totpService;
    private final ProfileBannerRepository profileBannerRepository;

    @Value("${server.type}")
    private ServerType serverType;

    @Value("${resend.from-email}")
    private String fromEmail;

    @Value("classpath:email-register.html")
    private Resource emailRegister;

    @Value("classpath:email-reset.html")
    private Resource emailReset;

    public AccountController(AccountService accountService, ProfileService profileService, Resend resend, PasswordEncoder passwordEncoder, TotpService totpService, ProfileBannerRepository profileBannerRepository) {
        this.accountService = accountService;
        this.profileService = profileService;
        this.resend = resend;
        this.passwordEncoder = passwordEncoder;
        this.totpService = totpService;
        this.profileBannerRepository = profileBannerRepository;
    }

    @Operation(summary = "Send registration email", description = "Sends a registration confirmation email to the user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration email sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "401", description = "Email already exists"),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @PostMapping("/sendRegistration/{uuid}")
    public ResponseEntity<Void> sendRegistration(
            @Parameter(description = "Registration request containing email and token") @RequestBody InitialRegistrationRequest registrationRequest,
            @Parameter(description = "User UUID") @PathVariable String uuid) throws IOException, ResendException {
        logger.info("Registration email requested for UUID: %s, email: %s", uuid, registrationRequest.email());
        
        if (registrationRequest.email() == null || registrationRequest.token() == null) {
            logger.warn("Invalid registration request - missing email or token for UUID: %s", uuid);
            return ResponseEntity.badRequest().build();
        }

        if (profileService.getById(uuid).isEmpty()) {
            logger.warn("Profile not found for registration: %s", uuid);
            return ResponseEntity.notFound().build();
        }

        if (accountService.getById(uuid).isPresent()) {
            logger.warn("Account already exists for UUID: %s", uuid);
            return ResponseEntity.badRequest().build();
        }

        if (accountService.getByEmail(registrationRequest.email()).isPresent()) {
            logger.warn("Email already exists: %s", registrationRequest.email());
            return ResponseEntity.status(401).build();
        }

        if (accountService.getByToken(registrationRequest.token()).isPresent()) {
            logger.warn("Token already exists: %s", registrationRequest.token());
            return ResponseEntity.badRequest().build();
        }

        String username = UUIDCache.getName(UUID.fromString(uuid));
        Account account = new Account(
                uuid,
                registrationRequest.email(),
                null,
                registrationRequest.token(),
                new Document(),
                new ArrayList<>(),
                username,
                null,
                false,
                null
        );
        accountService.save(account);

        String htmlContent = new String(emailRegister.getInputStream().readAllBytes())
                .replace("{{siteName}}", serverType.getName())
                .replace("{{serverName}}", serverType.getName())
                .replace("{{websiteUrl}}", serverType.getUrl())
                .replace("{{confirmationUrl}}", serverType.getUrl() + "/register?token=" + registrationRequest.token());

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(registrationRequest.email())
                .subject("Confirm your %s Account".formatted(serverType.getName()))
                .html(htmlContent)
                .build();

        CreateEmailResponse response = resend.emails().send(params);
        logger.info("Registration email sent to: %s (ID: %s)", registrationRequest.email(), response.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/uuid/{uuid}")
    public ResponseEntity<Account> getAccountByUuid(@PathVariable String uuid) {
        Optional<Account> account = accountService.getById(uuid);
        return ResponseEntity.ok(account.orElse(new Account(uuid, null, null, null, new Document(), new ArrayList<>(), null, null, false, null)));
    }

    @Operation(summary = "Register user", description = "Completes the registration process for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registration completed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "404", description = "Token not found")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @Parameter(description = "Registration request containing token and password") @RequestBody RegistrationRequest registrationRequest) {
        if (accountService.getByToken(registrationRequest.token()).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Account account = accountService.getByToken(registrationRequest.token()).get();
        if (account.getPassword() != null) {
            return ResponseEntity.badRequest().build();
        }

        account.setPassword(passwordEncoder.encode(registrationRequest.password()));
     
        account.setToken(null);
        String username = UUIDCache.getName(UUID.fromString(account.getUuid()));
        account.setUsername(username);
        accountService.save(account);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "User login", description = "Authenticates a user and returns their account information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = Account.class))),
        @ApiResponse(responseCode = "400", description = "Invalid credentials"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/login/{username}")
    public ResponseEntity<Account> login(
            @Parameter(description = "Username or email") @PathVariable String username,
            @Parameter(description = "User password") @RequestParam(required = false) String password) {
        logger.info("Login attempt for username/email: %s", username);
        
        if (username.contains("@")) {
            Optional<Account> account = accountService.getByEmail(username);
            if (account.isEmpty()) {
                logger.warn("Login failed - email not found: %s", username);
                return ResponseEntity.notFound().build();
            }

            if (password != null && !passwordEncoder.matches(password, account.get().getPassword())) {
                logger.warn("Login failed - invalid password for email: %s", username);
                return ResponseEntity.badRequest().build();
            }

            logger.success("Login successful for email: %s", username);
            return ResponseEntity.ok(account.get());
        }

        Optional<Account> account  = accountService.getById(UUIDCache.getUuid(username).toString());
        if (account.isEmpty()) {
            logger.warn("Login failed - account not found for username: %s", username);
            return ResponseEntity.notFound().build();
        }

        if (password != null && !passwordEncoder.matches(password, account.get().getPassword())) {
            logger.warn("Login failed - invalid password for username: %s", username);
            return ResponseEntity.badRequest().build();
        }

        logger.success("Login successful for username: %s", username);
        return ResponseEntity.ok(account.get());
    }

    @Operation(summary = "Get account by token", description = "Retrieves an account using a token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account found", content = @Content(schema = @Schema(implementation = Account.class))),
        @ApiResponse(responseCode = "404", description = "Token not found")
    })
    @GetMapping("/token/{token}")
    public ResponseEntity<Account> getByToken(
            @Parameter(description = "Account token") @PathVariable String token) {
        return ResponseEntity.ok(accountService.getByToken(token).orElse(null));
    }

    @Operation(summary = "Change password", description = "Updates a user's password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid current password"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PutMapping("/password/{uuid}")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "User UUID") @PathVariable String uuid,
            @Parameter(description = "Password change request") @RequestBody ChangePasswordRequest changePasswordRequest) {
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

    @Operation(summary = "Send password reset email", description = "Sends a password reset email to the user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset email sent successfully"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PostMapping("/sendResetPassword")
    public ResponseEntity<Void> sendResetPassword(
            @Parameter(description = "Password reset request containing email") @RequestBody ResetPasswordRequest resetPasswordRequest) throws IOException, ResendException {
        Optional<Account> account = accountService.getByEmail(resetPasswordRequest.email());
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        account.get().setToken(UUID.randomUUID().toString().substring(0, 8));
        accountService.save(account.get());

        String htmlContent = new String(emailReset.getInputStream().readAllBytes())
                .replace("{{siteName}}", serverType.getName())
                .replace("{{serverName}}", serverType.getName())
                .replace("{{websiteUrl}}", serverType.getUrl())
                .replace("{{resetPasswordUrl}}", serverType.getUrl() + "/reset-password?token=" + account.get().getToken());

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(resetPasswordRequest.email())
                .subject("Password Reset")
                .html(htmlContent)
                .build();

        CreateEmailResponse response = resend.emails().send(params);
        logger.info("Password reset email sent to: %s (ID: %s)", resetPasswordRequest.email(), response.getId());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reset password", description = "Resets a user's password using a token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @ApiResponse(responseCode = "404", description = "Token not found")
    })
    @PostMapping("/resetPassword/{token}")
    public ResponseEntity<Void> resetPassword(
            @Parameter(description = "Reset token") @PathVariable String token,
            @Parameter(description = "Password reset confirmation request") @RequestBody ConfirmPasswordResetRequest confirmPasswordResetRequest) {
        Optional<Account> account = accountService.getByToken(token);
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        account.get().setPassword(passwordEncoder.encode(confirmPasswordResetRequest.password()));
        account.get().setToken(null);
        accountService.save(account.get());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Add badge", description = "Adds a badge to a user's account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Badge added successfully"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PostMapping("/badge")
    public ResponseEntity<Void> addBadge(
            @Parameter(description = "Badge request containing UUID and badge ID") @RequestBody BadgeGivenRequest addBadgeRequest) {
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

    @Operation(summary = "Remove badge", description = "Removes a badge from a user's account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Badge removed successfully"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PostMapping("/badge/remove")
    public ResponseEntity<Void> removeBadge(
            @Parameter(description = "Badge removal request containing UUID and badge ID") @RequestBody BadgeGivenRequest removeBadgeRequest) {
        Optional<Account> account = accountService.getById(removeBadgeRequest.uuid());
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        account.get().getBadges().remove(removeBadgeRequest.badgeId());
        accountService.save(account.get());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/email/{uuid}")
    public ResponseEntity<Void> changeEmail(@PathVariable String uuid, @RequestBody ChangeEmailRequest request) {
        Optional<Account> account = accountService.getById(uuid);
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (request.email() == null || !request.email().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                || request.currentPassword() == null
                || !passwordEncoder.matches(request.currentPassword(), account.get().getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Account> existing = accountService.getByEmail(request.email());
        if (existing.isPresent() && !existing.get().getUuid().equals(uuid)) {
            return ResponseEntity.status(409).build();
        }
        account.get().setEmail(request.email().trim().toLowerCase());
        accountService.save(account.get());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tfa/setup/{uuid}")
    public ResponseEntity<TfaSetupResponse> setupTfa(@PathVariable String uuid, @RequestBody TfaSetupRequest request) {
        Optional<Account> account = accountService.getById(uuid);
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (account.get().isTfaEnabled()) {
            return ResponseEntity.status(409).build();
        }
        if (request.currentPassword() == null || !passwordEncoder.matches(request.currentPassword(), account.get().getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        String secret = totpService.createSecret();
        account.get().setTfaSecret(secret);
        accountService.save(account.get());
        String issuer = URLEncoder.encode(serverType.getName(), StandardCharsets.UTF_8);
        String label = URLEncoder.encode(serverType.getName() + ":" + account.get().getUsername(), StandardCharsets.UTF_8);
        return ResponseEntity.ok(new TfaSetupResponse(secret, "otpauth://totp/" + label + "?secret=" + secret + "&issuer=" + issuer));
    }

    @PostMapping("/tfa/enable/{uuid}")
    public ResponseEntity<Void> enableTfa(@PathVariable String uuid, @RequestBody TfaCodeRequest request) {
        Optional<Account> account = accountService.getById(uuid);
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (request.currentPassword() == null
                || !passwordEncoder.matches(request.currentPassword(), account.get().getPassword())
                || !totpService.verify(account.get().getTfaSecret(), request.code())) {
            return ResponseEntity.badRequest().build();
        }
        account.get().setTfaEnabled(true);
        accountService.save(account.get());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tfa/disable/{uuid}")
    public ResponseEntity<Void> disableTfa(@PathVariable String uuid, @RequestBody TfaCodeRequest request) {
        Optional<Account> account = accountService.getById(uuid);
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (request.currentPassword() == null
                || !passwordEncoder.matches(request.currentPassword(), account.get().getPassword())
                || !totpService.verify(account.get().getTfaSecret(), request.code())) {
            return ResponseEntity.badRequest().build();
        }
        account.get().setTfaEnabled(false);
        account.get().setTfaSecret(null);
        accountService.save(account.get());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tfa/verify/{uuid}")
    public ResponseEntity<Void> verifyTfa(@PathVariable String uuid, @RequestBody TfaVerifyRequest request) {
        Optional<Account> account = accountService.getById(uuid);
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return totpService.verify(account.get().getTfaSecret(), request.code())
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().build();
    }

    @PutMapping("/banner/{uuid}")
    public ResponseEntity<Void> selectBanner(@PathVariable String uuid, @RequestBody SelectBannerRequest request) {
        Optional<Account> account = accountService.getById(uuid);
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (request.bannerId() != null && !profileBannerRepository.existsById(request.bannerId())) {
            return ResponseEntity.badRequest().build();
        }
        account.get().setProfileBannerId(request.bannerId());
        accountService.save(account.get());
        return ResponseEntity.ok().build();
    }
}
