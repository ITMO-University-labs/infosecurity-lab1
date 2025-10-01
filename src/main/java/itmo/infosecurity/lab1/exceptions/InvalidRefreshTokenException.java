package itmo.infosecurity.lab1.exceptions;

public class InvalidRefreshTokenException extends Exception {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
