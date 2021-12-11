package arp.dto.warming;

import arp.exception.FailureReason;

public class BusinessError {
    public FailureReason reason;

    public BusinessError(FailureReason reason) {
        this.reason = reason;
    }
}
