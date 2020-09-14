package model.exception;

public class AuthenticationException extends Exception {
    public AuthenticationException(String errorMessage) {
        super(errorMessage);
    }
}
