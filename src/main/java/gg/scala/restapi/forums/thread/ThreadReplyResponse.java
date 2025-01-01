package gg.scala.restapi.forums.thread;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ThreadReplyResponse extends ThreadReply {
    private String authorColor;
    private List<ThreadReplyResponse> replies;

    public ThreadReplyResponse(ThreadReply reply, String authorColor, List<ThreadReplyResponse> replies) {
        super(reply.getUuid(), reply.getAuthor(), reply.getThread(), reply.getComment(), reply.getCreatedAt(), reply.getLastEditedAt(), reply.getParentReplyId());
        this.authorColor = authorColor;
        this.replies = replies;
    }
}
