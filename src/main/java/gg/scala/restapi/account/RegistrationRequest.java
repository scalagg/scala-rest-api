package gg.scala.restapi.account;

public record RegistrationRequest(
        String token,
        String password
) {
}
