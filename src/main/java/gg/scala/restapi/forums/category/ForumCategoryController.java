package gg.scala.restapi.forums.category;

import gg.scala.restapi.forums.forums.Forum;
import gg.scala.restapi.forums.forums.ForumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forum/category")
public class ForumCategoryController {
    private final ForumCategoryService forumCategoryService;
    private final ForumService forumService;

    public ForumCategoryController(ForumCategoryService forumCategoryService, ForumService forumService) {
        this.forumCategoryService = forumCategoryService;
        this.forumService = forumService;
    }

    @GetMapping
    public List<ForumCategoryResponse> getCategories() {
        return forumCategoryService.getCategories().stream().map(category -> {
            List<Forum> forums = forumService.getForumsByCategory(category.getId());
            return new ForumCategoryResponse(category, forums);
        }).toList();
    }

    @PostMapping
    public ResponseEntity<ForumCategory> createCategory(@RequestBody ForumCategory forumCategory) {

        if (forumCategoryService.getCategory(forumCategory.getId()) != null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(forumCategoryService.createCategory(forumCategory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ForumCategory> updateCategory(@PathVariable String id, @RequestBody ForumCategory forumCategory) {
        ForumCategory category = forumCategoryService.getCategory(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(forumCategoryService.updateCategory(id, forumCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        ForumCategory category = forumCategoryService.getCategory(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }

        forumCategoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
