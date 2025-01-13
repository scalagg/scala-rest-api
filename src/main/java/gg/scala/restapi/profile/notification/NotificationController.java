package gg.scala.restapi.profile.notification;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forum/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/{id}")
    public List<Notification> getNotification(@PathVariable String id) {
        return notificationRepository.findByTarget(id);
    }

    @PostMapping("/read")
    public void read(@RequestBody ReadRequest request) {
        Notification notification = notificationRepository.findById(request.notificationId())
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
