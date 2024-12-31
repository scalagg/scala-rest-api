package gg.scala.restapi.profile;

import gg.scala.restapi.util.UUIDCache;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/uuid")
public class UUIDController {

    protected record UUIDResponse(@Nullable UUID uuid, @Nullable String name) {}

    @GetMapping("/{data}")
    public ResponseEntity<UUIDResponse> getUUID(@PathVariable String data) {
        if (UUIDCache.isUuid(data)) {
            return ResponseEntity.ok(new UUIDResponse(UUID.fromString(data), UUIDCache.getName(UUID.fromString(data))));
        }

        return ResponseEntity.ok(new UUIDResponse(UUIDCache.getUuid(data), data));
    }
}
