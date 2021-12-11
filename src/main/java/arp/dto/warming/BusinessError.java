package arp.dto.warming;

import arp.exception.FailureReason;
import lombok.Data;

@Data
public class BusinessError {
    private FailureReason reason;
    private String message;

    public BusinessError(FailureReason reason, String message) {
        this.reason = reason;
        this.message = message;
    }
}
