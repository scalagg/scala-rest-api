package gg.scala.restapi.forums.category;

import gg.scala.restapi.forums.forums.Forum;
import gg.scala.restapi.forums.forums.ForumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@RestController
@RequestMapping("/forum/category")
@Tag(name = "Forum Category Management", description = "APIs for managing forum categories and organization")
public class ForumCategoryController {
    private final ForumCategoryService forumCategoryService;
    private final ForumService forumService;

    public ForumCategoryController(ForumCategoryService forumCategoryService, ForumService forumService) {
        this.forumCategoryService = forumCategoryService;
        this.forumService = forumService;
    }

    @Operation(summary = "Get categories", description = "Retrieves all forum categories with their associated forums")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully", content = @Content(schema = @Schema(implementation = ForumCategoryResponse.class)))
    })
    @GetMapping
    public List<ForumCategoryResponse> getCategories() {
        return forumCategoryService.getCategories().stream().map(category -> {
            List<Forum> forums = forumService.getForumsByCategory(category.getId());
            return new ForumCategoryResponse(category, forums);
        }).toList();
    }

    @Operation(summary = "Create category", description = "Creates a new forum category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category created successfully", content = @Content(schema = @Schema(implementation = ForumCategory.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or category already exists")
    })
    @PostMapping
    public ResponseEntity<ForumCategory> createCategory(
            @Parameter(description = "Category creation request") @RequestBody ForumCategory forumCategory) {
        if (forumCategoryService.getCategory(forumCategory.getId()) != null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(forumCategoryService.createCategory(forumCategory));
    }

    @Operation(summary = "Update category", description = "Updates an existing forum category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category updated successfully", content = @Content(schema = @Schema(implementation = ForumCategory.class))),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ForumCategory> updateCategory(
            @Parameter(description = "Category ID") @PathVariable String id,
            @Parameter(description = "Category update request") @RequestBody ForumCategory forumCategory) {
        ForumCategory category = forumCategoryService.getCategory(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(forumCategoryService.updateCategory(id, forumCategory));
    }

    @Operation(summary = "Delete category", description = "Deletes a forum category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category ID") @PathVariable String id) {
        ForumCategory category = forumCategoryService.getCategory(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }

        forumCategoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}
