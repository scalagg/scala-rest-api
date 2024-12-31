package gg.scala.restapi.forums.category;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumCategoryService {
    private final ForumCategoryRepository forumCategoryRepository;

    public ForumCategoryService(ForumCategoryRepository forumCategoryRepository) {
        this.forumCategoryRepository = forumCategoryRepository;
    }

    public List<ForumCategory> getCategories() {
        return forumCategoryRepository.findAll();
    }

    public ForumCategory createCategory(ForumCategory forumCategory) {
        return forumCategoryRepository.save(forumCategory);
    }

    public ForumCategory getCategory(String id) {
        return forumCategoryRepository.findById(id).orElse(null);
    }

    public ForumCategory updateCategory(String id, ForumCategory forumCategory) {
        ForumCategory category = forumCategoryRepository.findById(id).orElse(null);
        if (category == null) {
            return null;
        }

        category.setName(forumCategory.getName());
        category.setWeight(forumCategory.getWeight());
        return forumCategoryRepository.save(category);
    }

    public void deleteCategory(String id) {
        forumCategoryRepository.deleteById(id);
    }
}
