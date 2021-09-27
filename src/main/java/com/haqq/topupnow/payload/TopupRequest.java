package com.haqq.topupnow.payload;

import javax.validation.constraints.NotBlank;

public class TopupRequest {
    @NotBlank
    private String operatorId;
    @NotBlank
    private String amount;
    @NotBlank
    private String senderPhone;
    @NotBlank
    private String receiverPhone;
    @NotBlank
    private String countryCode;


    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
