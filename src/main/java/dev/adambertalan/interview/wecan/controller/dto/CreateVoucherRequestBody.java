package dev.adambertalan.interview.wecan.controller.dto;

import dev.adambertalan.interview.wecan.entity.VoucherType;

import java.util.Map;

public class CreateVoucherRequestBody {
    private String userName;
    private String code;
    private VoucherType type;
    private Map<String, String> typeSpecificData;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public VoucherType getType() {
        return type;
    }

    public void setType(VoucherType type) {
        this.type = type;
    }

    public Map<String, String> getTypeSpecificData() {
        return typeSpecificData;
    }

    public void setTypeSpecificData(Map<String, String> typeSpecificData) {
        this.typeSpecificData = typeSpecificData;
    }
}
