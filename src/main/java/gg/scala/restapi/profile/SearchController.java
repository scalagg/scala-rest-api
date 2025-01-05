package gg.scala.restapi.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final ProfileRepository profileRepository;

    public SearchController(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @GetMapping
    public ResponseEntity<List<SearchResponse>> search(@RequestParam("query") String query, @RequestParam(value = "limit", defaultValue = "6") int limit) {
        Pattern pattern = Pattern.compile("^" + Pattern.quote(query), Pattern.CASE_INSENSITIVE);

        List<SearchResponse> responses = new ArrayList<>();
        profileRepository.findByNameRegex(pattern.pattern()).stream()
                .limit(limit).forEach(profile -> responses.add(new SearchResponse(profile.getName(), profile.getUniqueId())));

        return ResponseEntity.ok(responses);
    }
}
