package com.haqq.topupnow.payload;

import javax.validation.constraints.NotBlank;

public class WalletRequest {
    @NotBlank
    private String inFlaw;

    @NotBlank
    private String amount;


    public String getInFlaw() {
        return inFlaw;
    }

    public void setInFlaw(String inFlaw) {
        this.inFlaw = inFlaw;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
