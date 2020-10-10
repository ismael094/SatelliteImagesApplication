package model.exception;

public class ProductNotAvailableException extends Exception {
    public ProductNotAvailableException(String errorMessage) {
        super(errorMessage);
    }
}
