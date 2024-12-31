package gg.scala.restapi.util;

import gg.scala.restapi.forums.forums.Forum;
import gg.scala.restapi.forums.forums.ForumService;
import gg.scala.restapi.forums.forums.ForumThreadResponse;
import gg.scala.restapi.forums.thread.ForumThread;
import gg.scala.restapi.profile.Profile;
import gg.scala.restapi.profile.ProfileService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ForumThreadResponseUtil {
    private final ProfileService profileService;
    private final ForumService forumService;

    public ForumThreadResponseUtil(ProfileService profileService, ForumService forumService) {
        this.profileService = profileService;
        this.forumService = forumService;
    }

    public ForumThreadResponse transform(ForumThread thread) {
        String authorName = null;
        String authorColor = null;
        String forumName = null;
        String lastEditedByName = null;
        String lastEditedByColor = null;

        Optional<Profile> optProfile = profileService.getById(thread.getAuthor());
        if (optProfile.isPresent()) {
            authorName = UUIDCache.getName(UUID.fromString(optProfile.get().getUniqueId()));
            authorColor = profileService.getActiveRank(optProfile.get().getUniqueId()).getColor();
        }

        Optional<Forum> optForum = Optional.ofNullable(forumService.getForum(thread.getForum()));
        if (optForum.isPresent()) {
            forumName = optForum.get().getName();
        }

        if (thread.getLastEditedBy() != null) {
            Optional<Profile> lastEditedByProfile = profileService.getById(thread.getLastEditedBy());
            if (lastEditedByProfile.isPresent()) {
                lastEditedByName = UUIDCache.getName(UUID.fromString(lastEditedByProfile.get().getUniqueId()));
                lastEditedByColor = profileService.getActiveRank(lastEditedByProfile.get().getUniqueId()).getColor();
            }
        }

        return new ForumThreadResponse(thread, authorName, authorColor, forumName, lastEditedByName, lastEditedByColor);
    }
}
