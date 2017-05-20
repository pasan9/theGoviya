package com.example.saucecode.thegoviya;

/**
 * Created by buwaneka on 07/05/2017.
 */

public class CurrentUser {
    private static String nicNumber = "";
    private static String fName = "";
    private static String address = "";
    private static String type = "";
    private static int mobileNumber = 0;

    public static String getNicNumber() {
        return nicNumber;
    }

    public static String getfName() {
        return fName;
    }

    public static String getAddress() {
        return address;
    }

    public static String getType() {
        return type;
    }

    public static int getMobileNumber() {
        return mobileNumber;
    }

    public static void setNicNumber(String nicNumber) {
        CurrentUser.nicNumber = nicNumber;
    }

    public static void setfName(String fName) {
        CurrentUser.fName = fName;
    }

    public static void setAddress(String address) {
        CurrentUser.address = address;
    }

    public static void setType(String type) {
        CurrentUser.type = type;
    }

    public static void setMobileNumber(int mobileNumber) {
        CurrentUser.mobileNumber = mobileNumber;
    }
}
