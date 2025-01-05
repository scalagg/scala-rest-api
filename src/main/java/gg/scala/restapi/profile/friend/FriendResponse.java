package gg.scala.restapi.profile.friend;

public record FriendResponse(
        String uuidA,
        String uuidB,
        String nameA,
        String nameB,
        long acceptedAt
) {
}
