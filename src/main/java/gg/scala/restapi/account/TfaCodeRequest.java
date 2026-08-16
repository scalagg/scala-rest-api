package gg.scala.restapi.account;

/**
 * @author Luna
 */
public record TfaCodeRequest(String currentPassword, String code) {
}
