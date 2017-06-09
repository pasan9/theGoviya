package com.example.saucecode.thegoviya;

/**
 * Created by buwaneka on 6/8/2017.
 */

public class BidderInfo extends BuyerBid{

    private  String fName = "";
    private  String address = "";
    private  int mobileNumber = 0;

    public BidderInfo(String bID,int pID,String fName,String address,int mobileNumber,double bidAmount){
        super(bID,pID,bidAmount);

        this.fName = fName;
        this.address = address;
        this.mobileNumber = mobileNumber;

    }


    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(int mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
