package com.example.saucecode.thegoviya;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.saucecode.thegoviya.R.drawable.rounded_corner_layout;

public class DisplayProducts extends AppCompatActivity {

    ArrayList<Products> prodList = new ArrayList<Products>();

    private RelativeLayout rLayout;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_products);

        listView = (ListView)findViewById(R.id.list);

        rLayout = (RelativeLayout)findViewById(R.id.relativeLayout);

        System.out.println("=====================================================================================================");
        DisplayProds prods = new DisplayProds();
        prods.execute("");

    }


    private void displayProducts(ArrayList<Products> prodList){

        System.out.println(prodList.size());

        final LinearLayout.LayoutParams attr = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout.LayoutParams attrLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        attr.setMargins(50,50,50,50);
        attrLayout.setMargins(50,50,50,50);
            for(int i = 0;i < prodList.size();i++){

                int productID = prodList.get(i).productID;
                String farmerID = prodList.get(i).farmerID;
                double qty = prodList.get(i).qty;
                double price = prodList.get(i).price;
                double mois = prodList.get(i).mois;
                String type = prodList.get(i).type;
                String sellingMethod = prodList.get(i).sellingMethod;

                System.out.println(productID+" "+farmerID+" "+qty+" "+price+" "+mois+" "+type+" "+sellingMethod);

                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);

                TextView ID = new TextView(this);
                ID.setText(productID+"");
                ID.setLayoutParams(attr);

                TextView fID = new TextView(this);
                fID.setText(farmerID);
                fID.setLayoutParams(attr);

                TextView qt = new TextView(this);
                qt.setText(qty+" KG");
                qt.setLayoutParams(attr);

                TextView pr = new TextView(this);
                pr.setText("Rs "+price);
                pr.setLayoutParams(attr);

                TextView moi = new TextView(this);
                moi.setText(mois+"%");
                moi.setLayoutParams(attr);

                TextView typ = new TextView(this);
                typ.setText(type);
                typ.setLayoutParams(attr);

                TextView sel = new TextView(this);
                sel.setText(sellingMethod);
                sel.setLayoutParams(attr);

                layout.addView(ID);
                layout.addView(fID);
                layout.addView(qt);
                layout.addView(pr);
                layout.addView(moi);
                layout.addView(typ);
                layout.addView(sel);

                layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_layout));
                layout.setLayoutParams(attrLayout);
                rLayout.addView(layout);

            }
    }

    public class DisplayProds extends AsyncTask<String, String,String> {
        @Override
        protected String doInBackground(String... params) {
            Crud con = new Crud();
            prodList = con.selectData("SELECT * FROM products");
            return "Success";
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println("===========================================================================================================");
            displayProducts(prodList);
        }
    }
}
