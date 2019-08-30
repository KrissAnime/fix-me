package com.wethinkcode.fixme.market.java;

public class Reply {
    private int brokerId;
    private int status;
    private int markerId;
    private int checkSum;

    public void setBrokerId(int brokerId) {
        this.brokerId = brokerId;
    }

    public void setCheckSum(int checkSum) {
        this.checkSum = checkSum;
    }

    public void setMarkerId(int markerId) {
        this.markerId = markerId;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBrokerId() {
        return brokerId;
    }

    public int getCheckSum() {
        return checkSum;
    }

    public int getMarkerId() {
        return markerId;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return super.toString();
    }
    public String getMessage(){
        return "|50="+ this.brokerId +"|56=" + this.markerId + "|39=" + this.status + "|10=" + this.checkSum;
    }
}
