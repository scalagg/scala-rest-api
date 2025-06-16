package gg.scala.restapi.profile;

import gg.scala.restapi.util.UUIDCache;
import gg.scala.restapi.util.AppLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
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

import java.util.UUID;

@RestController
@RequestMapping("/uuid")
@Tag(name = "UUID Management", description = "APIs for managing player UUIDs and usernames")
public class UUIDController {
    private final AppLogger logger = AppLogger.getLogger(UUIDController.class);

    protected record UUIDResponse(@Nullable UUID uuid, @Nullable String name) {}

    @Operation(summary = "Get UUID information", description = "Retrieves UUID and username information for a player")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "UUID information retrieved successfully", content = @Content(schema = @Schema(implementation = UUIDResponse.class))),
        @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @GetMapping("/{data}")
    public ResponseEntity<UUIDResponse> getUUID(
            @Parameter(description = "Player UUID or username") @PathVariable String data) {
        logger.info("UUID lookup requested for data: %s", data);
        
        if (UUIDCache.isUuid(data)) {
            String name = UUIDCache.getName(UUID.fromString(data));
            logger.debug("UUID lookup result - UUID: %s, name: %s", data, name);
            return ResponseEntity.ok(new UUIDResponse(UUID.fromString(data), name));
        }

        UUID uuid = UUIDCache.getUuid(data);
        logger.debug("Username lookup result - username: %s, UUID: %s", data, uuid);
        return ResponseEntity.ok(new UUIDResponse(uuid, data));
    }
}
