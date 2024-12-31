package gg.scala.restapi.profile.punishment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/punishment")
public class PunishmentController {
    private final PunishmentService punishmentService;

    public PunishmentController(PunishmentService punishmentService) {
        this.punishmentService = punishmentService;
    }

    @GetMapping("/profile/{uuid}")
    public ResponseEntity<List<Punishment>> getPunishments(@PathVariable String uuid) {
        List<Punishment> punishments = punishmentService.getPunishments(uuid);

        // TODO: now we would map it to like names and colors and such
        return ResponseEntity.ok(punishments);
    }
}
