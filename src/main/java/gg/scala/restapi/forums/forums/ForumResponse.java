package gg.scala.restapi.forums.forums;

import gg.scala.restapi.forums.thread.ForumThread;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ForumResponse extends Forum {
    private List<ForumThreadResponse> threads;

    public ForumResponse(Forum forum, List<ForumThreadResponse> threads) {
        super(forum.getId(), forum.getName(), forum.getDescription(), forum.getWeight(), forum.isLocked(), forum.getCategory(), forum.getThreadAmount());
        this.threads = threads;
    }
}