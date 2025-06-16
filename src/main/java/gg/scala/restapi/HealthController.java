package gg.scala.restapi;

import gg.scala.restapi.util.AppLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {
    private final AppLogger logger = AppLogger.getLogger(HealthController.class);

    @GetMapping("/istheapiworking")
    public Map<String, String> isWorking() {
        logger.info("Health check requested");
        return Map.of(
            "status", "ok",
            "message", "API is working"
        );
    }
}