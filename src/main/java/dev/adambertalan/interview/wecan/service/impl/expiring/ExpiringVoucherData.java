package dev.adambertalan.interview.wecan.service.impl.expiring;

import dev.adambertalan.interview.wecan.service.VoucherTypeSpecificData;

import java.time.LocalDateTime;

public class ExpiringVoucherData implements VoucherTypeSpecificData {

    public ExpiringVoucherData() {
    }

    public ExpiringVoucherData(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    private LocalDateTime expiresAt;

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
