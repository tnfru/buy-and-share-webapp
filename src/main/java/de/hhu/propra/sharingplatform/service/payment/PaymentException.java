package de.hhu.propra.sharingplatform.service.payment;

public class PaymentException extends Exception {

    public final int UNKNOWN = 0;
    public final int NETWORK_ERR = 1;
    public final int NOT_ENOUGH_MONEY = 2;
    public final int SENDER_NOT_EXISTS = 3;
    public final int RECIPIENT_NOT_EXISTS = 4;

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
