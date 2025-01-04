package gg.scala.restapi.anticheat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/anticheat")
public class AnticheatController {
    private final AnticheatService anticheatService;

    public AnticheatController(AnticheatService anticheatService) {
        this.anticheatService = anticheatService;
    }

    @GetMapping("/{uuid}")
    public List<AnticheatAlertResult> getAlerts(@PathVariable String uuid) {
        return anticheatService.getAlerts(uuid);
    }
}
