package gg.scala.restapi.profile.grant;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrantService {
    private final GrantRepository grantRepository;

    public GrantService(GrantRepository grantRepository) {
        this.grantRepository = grantRepository;
    }

    public Grant save(Grant grant) {
        return grantRepository.save(grant);
    }

    public void delete(Grant grant) {
        grantRepository.delete(grant);
    }

    public Grant getById(String id) {
        return grantRepository.findById(id).orElse(null);
    }

    public List<Grant> getGrants(String target) {
        return grantRepository.findAllByTarget(target);
    }

    public List<Grant> getAllActiveGrants(String target) {
        return grantRepository.findAllByTarget(target)
                .stream()
                .filter(grant -> grant.getRemovedAt() == -1)
                .toList();
    }

    public List<Grant> getAllActiveGrantsByRankId(String rankId) {
        return grantRepository.findAllByRankId(rankId)
                .stream()
                .filter(grant -> grant.getRemovedAt() == -1)
                .toList();
    }
}
