package itmo.infosecurity.lab1.exceptions;

public class UserExistsException extends Exception {
    public UserExistsException(String message) {
        super(message);
    }
}
