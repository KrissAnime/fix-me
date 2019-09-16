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
        String msg = "|50="+ this.markerId +"|56=" + this.brokerId + "|39=" + this.status;
        int checksum = getChecksum(msg);
        return msg + "|10="+checksum;
    }

    public int getChecksum(String message){
        String chkmessage = message;
        chkmessage = chkmessage.replace('|', '\u0001');

        int sum = 0;
        int char_val = 0;

        for (int i = 0; i < chkmessage.length(); i++) {
            char_val = chkmessage.charAt(i);
            sum += char_val;
        }
        return sum % 256;
    }
}
