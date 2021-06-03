package dev.adambertalan.interview.wecan.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class RedemptionEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private VoucherEntity voucher;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public VoucherEntity getVoucher() {
        return voucher;
    }

    public void setVoucher(VoucherEntity voucher) {
        this.voucher = voucher;
    }
}
