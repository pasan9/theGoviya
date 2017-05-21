package com.example.saucecode.thegoviya;

import java.io.Serializable;

/**
 * Created by buwaneka on 09/05/2017.
 */

public class Products implements Serializable {

    int productID;
    String farmerID;
    double qty;
    double price;
    double mois;
    String type;
    String sellingMethod;

    public Products(int p,String f,double q,double pr,double mo,String ty,String sel){
        this.productID = p;
        this.farmerID = f;
        this.price = pr;
        this.mois = mo;
        this.qty = q;
        this.sellingMethod = sel;
        this.type = ty;
    }
}
