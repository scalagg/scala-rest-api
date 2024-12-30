package gg.scala.restapi.profile;

import gg.scala.restapi.profile.grant.Grant;
import gg.scala.restapi.profile.grant.GrantService;
import gg.scala.restapi.rank.Rank;
import gg.scala.restapi.rank.RankRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final GrantService grantService;
    private final RankRepository rankRepository;

    public ProfileService(ProfileRepository profileRepository, GrantService grantService, RankRepository rankRepository) {
        this.profileRepository = profileRepository;
        this.grantService = grantService;
        this.rankRepository = rankRepository;
    }

    public Optional<Profile> getById(String id) {
        return profileRepository.findById(id);
    }

    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }

    public Rank getActiveRank(String uuid) {
        return getActiveRank(grantService.getAllActiveGrants(uuid), uuid);
    }

    public Rank getActiveRank(List<Grant> activeGrants, String uuid) {
        List<String> rankIds = activeGrants.stream()
                .map(Grant::getRankId)
                .toList();

        if (rankIds.isEmpty()) {
            return null;
        }

        List<Rank> ranks = rankRepository.findAll();
        return ranks.stream()
                .filter(rank -> rankIds.contains(rank.getUuid()))
                .max(Comparator.comparingInt(Rank::getWeight))
                .orElse(null);
    }
}
