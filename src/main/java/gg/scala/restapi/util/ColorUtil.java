package gg.scala.restapi.util;

public class ColorUtil {
    public static String getHexFromMinecraft(String colorCode) {
        String stripped = colorCode.substring(1, 2);
        return switch (stripped) {
            case "0" -> "#000000";
            case "1" -> "#0000AA";
            case "2" -> "#00AA00";
            case "3" -> "#00AAAA";
            case "4" -> "#AA0000";
            case "5" -> "#AA00AA";
            case "6" -> "#FFAA00";
            case "7" -> "#AAAAAA";
            case "8" -> "#555555";
            case "9" -> "#5555FF";
            case "a" -> "#55FF55";
            case "b" -> "#55FFFF";
            case "c" -> "#FF5555";
            case "d" -> "#FF55FF";
            case "e" -> "#FFFF55";
            case "f" -> "#FFFFFF";
            default -> "#000000";
        };
    }
}
