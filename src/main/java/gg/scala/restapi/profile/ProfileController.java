package gg.scala.restapi.profile;

import gg.scala.restapi.account.Account;
import gg.scala.restapi.account.AccountService;
import gg.scala.restapi.profile.grant.Grant;
import gg.scala.restapi.profile.grant.GrantRepository;
import gg.scala.restapi.profile.grant.GrantService;
import gg.scala.restapi.rank.Rank;
import gg.scala.restapi.rank.RankService;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final GrantService grantService;
    private final RankService rankService;
    private final AccountService accountService;

    public ProfileController(ProfileService profileService, GrantService grantService, RankService rankService, AccountService accountService) {
        this.profileService = profileService;
        this.grantService = grantService;
        this.rankService = rankService;
        this.accountService = accountService;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String uuid, @RequestParam(name = "webResolved", defaultValue = "false") boolean web, @RequestParam(name = "includePermissions", defaultValue = "false") boolean perms) {
        Optional<Profile> profile = profileService.getById(uuid);
        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Grant> activeGrants = null;
        Rank rank = null;
        Document webSettings = null;
        List<String> effectivePermissions = null;

        if (web) {
            activeGrants = grantService.getAllActiveGrants(uuid);
            rank = profileService.getActiveRank(activeGrants, uuid);

            Optional<Account> account = accountService.getById(uuid);
            if (account.isPresent()) {
                webSettings = account.get().getSettings();
            }
        }

        if (perms) {
            // TODO: this
            effectivePermissions = List.of();
        }

        return ResponseEntity.ok(new ProfileResponse(profile.get(), activeGrants, rank, webSettings, effectivePermissions));
    }
}
