package gg.scala.restapi.profile.comments;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getComments(String profileId) {
        return commentRepository.findAllByProfileId(profileId);
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment getComment(String id) {
        return commentRepository.findById(id).orElse(null);
    }

    public void deleteComment(String id) {
        commentRepository.deleteById(id);
    }
}
