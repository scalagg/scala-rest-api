package gg.scala.restapi.forums.category;

import gg.scala.restapi.forums.forums.Forum;
import gg.scala.restapi.forums.forums.ForumService;
import gg.scala.restapi.util.AppLogger;
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
    private final AppLogger logger = AppLogger.getLogger(ForumCategoryController.class);
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
        logger.info("Forum categories requested");
        List<ForumCategoryResponse> categories = forumCategoryService.getCategories().stream().map(category -> {
            List<Forum> forums = forumService.getForumsByCategory(category.getId());
            return new ForumCategoryResponse(category, forums);
        }).toList();
        logger.debug("Retrieved %d forum categories", categories.size());
        return categories;
    }

    @Operation(summary = "Create category", description = "Creates a new forum category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category created successfully", content = @Content(schema = @Schema(implementation = ForumCategory.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or category already exists")
    })
    @PostMapping
    public ResponseEntity<ForumCategory> createCategory(
            @Parameter(description = "Category creation request") @RequestBody ForumCategory forumCategory) {
        logger.info("Category creation requested for ID: %s", forumCategory.getId());
        
        if (forumCategoryService.getCategory(forumCategory.getId()) != null) {
            logger.warn("Category already exists for ID: %s", forumCategory.getId());
            return ResponseEntity.badRequest().build();
        }

        ForumCategory savedCategory = forumCategoryService.createCategory(forumCategory);
        logger.success("Category created successfully for ID: %s", forumCategory.getId());
        return ResponseEntity.ok(savedCategory);
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
        logger.info("Category update requested for ID: %s", id);
        
        ForumCategory category = forumCategoryService.getCategory(id);
        if (category == null) {
            logger.warn("Category not found for update ID: %s", id);
            return ResponseEntity.notFound().build();
        }

        ForumCategory updatedCategory = forumCategoryService.updateCategory(id, forumCategory);
        logger.success("Category updated successfully for ID: %s", id);
        return ResponseEntity.ok(updatedCategory);
    }

    @Operation(summary = "Delete category", description = "Deletes a forum category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Category ID") @PathVariable String id) {
        logger.info("Category deletion requested for ID: %s", id);
        
        ForumCategory category = forumCategoryService.getCategory(id);
        if (category == null) {
            logger.warn("Category not found for deletion ID: %s", id);
            return ResponseEntity.notFound().build();
        }

        forumCategoryService.deleteCategory(id);
        logger.success("Category deleted successfully for ID: %s", id);
        return ResponseEntity.ok().build();
    }
}
