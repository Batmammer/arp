package arp.dto.warming;

import arp.exception.FailureReason;
import lombok.Data;

@Data
public class BusinessError {
    private FailureReason reason;

    public BusinessError(FailureReason reason) {
        this.reason = reason;
    }
}
