package gg.scala.restapi.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import gg.scala.restapi.util.AppLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/search")
@Tag(name = "Search Management", description = "APIs for searching players and profiles")
public class SearchController {
    private final AppLogger logger = AppLogger.getLogger(SearchController.class);
    private final ProfileRepository profileRepository;

    public SearchController(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Operation(summary = "Search players", description = "Searches for players by username with optional limit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully", content = @Content(schema = @Schema(implementation = SearchResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid search query")
    })
    @GetMapping
    public ResponseEntity<List<SearchResponse>> search(
            @Parameter(description = "Search query") @RequestParam("query") String query,
            @Parameter(description = "Maximum number of results to return") @RequestParam(value = "limit", defaultValue = "6") int limit) {
        logger.info("Player search requested for query: %s, limit: %d", query, limit);
        
        Pattern pattern = Pattern.compile("^" + Pattern.quote(query), Pattern.CASE_INSENSITIVE);

        List<SearchResponse> responses = new ArrayList<>();
        profileRepository.findByNameRegex(pattern.pattern()).stream()
                .limit(limit).forEach(profile -> responses.add(new SearchResponse(profile.getName(), profile.getUniqueId())));

        logger.debug("Search completed for query '%s' - found %d results", query, responses.size());
        return ResponseEntity.ok(responses);
    }
}
