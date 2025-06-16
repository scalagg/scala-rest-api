package gg.scala.restapi.profile.grant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import gg.scala.restapi.util.AppLogger;

import java.util.List;

@RestController
@RequestMapping("/grant")
@Tag(name = "Grant Management", description = "APIs for managing rank grants and permissions")
public class GrantController {
    private final AppLogger logger = AppLogger.getLogger(GrantController.class);
    private final GrantService grantService;

    public GrantController(GrantService grantService) {
        this.grantService = grantService;
    }

    @Operation(summary = "Get player grants", description = "Retrieves all rank grants for a specific player")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Grants retrieved successfully", content = @Content(schema = @Schema(implementation = Grant.class))),
        @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @GetMapping("/profile/{uuid}")
    public ResponseEntity<List<Grant>> getGrants(
            @Parameter(description = "Player UUID") @PathVariable String uuid) {
        logger.info("Grants requested for player UUID: %s", uuid);
        
        List<Grant> grants = grantService.getGrants(uuid);
        logger.debug("Retrieved %d grants for player UUID: %s", grants.size(), uuid);

        // TODO: map to uuid cache and such
        return ResponseEntity.ok(grants);
    }
}
