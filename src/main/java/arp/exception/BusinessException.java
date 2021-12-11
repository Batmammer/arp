package arp.exception;

public class BusinessException extends RuntimeException {
    public FailureReason type;

    public BusinessException(String message, FailureReason type) {
        super(message);
        this.type = type;
    }
}
