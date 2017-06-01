package com.example.saucecode.thegoviya;

import java.io.Serializable;

/**
 * Created by buwaneka on 6/1/2017.
 */

public class Person implements Serializable{

    private  String nicNumber = "";
    private  String fName = "";
    private  String address = "";
    private  String type = "";
    private  int mobileNumber = 0;

    public  String getNicNumber() {
        return nicNumber;
    }

    public  String getfName() {
        return fName;
    }

    public  String getAddress() {
        return address;
    }

    public  String getType() {
        return type;
    }

    public  int getMobileNumber() {
        return mobileNumber;
    }

    public  void setNicNumber(String nicNumber) {
        this.nicNumber = nicNumber;
    }

    public  void setfName(String fName) {
        this.fName = fName;
    }

    public  void setAddress(String address) {
        this.address = address;
    }

    public  void setType(String type) {
        this.type = type;
    }

    public  void setMobileNumber(int mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
