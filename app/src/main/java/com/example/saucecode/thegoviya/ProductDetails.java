package com.example.saucecode.thegoviya;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ProductDetails extends AppCompatActivity {

    private TextView ProductType, SaleType, Price, Qty, Moisture, MoistureUpdate, bidTime, currentBid;
    private EditText bidAmount;
    private Button contactSeller, sellerLocation, bid;
    private Products product;
    private String MoistureUpDate = "22/05/2017";
    private String MoistureUpTime = "10:23";
    private CountDownTimer timer;
    private boolean runThread = true;
    private boolean aucFinished = false;
    private double curBid = 0.0;
    private GetCurrentHighestBid thread = new GetCurrentHighestBid();
    private double userBidAmount = 0.0;
    private ProgressDialog progress;
    private LinearLayout biddinglayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        product = (Products) getIntent().getSerializableExtra("product");

        if (product.sellingMethod.equalsIgnoreCase("Sell it now")) {

            setContentView(R.layout.activity_product_details);
            ProductType = (TextView) findViewById(R.id.productType);
            //SaleType = (TextView)findViewById(R.id.saleType);
            Price = (TextView) findViewById(R.id.priceTxt);
            Qty = (TextView) findViewById(R.id.QtyTxt);
            Moisture = (TextView) findViewById(R.id.moistureTxt);
            MoistureUpdate = (TextView) findViewById(R.id.MoistureLastUpTxt);
            contactSeller = (Button) findViewById(R.id.btnContactSeller);
            sellerLocation = (Button) findViewById(R.id.btnSellerLocation);
            progress = new ProgressDialog(this);

            ProductType.setText(product.type);
            Price.setText("Price : Rs." + product.price + " Per Kg");
            Qty.setText("Quantity :" + product.qty + "Kg");
            Moisture.setText("Moisture :" + product.mois);
            MoistureUpdate.setText("Last Updated On : " + MoistureUpDate + " at " + MoistureUpTime);

        } else /*(product.sellingMethod.equalsIgnoreCase("Sell it now"))*/ {

            setContentView(R.layout.product_bidding_layout);

            bidAmount = (EditText) findViewById(R.id.userBid);
            ProductType = (TextView) findViewById(R.id.bidProductType);
            Price = (TextView) findViewById(R.id.bidPriceTxt);
            Qty = (TextView) findViewById(R.id.bidQtyTxt);
            Moisture = (TextView) findViewById(R.id.bidMoistureTxt);
            currentBid = (TextView) findViewById(R.id.currentHBidValue);
            MoistureUpdate = (TextView) findViewById(R.id.bidMoistureLastUpTxt);
            bidTime = (TextView) findViewById(R.id.bidTimeLimit);
            contactSeller = (Button) findViewById(R.id.bidBtnContactSeller);
            sellerLocation = (Button) findViewById(R.id.bidBtnSellerLocation);
            bid = (Button) findViewById(R.id.bidBtn);
            biddinglayout = (LinearLayout) findViewById(R.id.biddingLayout);

            setBidTimer();
            ProductType.setText(product.type);
            Price.setText("Starting price : Rs." + product.price + " Per Kg");
            Qty.setText("Quantity :" + product.qty + "Kg");
            Moisture.setText("Moisture :" + product.mois);
            MoistureUpdate.setText("Last Updated On : " + MoistureUpDate + " at " + MoistureUpTime);

            bid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard(ProductDetails.this);
                    if (bidAmount.getText().toString() != null && !bidAmount.getText().toString().equalsIgnoreCase("")) {
                        if (Double.parseDouble(bidAmount.getText().toString()) > curBid) {
                            userBidAmount = Double.parseDouble(bidAmount.getText().toString());
                            //progress.setMessage("Placing bid, please wait ...");
                            //progress.show();
                            PlaceBid bid = new PlaceBid();
                            bid.execute();
                            bidAmount.setText("");
                        } else {

                            final AlertDialog.Builder exitDialog = new AlertDialog.Builder(ProductDetails.this);
                            exitDialog.setTitle("Oops");
                            exitDialog.setMessage("Enter a value higher than " + curBid + "! :/");
                            exitDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });

                            final AlertDialog alert = exitDialog.create();
                            alert.show();

                        }
                    }
                }
            });

        }
    }

    private void setBidTimer() {

        int year;
        int month;
        int day;
        int hour;
        int minute;

        String[] array = product.bidDuration.split("T");
        String date = array[0];
        String time = array[1];
        System.out.println("===============================================================\nDate : " + date + " Time : " + time);
        SimpleDateFormat dateTimeFor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar cal = Calendar.getInstance();

        try {

            cal.setTime(dateTimeFor.parse(date + " " + time));
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
            hour = cal.get(Calendar.HOUR);
            minute = cal.get(Calendar.MINUTE);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar now = Calendar.getInstance();

        System.out.println("date : " + cal.getTime() + " mils " + cal.getTimeInMillis() + " today : " + now.getTime() + " mils : " + now.getTimeInMillis());
        final long mil = cal.getTimeInMillis() - now.getTimeInMillis();
        if (mil > 0) {
            thread.start();
            cal.setTimeInMillis(mil);
            System.out.println("++++++++++++++++++++++++    : " + mil);

            timer = new CountDownTimer(mil, 1000) {

                public void onTick(long millisUntilFinished) {

                    long remain = 0;
                    long dayDiff = 0;
                    long houDiff = 0;
                    long minDiff = 0;
                    long secDiff = 0;

                    remain = millisUntilFinished / 1000;
                    secDiff = remain % 60;
                    remain /= 60;
                    minDiff = remain % 60;
                    remain /= 60;
                    houDiff = remain % 24;
                    remain /= 24;
                    dayDiff = remain;

                    bidTime.setText(dayDiff + " days " + houDiff + ":" + minDiff + ":" + secDiff + " left");
                }

                @Override
                public void onFinish() {
                    auctionFinished();
                    final AlertDialog.Builder exitDialog = new AlertDialog.Builder(ProductDetails.this);
                    exitDialog.setTitle("Oops");
                    exitDialog.setMessage("The time limit for this auction has reached! :/");
                    exitDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            runThread = false;
                            finish();
                        }
                    });

                    final AlertDialog alert = exitDialog.create();
                    alert.show();
                }
            }.start();

        } else {
            runThread = false;
            auctionFinished();
        }

    }

    private void auctionFinished() {
        bidAmount.setEnabled(false);
        bid.setEnabled(false);
        contactSeller.setEnabled(false);
        sellerLocation.setEnabled(false);
        bidTime.setText("This auction is closed!");
        bid.setAlpha((float) 0.7);
        contactSeller.setAlpha((float) 0.7);
        sellerLocation.setAlpha((float) 0.7);
        bidAmount.setAlpha((float) 0.7);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private class PlaceBid extends AsyncTask<Void, Void, String> {
        private CurrentUser user = new CurrentUser();

        @Override
        protected void onPreExecute() {
            user = new CurrentUser();
        }

        public JSONObject getJSONObject() {

            BuyerBid bid = new BuyerBid(user.getNicNumber(), product.productID, userBidAmount);

            JSONObject obj = new JSONObject();
            try {
                obj.put("buyerID", bid.getBuyerID());
                obj.put("productID", bid.getProductID());
                obj.put("bidAmount", bid.getBidAmount());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return obj;
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection httpConnection;
            URL mUrl;
            try {
                mUrl = new URL("http://thegoviyawebservice.azurewebsites.net/api/Auction");
                httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "application/json");
                httpConnection.setRequestProperty("Content-length", "" + getJSONObject().toString().length() + "");
                httpConnection.setDoInput(true);
                httpConnection.setDoOutput(true);
                httpConnection.connect();
                System.out.println(getJSONObject());
                DataOutputStream os = new DataOutputStream(httpConnection.getOutputStream());
                os.writeBytes(getJSONObject().toString());
                os.flush();
                os.close();
                System.out.println(getJSONObject());
                int responseCode = httpConnection.getResponseCode();
                if (responseCode == 204 || responseCode == RESULT_OK) return "success";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "failed";
        }

        @Override
        protected void onPostExecute(String s) {

            //progress.setMessage("Success");
            //progress.show();
            //progress.dismiss();

        }
    }

    private class GetCurrentHighestBid extends Thread {
        JSONObject prodObject;

        public void run() {
            System.out.println("======================Thread running in auction");
            while (runThread) {

                if (!aucFinished) {
                    System.out.println("=============================================gggg==============");
                    URL mUrl = null;
                    try {
                        mUrl = new URL("http://thegoviyawebservice.azurewebsites.net/api/Auction/?productID=" + product.productID);
                        HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                        httpConnection.setRequestMethod("GET");
                        httpConnection.setRequestProperty("Content-length", "0");
                        httpConnection.setUseCaches(false);
                        httpConnection.setAllowUserInteraction(false);
                        httpConnection.setConnectTimeout(100000);
                        httpConnection.setReadTimeout(100000);
                        httpConnection.connect();

                        int responseCode = httpConnection.getResponseCode();

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            br.close();

                            JSONObject products = null;

                            products = new JSONObject(sb.toString());

                            JSONArray dataObject = products.getJSONArray("Table");

                            prodObject = dataObject.getJSONObject(0);

                            System.out.println(sb);

                            System.out.println(prodObject.get("Column1").toString());

                            String value = prodObject.get("Column1").toString();

                            if (!(value.equalsIgnoreCase(null) || value.equalsIgnoreCase("") || value == null || value.equalsIgnoreCase("null"))) {
                                if(curBid < Double.parseDouble(prodObject.get("Column1").toString())){
                                    curBid = Double.parseDouble(prodObject.get("Column1").toString());
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                currentBid.setBackgroundResource(R.drawable.rounded_edit_text);
                                                currentBid.setText(prodObject.get("Column1").toString());
                                                bidAmount.setHint("higher than " + curBid);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            new CountDownTimer(2000, 1000) {

                                                @Override
                                                public void onTick(long millisUntilFinished) {

                                                }

                                                @Override
                                                public void onFinish() {
                                                    currentBid.setBackgroundResource(0);
                                                }
                                            }.start();


                                        }
                                    });
                                }
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        currentBid.setText(product.price + "");
                                        curBid = product.price;
                                    }
                                });
                            }

                        }
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
