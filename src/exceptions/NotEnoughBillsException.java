package src.exceptions;

public class NotEnoughBillsException extends Exception {
    public NotEnoughBillsException(String errorMessage) {
        super(errorMessage);
    }
}
