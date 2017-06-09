package com.example.saucecode.thegoviya;

/**
 * Created by buwaneka on 6/3/2017.
 */

public class BuyerBid {
    protected String buyerID;
    protected int productID;
    protected double bidAmount;

    public BuyerBid(String b,int p,double a ){
        this.buyerID = b;
        this.productID = p;
        this.bidAmount = a;
    }

    public String getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(String buyerID) {
        this.buyerID = buyerID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public Double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(Double bidAmount) {
        this.bidAmount = bidAmount;
    }


}
