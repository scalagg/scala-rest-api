package gg.scala.restapi.profile;

import gg.scala.restapi.profile.grant.Grant;
import gg.scala.restapi.profile.grant.GrantService;
import gg.scala.restapi.rank.Rank;
import gg.scala.restapi.rank.RankRepository;
import gg.scala.restapi.util.AppLogger;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    private final AppLogger logger = AppLogger.getLogger(ProfileService.class);
    private final ProfileRepository profileRepository;
    private final GrantService grantService;
    private final RankRepository rankRepository;

    public ProfileService(ProfileRepository profileRepository, GrantService grantService, RankRepository rankRepository) {
        this.profileRepository = profileRepository;
        this.grantService = grantService;
        this.rankRepository = rankRepository;
    }

    public Optional<Profile> getById(String id) {
        logger.debug("Fetching profile by ID: %s", id);
        Optional<Profile> profile = profileRepository.findById(id);
        if (profile.isEmpty()) {
            logger.debug("Profile not found for ID: %s", id);
        } else {
            logger.debug("Profile found for ID: %s", id);
        }
        return profile;
    }

    public Profile save(Profile profile) {
        logger.info("Saving profile for UUID: %s", profile.getUniqueId());
        return profileRepository.save(profile);
    }

    public Rank getActiveRank(String uuid) {
        logger.debug("Getting active rank for UUID: %s", uuid);
        return getActiveRank(grantService.getAllActiveGrants(uuid), uuid);
    }

    public Rank getActiveRank(List<Grant> activeGrants, String uuid) {
        logger.debug("Getting active rank for UUID: %s with %d active grants", uuid, activeGrants.size());
        
        List<String> rankIds = activeGrants.stream()
                .map(Grant::getRankId)
                .toList();

        if (rankIds.isEmpty()) {
            logger.debug("No active grants found for UUID: %s", uuid);
            return null;
        }

        List<Rank> ranks = rankRepository.findAll();
        Rank activeRank = ranks.stream()
                .filter(rank -> rankIds.contains(rank.getUuid()))
                .max(Comparator.comparingInt(Rank::getWeight))
                .orElse(null);
        
        if (activeRank != null) {
            logger.debug("Active rank found for UUID %s: %s (weight: %d)", uuid, activeRank.getName(), activeRank.getWeight());
        } else {
            logger.debug("No active rank found for UUID: %s", uuid);
        }
        
        return activeRank;
    }
}
