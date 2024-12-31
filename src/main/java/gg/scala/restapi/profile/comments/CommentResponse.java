package gg.scala.restapi.profile.comments;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentResponse extends Comment {
    private final String authorName;
    private final String authorColor;

    public CommentResponse(Comment comment, String authorName, String authorColor) {
        super(comment.getUuid(), comment.getAuthor(), comment.getContent(), comment.getAddedAt(), comment.getProfileId());
        this.authorName = authorName;
        this.authorColor = authorColor;
    }
}
