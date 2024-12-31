package gg.scala.restapi.forums.forums;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumService {
    private final ForumRepository forumRepository;

    public ForumService(ForumRepository forumRepository) {
        this.forumRepository = forumRepository;
    }

    public Forum save(Forum forum) {
        return forumRepository.save(forum);
    }

    public void delete(Forum forum) {
        forumRepository.delete(forum);
    }

    public Forum getForum(String id) {
        return forumRepository.findById(id).orElse(null);
    }

    public List<Forum> getForumsByCategory(String categoryId) {
        return forumRepository.findAllByCategory(categoryId);
    }

    public Forum getForumByNameOrId(String nameOrId) {
        return forumRepository.findByNameIgnoreCaseOrIdIgnoreCase(nameOrId, nameOrId);
    }

    public Forum updateForum(String id, Forum forum) {
        Forum existing = forumRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        existing.setName(forum.getName());
        existing.setDescription(forum.getDescription());
        existing.setCategory(forum.getCategory());
        existing.setWeight(forum.getWeight());
        return forumRepository.save(existing);
    }
}
