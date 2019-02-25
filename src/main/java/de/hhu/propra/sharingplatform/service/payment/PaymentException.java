package de.hhu.propra.sharingplatform.service.payment;

public class PaymentException extends Exception {

    public static final int UNKNOWN = 0;
    public static final int NETWORKERR = 1;
    public static final int NOTENOUGHMONEY = 2;
    public static final int SENDERNOTEXISTS = 3;
    public static final int RECIPIENTNOTEXISTS = 4;

    public int errorCode = UNKNOWN;

    public void setErrorCode(int code) {
        if (code < 0) {
            errorCode = 0;
        } else if (code > 4) {
            errorCode = 0;
        } else {
            errorCode = code;
        }
    }
}
