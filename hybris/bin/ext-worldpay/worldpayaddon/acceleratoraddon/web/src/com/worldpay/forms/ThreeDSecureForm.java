package com.worldpay.forms;

public class ThreeDSecureForm {
    private String PaRes;
    private String MD;
    private Continue position;

    public Continue getContinue() {
        return position;
    }

    public void setContinue(final Continue position) {
        this.position = position;
    }

    public String getMD() {
        return MD;
    }

    public void setMD(final String MD) {
        this.MD = MD;
    }

    public String getPaRes() {
        return PaRes;
    }

    public void setPaRes(final String paRes) {
        PaRes = paRes;
    }
}
