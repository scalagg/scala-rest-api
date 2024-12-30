package gg.scala.restapi.account;

public record InitialRegistrationRequest(
        String email,
        String token
) { }
