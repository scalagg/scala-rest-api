package gg.scala.restapi.account;

/**
 * @author Luna
 */
public record ChangeEmailRequest(String currentPassword, String email) {
}
