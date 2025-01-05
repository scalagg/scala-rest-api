package gg.scala.restapi.profile;

import gg.scala.restapi.account.Account;
import gg.scala.restapi.account.AccountService;
import gg.scala.restapi.forums.forums.ForumThreadResponse;
import gg.scala.restapi.forums.thread.ForumThreadService;
import gg.scala.restapi.profile.comments.Comment;
import gg.scala.restapi.profile.comments.CommentResponse;
import gg.scala.restapi.profile.comments.CommentService;
import gg.scala.restapi.profile.friend.FriendResponse;
import gg.scala.restapi.profile.friend.FriendService;
import gg.scala.restapi.profile.grant.Grant;
import gg.scala.restapi.profile.grant.GrantRepository;
import gg.scala.restapi.profile.grant.GrantService;
import gg.scala.restapi.profile.punishment.Punishment;
import gg.scala.restapi.profile.punishment.PunishmentService;
import gg.scala.restapi.rank.Rank;
import gg.scala.restapi.rank.RankService;
import gg.scala.restapi.util.ForumThreadResponseUtil;
import gg.scala.restapi.util.UUIDCache;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final GrantService grantService;
    private final RankService rankService;
    private final AccountService accountService;
    private final PunishmentService punishmentService;
    private final CommentService commentService;
    private final ForumThreadService forumThreadService;
    private final ForumThreadResponseUtil forumThreadResponseUtil;
    private final FriendService friendService;

    public ProfileController(ProfileService profileService, GrantService grantService, RankService rankService, AccountService accountService, PunishmentService punishmentService, CommentService commentService, ForumThreadService forumThreadService, ForumThreadResponseUtil forumThreadResponseUtil, FriendService friendService) {
        this.profileService = profileService;
        this.grantService = grantService;
        this.rankService = rankService;
        this.accountService = accountService;
        this.punishmentService = punishmentService;
        this.commentService = commentService;
        this.forumThreadService = forumThreadService;
        this.forumThreadResponseUtil = forumThreadResponseUtil;
        this.friendService = friendService;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String uuid, @RequestParam(name = "webResolved", defaultValue = "false") boolean web, @RequestParam(name = "includePermissions", defaultValue = "false") boolean perms) {
        Optional<Profile> profile = profileService.getById(uuid);
        if (profile.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Grant> activeGrants = null;
        Punishment activeBan = null;
        Punishment activeMute = null;
        Rank rank = null;
        Document webSettings = null;
        List<String> effectivePermissions = null;
        List<CommentResponse> comments = null;
        List<ForumThreadResponse> threads = null;
        List<String> badges = null;
        List<FriendResponse> friends = new ArrayList<>();

        if (web) {
            activeGrants = grantService.getAllActiveGrants(uuid);
            rank = profileService.getActiveRank(activeGrants, uuid);

            List<Punishment> activePunishments = punishmentService.getActivePunishments(uuid);

            if (!activePunishments.isEmpty()) {
                for (Punishment punishment : activePunishments) {
                    if (punishment.getCategory().contains("BAN")) {
                        activeBan = punishment;
                    } else if (punishment.getCategory().contains("MUTE")) {
                        activeMute = punishment;
                    }
                }
            }

            Optional<Account> account = accountService.getById(uuid);
            if (account.isPresent()) {
                webSettings = account.get().getSettings();
                badges = account.get().getBadges();
            }

            Rank finalRank = rank;
            comments = commentService.getComments(uuid).stream().map(
                    comment -> new CommentResponse(
                            comment,
                            UUIDCache.getName(UUID.fromString(comment.getAuthor())),
                            finalRank.getColor()
                    )
            ).toList();

            friends = friendService.findAllForPlayer(uuid).stream().map(friend ->
                        new FriendResponse(friend.getPlayer1(), friend.getPlayer2(), UUIDCache.getName(UUID.fromString(friend.getPlayer1())), UUIDCache.getName(UUID.fromString(friend.getPlayer2())), friend.getStartedOn())
                    ).toList();

            threads = forumThreadService.getThreadsByAuthor(uuid).stream().map(forumThreadResponseUtil::transform).toList();

        }

        if (perms) {
            // TODO: this
            if (activeGrants != null) {
                effectivePermissions = new ArrayList<>();
                List<String> checkedRanks = new ArrayList<>();
                for (Grant grant : activeGrants) {
                    if (checkedRanks.contains(grant.getRankId())) {
                        continue;
                    }

                    Rank grantedRank = rankService.getById(grant.getRankId()).get();
                    effectivePermissions.addAll(grantedRank.getPermissions());

                    // TODO: Temp solution we should make something recursive for all the kids
                    checkedRanks.add(grant.getRankId());
                }

                effectivePermissions.addAll(profile.get().getSpecificPermissions());
            } else {
                effectivePermissions = new ArrayList<>(profile.get().getSpecificPermissions());
            }
        }

        return ResponseEntity.ok(new ProfileResponse(profile.get(), activeGrants, activeBan, activeMute, rank, webSettings, effectivePermissions, comments, threads, badges, friends));
    }
}
