package gg.scala.restapi.forums.category;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forum/category")
public class ForumCategoryController {
    private final ForumCategoryService forumCategoryService;

    public ForumCategoryController(ForumCategoryService forumCategoryService) {
        this.forumCategoryService = forumCategoryService;
    }

    @GetMapping
    public List<ForumCategory> getCategories() {
        return forumCategoryService.getCategories();
    }

    @PostMapping
    public ResponseEntity<ForumCategory> createCategory(ForumCategory forumCategory) {
        if (forumCategoryService.getCategory(forumCategory.getId()) != null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(forumCategoryService.createCategory(forumCategory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ForumCategory> updateCategory(@PathVariable String id, ForumCategory forumCategory) {
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
