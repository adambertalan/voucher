package dev.adambertalan.interview.wecan.service.impl.xtimes;

import dev.adambertalan.interview.wecan.service.VoucherTypeSpecificData;

public class XTimesRedemptionVoucherData implements VoucherTypeSpecificData {

    public XTimesRedemptionVoucherData() {
    }

    public XTimesRedemptionVoucherData(Integer timesRedeemable) {
        this.timesRedeemable = timesRedeemable;
    }

    private Integer timesRedeemable;

    public Integer getTimesRedeemable() {
        return timesRedeemable;
    }

    public void setTimesRedeemable(Integer timesRedeemable) {
        this.timesRedeemable = timesRedeemable;
    }
}
