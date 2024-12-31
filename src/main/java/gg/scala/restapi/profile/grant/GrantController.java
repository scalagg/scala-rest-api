package gg.scala.restapi.profile.grant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/grant")
public class GrantController {
    private final GrantService grantService;

    public GrantController(GrantService grantService) {
        this.grantService = grantService;
    }

    @GetMapping("/profile/{uuid}")
    public ResponseEntity<List<Grant>> getGrants(@PathVariable String uuid) {
        List<Grant> grants = grantService.getGrants(uuid);

        // TODO: map to uuid cache and such
        return ResponseEntity.ok(grants);
    }
}
