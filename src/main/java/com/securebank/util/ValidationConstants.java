package com.securebank.util;

public class ValidationConstants {

    // User
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_PASSWORD_LENGTH = 50;
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;

    // Account
    public static final double MIN_TRANSFER_AMOUNT = 1.0;
    public static final double MAX_TRANSFER_AMOUNT = 1000000.0;

    // Loan
    public static final double MIN_LOAN_AMOUNT = 10000.0;
    public static final double MAX_LOAN_AMOUNT = 5000000.0;
    public static final int MIN_LOAN_TENURE = 6;
    public static final int MAX_LOAN_TENURE = 360;

    // FD
    public static final double MIN_FD_AMOUNT = 1000.0;
    public static final double MAX_FD_AMOUNT = 10000000.0;
    public static final int MIN_FD_TENURE = 1;
    public static final int MAX_FD_TENURE = 120;

    // OTP
    public static final int OTP_LENGTH = 6;

    private ValidationConstants() {}
}