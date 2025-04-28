package gg.scala.restapi.profile.notification;

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
@RequestMapping("/forum/notifications")
@Tag(name = "Notification Management", description = "APIs for managing user notifications and alerts")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Operation(summary = "Get user notifications", description = "Retrieves all notifications for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully", content = @Content(schema = @Schema(implementation = Notification.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public List<Notification> getNotification(
            @Parameter(description = "User UUID") @PathVariable String id) {
        return notificationRepository.findByTarget(id);
    }

    @Operation(summary = "Mark notification as read", description = "Marks a specific notification as read")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification marked as read successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or notification not found")
    })
    @PostMapping("/read")
    public void read(
            @Parameter(description = "Notification read request") @RequestBody ReadRequest request) {
        Notification notification = notificationRepository.findById(request.notificationId())
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
