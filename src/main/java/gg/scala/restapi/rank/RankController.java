package gg.scala.restapi.rank;

import gg.scala.restapi.profile.grant.GrantService;
import gg.scala.restapi.util.UUIDCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
public class RankController {

    private final RankService rankService;
    private final GrantService grantService;

    public RankController(RankService rankService, GrantService grantService) {
        this.rankService = rankService;
        this.grantService = grantService;
    }

    @GetMapping("/staffList")
    public ResponseEntity<List<StaffRank>> getStaffList() {
        List<StaffRank> ranks = new ArrayList<>();

        List<String> processed = new ArrayList<>();
        rankService.getAll().stream().filter(rank -> rank.getPermissions().contains("scstaff.staff-member"))
                .sorted(Comparator.comparingInt(Rank::getWeight).reversed()).forEach(rank -> {
            List<StaffRank.StaffRankMember> members = new ArrayList<>();

            grantService.getAllActiveGrantsByRankId(rank.getUuid()).forEach(grant -> {
                if (processed.contains(grant.getTarget())) return;

                members.add(new StaffRank.StaffRankMember(UUIDCache.getName(UUID.fromString(grant.getTarget())), grant.getTarget()));
                processed.add(grant.getTarget());
            });

            ranks.add(new StaffRank(rank.getUuid(), rank.getName(), rank.getWeight(), rank.getColor(), members));
        });

        return ResponseEntity.ok(ranks);
    }
}
