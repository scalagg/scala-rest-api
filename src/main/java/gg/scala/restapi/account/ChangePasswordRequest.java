package gg.scala.restapi.account;

public record ChangePasswordRequest(
        String currentPassword,
        String password
) {
}
