package gg.scala.restapi.profile.punishment;

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
@RequestMapping("/punishment")
@Tag(name = "Punishment Management", description = "APIs for managing player punishments and sanctions")
public class PunishmentController {
    private final AppLogger logger = AppLogger.getLogger(PunishmentController.class);
    private final PunishmentService punishmentService;

    public PunishmentController(PunishmentService punishmentService) {
        this.punishmentService = punishmentService;
    }

    @Operation(summary = "Get player punishments", description = "Retrieves all punishments for a specific player")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Punishments retrieved successfully", content = @Content(schema = @Schema(implementation = Punishment.class))),
        @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @GetMapping("/profile/{uuid}")
    public ResponseEntity<List<Punishment>> getPunishments(
            @Parameter(description = "Player UUID") @PathVariable String uuid) {
        logger.info("Punishments requested for player UUID: %s", uuid);
        
        List<Punishment> punishments = punishmentService.getPunishments(uuid);
        logger.debug("Retrieved %d punishments for player UUID: %s", punishments.size(), uuid);

        // TODO: now we would map it to like names and colors and such
        return ResponseEntity.ok(punishments);
    }
}
