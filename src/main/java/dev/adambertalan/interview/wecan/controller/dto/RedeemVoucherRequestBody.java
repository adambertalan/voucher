package dev.adambertalan.interview.wecan.controller.dto;

public class RedeemVoucherRequestBody {
    private String userName;
    private String voucherCode;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }
}
