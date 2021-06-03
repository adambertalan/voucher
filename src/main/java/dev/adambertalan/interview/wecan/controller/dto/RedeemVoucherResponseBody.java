package dev.adambertalan.interview.wecan.controller.dto;

public class RedeemVoucherResponseBody {
    private String code;
    private String userName;
    private Boolean successful;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }
}
