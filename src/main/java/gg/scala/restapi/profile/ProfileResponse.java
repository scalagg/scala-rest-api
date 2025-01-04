package gg.scala.restapi.profile;

import gg.scala.restapi.forums.forums.ForumThreadResponse;
import gg.scala.restapi.profile.comments.Comment;
import gg.scala.restapi.profile.comments.CommentResponse;
import gg.scala.restapi.profile.grant.Grant;
import gg.scala.restapi.profile.punishment.Punishment;
import gg.scala.restapi.rank.Rank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.Document;
import org.springframework.lang.Nullable;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfileResponse extends Profile {
    private final @Nullable List<Grant> activeGrants;
    private final @Nullable Punishment activeBan;
    private final @Nullable Punishment activeMute; 
    private final @Nullable Rank rank;
    private final @Nullable Document webSettings;
    private final @Nullable List<String> effectivePermissions;
    private final @Nullable List<CommentResponse> comments;
    private final @Nullable List<ForumThreadResponse> threads;
    private final @Nullable List<String> badges;

    public ProfileResponse(Profile profile, List<Grant> activeGrants, @Nullable Punishment activeBan, @Nullable Punishment activeMute, Rank rank, Document webSettings, @Nullable List<String> effectivePermissions, @Nullable List<CommentResponse> comments, @Nullable List<ForumThreadResponse> threads, @Nullable List<String> badges) {
        super(profile.getUniqueId(), profile.getIgnoring(), profile.getIpAddress(), profile.getMetadata(), profile.getName(), profile.getPastIpAddresses(), profile.getSpecificPermissions(), profile.getTimestamp());
        this.activeGrants = activeGrants;
        this.activeBan = activeBan;
        this.activeMute = activeMute;
        this.rank = rank;
        this.webSettings = webSettings;
        this.effectivePermissions = effectivePermissions;
        this.comments = comments;
        this.threads = threads;
        this.badges = badges;
    }
}
