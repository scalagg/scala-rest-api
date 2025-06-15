package gg.scala.restapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {
    @GetMapping("/istheapiworking")
    public Map<String, String> isWorking() {
        return Map.of(
            "status", "ok",
            "message", "API is working"
        );
    }
}