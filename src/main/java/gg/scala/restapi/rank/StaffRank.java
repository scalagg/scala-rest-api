package gg.scala.restapi.rank;

import java.util.List;

public record StaffRank(
        String uuid,
        String name,
        int weight,
        String color,
        List<StaffRankMember> members

) {
    public record StaffRankMember(
            String name,
            String uuid
    ) {}
}
