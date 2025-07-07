package gg.scala.restapi.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coreplayerprofile")
public class CorePlayerProfileController {
    @Autowired
    private CorePlayerProfileRepository repository;

    @GetMapping("/{id}")
    public ResponseEntity<CorePlayerProfile> getProfile(@PathVariable String id) {
        return repository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
} 