package com.wethinkcode.fixme.core;

public class CheckSumClass {
    private static CheckSumClass checkSumClass = null;

    private CheckSumClass(){}

    public String ConstructCheckSumMessage(String message) {
        int sum = 0;
        int length;

        switch (message.charAt(message.length() - 1)){
            case '|':
                length = message.length() - 7;
                break;
            default:
                length = message.length() - 6;
        }

        for (int i = 0; i < length; i++) {
            if (message.charAt(i) == '|') {
                sum += 1;
            } else {
                sum += message.charAt(i);
            }
        }

        String checksumValue = String.valueOf(sum % 256);
        for (int i = 3; i > String.valueOf(sum % 256).length(); i--) {
            checksumValue = "0" + checksumValue;
        }

        return checksumValue;
    }

    public static CheckSumClass getInstance() {
        if (checkSumClass == null) {
            checkSumClass = new CheckSumClass();
        }
        return checkSumClass;
    }
}
