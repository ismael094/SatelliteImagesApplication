package model.exception;

public class NotAuthenticatedException extends Exception {
    public NotAuthenticatedException(String errorMessage) {
        super(errorMessage);
    }
}
