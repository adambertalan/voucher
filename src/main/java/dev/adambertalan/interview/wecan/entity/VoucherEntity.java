package dev.adambertalan.interview.wecan.entity;

import javax.persistence.*;

@Entity
public class VoucherEntity extends BaseEntity {

    @Column(unique = true)
    private String code;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private VoucherType voucherType;

    private String typeSpecificData;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public VoucherType getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(VoucherType voucherType) {
        this.voucherType = voucherType;
    }

    public String getTypeSpecificData() {
        return typeSpecificData;
    }

    public void setTypeSpecificData(String typeSpecificData) {
        this.typeSpecificData = typeSpecificData;
    }
}
