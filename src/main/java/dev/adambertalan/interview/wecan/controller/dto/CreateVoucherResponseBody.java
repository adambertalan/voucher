package dev.adambertalan.interview.wecan.controller.dto;

public class CreateVoucherResponseBody {
    private String code;
    private Boolean successful;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }
}
