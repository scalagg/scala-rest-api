package gg.scala.restapi.profile;

import com.fasterxml.jackson.databind.JsonNode;
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
import gg.scala.restapi.profile.notification.Notification;
import gg.scala.restapi.profile.notification.NotificationRepository;
import gg.scala.restapi.profile.punishment.Punishment;
import gg.scala.restapi.profile.punishment.PunishmentService;
import gg.scala.restapi.rank.Rank;
import gg.scala.restapi.rank.RankService;
import gg.scala.restapi.util.ForumThreadResponseUtil;
import gg.scala.restapi.util.UUIDCache;
import gg.scala.restapi.cache.OnlinePlayerCache;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;

@RestController
@RequestMapping("/profile")
@Tag(name = "Profile Management", description = "APIs for managing user profiles")
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
    private final KFriendsRepository kFriendsRepository;
    private final NotificationRepository notificationRepository;
    private final OnlinePlayerCache onlinePlayerCache;

    public ProfileController(ProfileService profileService, GrantService grantService, RankService rankService, AccountService accountService, PunishmentService punishmentService, CommentService commentService, ForumThreadService forumThreadService, ForumThreadResponseUtil forumThreadResponseUtil, FriendService friendService, KFriendsRepository kFriendsRepository, NotificationRepository notificationRepository, OnlinePlayerCache onlinePlayerCache) {
        this.profileService = profileService;
        this.grantService = grantService;
        this.rankService = rankService;
        this.accountService = accountService;
        this.punishmentService = punishmentService;
        this.commentService = commentService;
        this.forumThreadService = forumThreadService;
        this.forumThreadResponseUtil = forumThreadResponseUtil;
        this.friendService = friendService;
        this.kFriendsRepository = kFriendsRepository;
        this.notificationRepository = notificationRepository;
        this.onlinePlayerCache = onlinePlayerCache;
    }

    @Operation(summary = "Get profile", description = "Retrieves a user's profile with optional web-specific data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile found", content = @Content(schema = @Schema(implementation = ProfileResponse.class))),
        @ApiResponse(responseCode = "404", description = "Profile not found")
    })
    @GetMapping("/{uuid}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String uuid, @RequestParam(name = "webResolved", defaultValue = "false") boolean web, @RequestParam(name = "includePermissions", defaultValue = "false") boolean perms) {
        Optional<Profile> profile = profileService.getById(uuid);
        if (profile.isEmpty()) {
            Profile notFoundProfile = new Profile();
            notFoundProfile.setUniqueId(uuid);
            notFoundProfile.setName("USER NOT FOUND");
            ProfileResponse notFoundResponse = new ProfileResponse(
                notFoundProfile,
                null, null, null, null, null, null, null, null, null, null, null
            );
            return ResponseEntity.status(404).body(notFoundResponse);
        }

        List<Grant> activeGrants = null;
        Punishment activeBan = null;
        Punishment activeMute = null;
        Rank rank = null;
        Document webSettings;
        List<String> effectivePermissions = null;
        List<CommentResponse> comments = null;
        List<ForumThreadResponse> threads = null;
        List<String> badges = null;
        List<FriendResponse> friends = new ArrayList<>();
        List<Notification> notifi = new ArrayList<>();

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
                kFriendsRepository.findById(uuid).ifPresent(kFriendsPlayer -> {
                    for (String key : kFriendsPlayer.getSocialMedia().keySet()) {
                        webSettings.put(key, kFriendsPlayer.getSocialMedia().get(key));
                    }
                });

                badges = account.get().getBadges();
            } else {
                webSettings = null;
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
            notifi = notificationRepository.findByTarget(uuid);
        } else {
            webSettings = null;
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

        return ResponseEntity.ok(new ProfileResponse(profile.get(), activeGrants, activeBan, activeMute, rank, webSettings, effectivePermissions, comments, threads, badges, friends, notifi));
    }

    @Operation(summary = "Get public connections", description = "Retrieves a user's public social media connections")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connections retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/connections/{uuid}")
    public ResponseEntity<Map<String, String>> getPublicConnections(@PathVariable String uuid) {
        Optional<KFriendsPlayer> player = kFriendsRepository.findById(uuid);
        if (player.isEmpty()) {
            // Return empty map if user not found
            // return a error with a msg
            return ResponseEntity.status(404).body(Map.of("error", "User not found / No connections found"));
        }

        // Convert MongoDB Document to Map<String, String>
        Document socialMedia = player.get().getSocialMedia();
        Map<String, String> connections = new HashMap<>();
        socialMedia.forEach((key, value) -> {
            if (value != null) {
                connections.put(key.toString(), value.toString());
            }
        });

        return ResponseEntity.ok(connections);
    }

    @Operation(summary = "Update settings", description = "Updates a user's social media settings")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Settings updated successfully", content = @Content(schema = @Schema(implementation = Account.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/settings/{uuid}")
    public ResponseEntity<Account> updateSettings(@PathVariable String uuid, @RequestBody JsonNode settings) {
        Optional<KFriendsPlayer> player = kFriendsRepository.findById(uuid);
        if (player.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Document socialMedia = player.get().getSocialMedia();
        if (settings.has("YouTube")) {
            socialMedia.put("YouTube", settings.get("YouTube").asText());
        }

        if (settings.has("Twitter")) {
            socialMedia.put("Twitter", settings.get("Twitter").asText());
        }

        if (settings.has("Twitch")) {
            socialMedia.put("Twitch", settings.get("Twitch").asText());
        }

        if (settings.has("Telegram")) {
            socialMedia.put("Telegram", settings.get("Telegram").asText());
        }

        if (settings.has("Discord")) {
            socialMedia.put("Discord", settings.get("Discord").asText());
        }

        if (settings.has("GitHub")) {
            socialMedia.put("GitHub", settings.get("GitHub").asText());
        }

        kFriendsRepository.save(player.get());

        Account account = accountService.getById(uuid).get();
        account.setSettings(socialMedia);

        return ResponseEntity.ok(account);
    }

    @Operation(summary = "Update privacy settings", description = "Updates a user's privacy settings")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Privacy settings updated successfully", content = @Content(schema = @Schema(implementation = Account.class))),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PutMapping("/updatePrivacySettings/{uuid}")
    public ResponseEntity<Account> updatePrivacySettings(@PathVariable String uuid, @RequestBody JsonNode settings) {
        Optional<Account> account = accountService.getById(uuid);
        if (account.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Document privacySettings = account.get().getSettings();
        if (settings.has("COMMENT_STATUS")) {
            privacySettings.put("COMMENT_STATUS", settings.get("COMMENT_STATUS").asText());
        }

        if (settings.has("ONLINE_STATUS")) {
            privacySettings.put("ONLINE_STATUS", settings.get("ONLINE_STATUS").asText());
        }

        if (settings.has("STAFF_PAGE_STATUS")) {
            privacySettings.put("STAFF_PAGE_STATUS", settings.get("STAFF_PAGE_STATUS").asText());
        }

        account.get().setSettings(privacySettings);
        accountService.save(account.get());

        Optional<KFriendsPlayer> player = kFriendsRepository.findById(uuid);
        player.ifPresent(kFriendsPlayer -> {
            privacySettings.putAll(kFriendsPlayer.getSocialMedia());
        });

        return ResponseEntity.ok(account.get());
    }

    @Operation(summary = "Get online players", description = "Retrieves a map of online players and their current servers")
    @ApiResponse(responseCode = "200", description = "Online players retrieved successfully", 
                content = @Content(schema = @Schema(implementation = Map.class)))
    @GetMapping("/online")
    public ResponseEntity<Map<String, String>> getOnlinePlayers() {
        return ResponseEntity.ok(onlinePlayerCache.getOnlinePlayers());
    }
}
