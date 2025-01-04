package gg.scala.restapi.account;

public record ConfirmPasswordResetRequest(
        String token,
        String password
) {
}
