package gg.scala.restapi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogger {
    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    private static final String BOLD = "\u001B[1m";

    private final Logger logger;
    private final String context;

    private AppLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
        this.context = clazz.getSimpleName();
    }

    public static AppLogger getLogger(Class<?> clazz) {
        return new AppLogger(clazz);
    }

    public void info(String message, Object... args) {
        logger.info(BLUE + BOLD + "[INFO] [" + context + "] " + RESET + String.format(message, args));
    }

    public void warn(String message, Object... args) {
        logger.warn(YELLOW + BOLD + "[WARN] [" + context + "] " + RESET + String.format(message, args));
    }

    public void error(String message, Object... args) {
        logger.error(RED + BOLD + "[ERROR] [" + context + "] " + RESET + String.format(message, args));
    }

    public void debug(String message, Object... args) {
        logger.debug(CYAN + BOLD + "[DEBUG] [" + context + "] " + RESET + String.format(message, args));
    }

    public void success(String message, Object... args) {
        logger.info(GREEN + BOLD + "[SUCCESS] [" + context + "] " + RESET + String.format(message, args));
    }
}
