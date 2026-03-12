package pl.knck.jbot.exceptions;

public class BotNotFoundException extends RuntimeException {
    public BotNotFoundException(String message) {

        super("BotNotFoundException: " + message);
    }
}
