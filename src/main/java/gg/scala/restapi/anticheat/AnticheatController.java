package gg.scala.restapi.anticheat;

import gg.scala.restapi.util.AppLogger;
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

import java.util.List;

@RestController
@RequestMapping("/anticheat")
@Tag(name = "Anticheat Management", description = "APIs for managing anticheat alerts and violations")
public class AnticheatController {
    private final AppLogger logger = AppLogger.getLogger(AnticheatController.class);
    private final AnticheatService anticheatService;

    public AnticheatController(AnticheatService anticheatService) {
        this.anticheatService = anticheatService;
    }

    @Operation(summary = "Get anticheat alerts", description = "Retrieves all anticheat alerts for a specific player")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Alerts retrieved successfully", content = @Content(schema = @Schema(implementation = AnticheatAlertResult.class))),
        @ApiResponse(responseCode = "404", description = "Player not found")
    })
    @GetMapping("/{uuid}")
    public List<AnticheatAlertResult> getAlerts(
            @Parameter(description = "Player UUID") @PathVariable String uuid) {
        logger.info("Anticheat alerts requested for UUID: %s", uuid);
        List<AnticheatAlertResult> alerts = anticheatService.getAlerts(uuid);
        logger.debug("Retrieved %d anticheat alerts for UUID: %s", alerts.size(), uuid);
        return alerts;
    }
}
