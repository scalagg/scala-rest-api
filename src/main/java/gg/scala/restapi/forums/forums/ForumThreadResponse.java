package gg.scala.restapi.forums.forums;

import gg.scala.restapi.forums.thread.ForumThread;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ForumThreadResponse extends ForumThread {
    private String authorName;
    private String authorColor;
    private String forumName;
    private String lastEditedByName;
    private String lastEditedByColor;

    public ForumThreadResponse(ForumThread thread, String authorName, String authorColor, String forumName, String lastEditedByName, String lastEditedByColor) {
        super(thread.getId(), thread.getTitle(), thread.getBody(), thread.getForum(), thread.getAuthor(), thread.getCreatedAt(), thread.getLastEditedBy(), thread.getLastEditedAt(), thread.getLastReplyAt(), thread.isPinned(), thread.isLocked(), thread.getParentThreadId(), thread.getImageUrl());
        this.authorName = authorName;
        this.authorColor = authorColor;
        this.forumName = forumName;
        this.lastEditedByName = lastEditedByName;
        this.lastEditedByColor = lastEditedByColor;
    }
}
