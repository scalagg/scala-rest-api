package gg.scala.restapi.rank;

import gg.scala.restapi.profile.grant.GrantService;
import gg.scala.restapi.util.UUIDCache;
import gg.scala.restapi.util.AppLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
public class RankController {
    private final AppLogger logger = AppLogger.getLogger(RankController.class);
    private final RankService rankService;
    private final GrantService grantService;

    public RankController(RankService rankService, GrantService grantService) {
        this.rankService = rankService;
        this.grantService = grantService;
    }

    @GetMapping("/staffList")
    public ResponseEntity<List<StaffRank>> getStaffList() {
        logger.info("Staff list requested");
        
        List<StaffRank> ranks = new ArrayList<>();

        List<String> processed = new ArrayList<>();
        rankService.getAll().stream().filter(rank -> rank.getPermissions().contains("core.staff"))
                .sorted(Comparator.comparingInt(Rank::getWeight).reversed()).forEach(rank -> {
            List<StaffRank.StaffRankMember> members = new ArrayList<>();

            grantService.getAllActiveGrantsByRankId(rank.getUuid()).forEach(grant -> {
                if (processed.contains(grant.getTarget())) return;

                members.add(new StaffRank.StaffRankMember(UUIDCache.getName(UUID.fromString(grant.getTarget())), grant.getTarget()));
                processed.add(grant.getTarget());
            });

            ranks.add(new StaffRank(rank.getUuid(), rank.getName(), rank.getDisplayName(), rank.getWeight(), rank.getColor(), members));
        });

        logger.debug("Staff list generated with %d ranks and %d total staff members", ranks.size(), processed.size());
        return ResponseEntity.ok(ranks);
    }
}
