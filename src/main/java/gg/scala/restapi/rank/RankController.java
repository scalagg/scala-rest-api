package gg.scala.restapi.rank;

import gg.scala.restapi.profile.grant.GrantService;
import gg.scala.restapi.util.UUIDCache;
import gg.scala.restapi.util.AppLogger;
import gg.scala.restapi.util.ColorUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.Set;

@RestController
public class RankController {
    private final AppLogger logger = AppLogger.getLogger(RankController.class);
    private final RankService rankService;
    private final GrantService grantService;

    public RankController(RankService rankService, GrantService grantService) {
        this.rankService = rankService;
        this.grantService = grantService;
    }

    @GetMapping("/rank/{uuid}")
    public ResponseEntity<Rank> getRank(@PathVariable UUID uuid) {
        logger.info("Rank requested for UUID: %s", uuid);
        
        try {
            Optional<Rank> rankOpt = rankService.getById(uuid.toString());
            if (rankOpt.isPresent()) {
                Rank rank = rankOpt.get();
                logger.debug("Rank found: %s (name: %s)", uuid, rank.getName());
                return ResponseEntity.ok(rank);
            } else {
                logger.warn("Rank not found for UUID: %s", uuid);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error retrieving rank for UUID %s: %s", uuid, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/rank/color/{uuid}")
    public ResponseEntity<RankColorResponse> getRankColor(@PathVariable UUID uuid) {
        logger.info("Rank color requested for UUID: %s", uuid);
        
        try {
            Optional<Rank> rankOpt = rankService.getById(uuid.toString());
            if (rankOpt.isPresent()) {
                Rank rank = rankOpt.get();
                String color = rank.getColor();
                // Convert Minecraft color code to hex if needed
                if (color != null && (color.startsWith("§") || color.startsWith("&"))) {
                    color = ColorUtil.getHexFromMinecraft(color.replace('&', '§'));
                }
                logger.debug("Rank color found: %s (color: %s)", uuid, color);
                return ResponseEntity.ok(new RankColorResponse(color));
            } else {
                logger.warn("Rank not found for UUID: %s", uuid);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error retrieving rank color for UUID %s: %s", uuid, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/rank/name/{name}")
    public ResponseEntity<Rank> getRankByName(@PathVariable String name) {
        logger.info("Rank requested by name: %s", name);
        
        try {
            Optional<Rank> rankOpt = rankService.getByName(name);
            if (rankOpt.isPresent()) {
                Rank rank = rankOpt.get();
                logger.debug("Rank found by name: %s (UUID: %s)", name, rank.getUuid());
                return ResponseEntity.ok(rank);
            } else {
                logger.warn("Rank not found by name: %s", name);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error retrieving rank by name %s: %s", name, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
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

    @GetMapping("/rank/{uuid}/members")
    public ResponseEntity<List<StaffRank.StaffRankMember>> getRankMembers(@PathVariable UUID uuid) {
        if (rankService.getById(uuid.toString()).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<StaffRank.StaffRankMember> members = grantService.getAllActiveGrantsByRankId(uuid.toString())
                .stream()
                .map(grant -> new StaffRank.StaffRankMember(
                        UUIDCache.getName(UUID.fromString(grant.getTarget())), grant.getTarget()))
                .toList();
        return ResponseEntity.ok(members);
    }

    @GetMapping("/rankGroups/{group}")
    public ResponseEntity<List<StaffRank>> getRankGroup(@PathVariable String group) {
        Set<String> names;
        if (group.equalsIgnoreCase("creators")) {
            names = Set.of("owner", "partner", "partners", "famous", "media", "creator", "youtube", "youtuber", "twitch");
        } else if (group.equalsIgnoreCase("hall-of-fame")) {
            names = Set.of("hof", "halloffame", "hall-of-fame", "hall of fame");
        } else {
            return ResponseEntity.notFound().build();
        }

        List<StaffRank> ranks = rankService.getAll().stream()
                .filter(rank -> names.contains(rank.getName().toLowerCase()))
                .sorted(Comparator.comparingInt(Rank::getWeight).reversed())
                .map(rank -> new StaffRank(
                        rank.getUuid(), rank.getName(), rank.getDisplayName(), rank.getWeight(), rank.getColor(),
                        grantService.getAllActiveGrantsByRankId(rank.getUuid()).stream()
                                .map(grant -> new StaffRank.StaffRankMember(
                                        UUIDCache.getName(UUID.fromString(grant.getTarget())), grant.getTarget()))
                                .toList()))
                .toList();
        return ResponseEntity.ok(ranks);
    }

    public static class RankColorResponse {
        private final String color;

        public RankColorResponse(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }
}
