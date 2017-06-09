package com.example.saucecode.thegoviya;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class UserProductsInfo extends AppCompatActivity implements View.OnClickListener{

    private TextView ProductType, SaleType, Moisture, MoistureUpdate, currentBid, bidTime, PriceBid;
    private EditText Price,Qty;
    private Button contactBuyer, updatePrice, updateQty, updateMois;
    private Products product;
    private String MoistureUpDate = "22/05/2017";
    private String MoistureUpTime = "10:23";
    private CountDownTimer timer;
    private boolean runThread = true;
    private boolean aucFinished = false;
    private double curBid = 0.0;
    private GetCurrentHighestBid thread = new GetCurrentHighestBid();
    private ProgressDialog progress;
    private ArrayList<BidderInfo> bidList = new ArrayList<BidderInfo>();
    private ListView listView;
    private CustomAdapterFarmerBidders adapter;
    private URL mUrl = null;
    private int responseCode;

    public static final String PREFS_NAME = "ConnectedAddressFile";
    private String bluDevicename = "";
    private String address = "";
    private BluetoothAdapter myBT = null;
    private BluetoothSocket btSocket = null;
    private boolean isBTConnected = false;
    private InputStream in;
    private OutputStream out;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectBT conn = new ConnectBT();
    private GetBReading reading = new GetBReading();
    private Double moistureLevel = 0.0;
    private int[] values;
    private int count = 0;
    private String moisUpdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        product = (Products) getIntent().getSerializableExtra("product");

        String[] array = product.MoisUpdate.split("T");
        String date = array[0];
        String time = array[1];
        MoistureUpDate = date;
        MoistureUpTime = time;

        if (product.sellingMethod.equalsIgnoreCase("Sell it now")) {
            setContentView(R.layout.activity_user_direct_products_info);
            ProductType = (TextView) findViewById(R.id.productType);
            Price = (EditText) findViewById(R.id.priceTxt);
            Qty = (EditText) findViewById(R.id.QtyTxt);
            Moisture = (TextView) findViewById(R.id.moistureTxt);
            MoistureUpdate = (TextView) findViewById(R.id.MoistureLastUpTxt);
            updateQty = (Button) findViewById(R.id.updateQty);
            updateMois = (Button) findViewById(R.id.updateMois);
            updatePrice = (Button) findViewById(R.id.updatePrice);
            updateQty.setOnClickListener(this);
            updateMois.setOnClickListener(this);
            updatePrice.setOnClickListener(this);

            progress = new ProgressDialog(this);
            System.out.println("============"+product.type);
            ProductType.setText(product.type);
            Price.setText(product.price+"");
            Qty.setText(product.qty+"");
            Moisture.setText(product.mois + "%");
            MoistureUpdate.setText("Last Updated On : " + MoistureUpDate + " at " + MoistureUpTime);

        } else {

            setContentView(R.layout.user_products_auction);

            ProductType = (TextView) findViewById(R.id.bidProductType);
            PriceBid = (TextView) findViewById(R.id.bidPriceTxt);
            Qty = (EditText) findViewById(R.id.bidQtyTxt);
            Moisture = (TextView) findViewById(R.id.bidMoistureTxt);
            currentBid = (TextView) findViewById(R.id.currentHBidValue);
            MoistureUpdate = (TextView) findViewById(R.id.bidMoistureLastUpTxt);
            bidTime = (TextView) findViewById(R.id.bidTimeLimit);
            updateQty = (Button) findViewById(R.id.updateQty);
            updateMois = (Button) findViewById(R.id.updateMois);
            updateQty.setOnClickListener(this);
            updateMois.setOnClickListener(this);

            setBidTimer();
            progress = new ProgressDialog(this);
            ProductType.setText(product.type);
            PriceBid.setText(product.price+"");
            Qty.setText(product.qty+"");
            Moisture.setText(product.mois + "%");
            MoistureUpdate.setText("Last Updated On : " + MoistureUpDate + " at " + MoistureUpTime);
            DisplayBidders bidders = new DisplayBidders();
            bidders.execute();

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
                    final AlertDialog.Builder exitDialog = new AlertDialog.Builder(UserProductsInfo.this);
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
        bidTime.setText("Auction Closed");
    }

    @Override
    public void onClick(View v) {
        if(v == updateQty){
            try {
                mUrl = new URL("http://thegoviyawebservice.azurewebsites.net/api/Product/?productID="+product.productID+"&columnName=Quantity&value="+Qty.getText().toString());
                System.out.println(mUrl);
                UpdateData up = new UpdateData();
                up.execute();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }

        if(v == updatePrice){
            try {
                mUrl = new URL("http://thegoviyawebservice.azurewebsites.net/api/Product/?productID="+product.productID+"&columnName=UnitPrice&value="+Price.getText().toString());
                UpdateData up = new UpdateData();
                up.execute();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }

        if(v == updateMois){

            Calendar cal = Calendar.getInstance();
            moisUpdate = cal.get(Calendar.YEAR)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.DAY_OF_MONTH)+" "+cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE)+":"+"00";

            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
            boolean hasAddress = prefs.getBoolean("hasAddress", false);
            if (hasAddress) {
                hasBluetoothDevice();
            } else {
                selectBluetoothDevice();
            }



        }
    }

    private void hasBluetoothDevice() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        address = prefs.getString("address", "");
        bluDevicename = prefs.getString("deviceName", "");
        if ((!isBTConnected || btSocket == null) && !address.equalsIgnoreCase(""))
            updateMois.setVisibility(View.INVISIBLE);
        conn = new ConnectBT();
        conn.execute();

    }

    private void selectBluetoothDevice() {
        Intent intent = new Intent(getApplicationContext(), BluetoothDevices.class);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                address = data.getStringExtra(BluetoothDevices.EXTRA_ADDRESS);
                hasBluetoothDevice();
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
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

    private class DisplayBidders extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            /*progressDialog = new ProgressDialog(DisplayProducts.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Loading product list, Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);*/
            // progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            URL mUrl = null;
            try {
                mUrl = new URL("http://thegoviyawebservice.azurewebsites.net/api/Auction/?productID="+product.productID+"&showBidders=true");
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
                    System.out.println("=============================================gggg==============");
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    /*synchronized (this) {
                        int counter = 0;
                        while (counter <= 4) {
                            this.wait(10);
                            counter++;
                            publishProgress(counter * 25);
                        }
                    }*/

                    System.out.println("=========================================================="+sb);

                    return sb.toString();

                }

                httpConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {

            System.out.println("===========================================================");
            System.out.println(s);

            JSONObject bids = null;

            BidderInfo bidObj;
            bidList.clear();
            try {
                bids = new JSONObject(s);

                JSONArray dataObject = bids.getJSONArray("Table");

                for (int i = 0; i < dataObject.length(); i++) {
                    JSONObject bidObject = dataObject.getJSONObject(i);
                    bidObj = new BidderInfo(bidObject.getString("buyerID"), bidObject.getInt("productID"),bidObject.getString("fullName"),bidObject.getString("address"),bidObject.getInt("mobile"), bidObject.getDouble("bidAmount"));
                    bidList.add(bidObj);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e1) {
                e1.printStackTrace();
            }

            listView = (ListView) findViewById(R.id.userBidderList);
            populateProductList();
            /*swiper = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshDis);
            swiper.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
            swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    onOptionsItemSelected(R.id.action_refresh);
                }
            });

            loadNavBar();*/
        }
    }

    private void populateProductList() {
        adapter = new CustomAdapterFarmerBidders(bidList, getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BuyerBid dataModel = bidList.get(position);

            }
        });
    }

    private class UpdateData extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("Content-length", "0");
                httpConnection.setUseCaches(false);
                httpConnection.setAllowUserInteraction(false);
                httpConnection.setConnectTimeout(100000);
                httpConnection.setReadTimeout(100000);
                httpConnection.connect();

                responseCode = httpConnection.getResponseCode();

                httpConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if(responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_OK){
                Toast.makeText(UserProductsInfo.this, "Values updated", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            } else {
                Toast.makeText(UserProductsInfo.this, "Failed", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {//UI thread
        String fileName = PREFS_NAME;
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            updateMois.setClickable(false);
            progress.setMessage("Initiating connection...");
            progress.show();
            System.out.println("hereeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            System.out.println("******************************************************************************");
            try {
                if (btSocket == null || !isBTConnected) {
                    System.out.println("))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))");
                    progress.setMessage("Connecting to moisture sensor.\nPlease switch ON your device");
                    progress.show();
                    myBT = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBT.getRemoteDevice(address);
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();

                }
            } catch (IOException e) {
                ConnectSuccess = false;
                e.printStackTrace();
            }


            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!ConnectSuccess) {

                isBTConnected = false;
                Toast.makeText(getApplicationContext(), "Please Select your Device", Toast.LENGTH_LONG).show();
                SharedPreferences prefs = getSharedPreferences(fileName, 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("hasAddress", false);
                editor.putString("deviceName", "");
                editor.putString("address", "");
                editor.commit();
                updateMois.performClick();
            } else {
                progress.setMessage("Connected");
                progress.show();
                isBTConnected = true;
                reading = new GetBReading();
                //reading.write("#");
                reading.execute();
            }
        }
    }

    private class GetBReading extends AsyncTask<Void, Void, Void> {//UI thread
        byte[] buffer = new byte[256];
        int bytes;
        private String msgReceived = "";

        @Override
        protected void onPreExecute() {
            try {
                in = btSocket.getInputStream();
                out = btSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            values = new int[10];
            count = 0;
            progress.setMessage("Getting moisture sensor readings.\nPlease keep it inside the container until the values are calculated");
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... devices) {
            while (count < 10) {
                try {

                    write("5");
                    bytes = in.read(buffer);
                    String strReceived = new String(buffer, 0, bytes);
                    msgReceived = strReceived;
                    String[] array = msgReceived.split("\n");
                    msgReceived = array[array.length - 1];
                    if (msgReceived.length() > 2 && Integer.parseInt(array[array.length - 1].trim().toString()) > 200) {
                        values[count] = Integer.parseInt(array[array.length - 1].trim().toString());
                        count++;
                    }


                } catch (IOException e) {
                    break;
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progress.setMessage("Calculating average moisture level");
            progress.show();
            checkStatus();

        }

        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                out.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }


    }


    private void checkStatus() {
        if (count == 10) {

            conn.cancel(true);

            int total = 0;
            for (int i = 0; i < 10; i++) {
                total += values[i];
            }
            double average = total / 10;
            double percentage = 100.00 - Math.round(((average / 1023.0) * 100) * 100) / 100;
            progress.setMessage("Done!");
            progress.show();
            moistureLevel = percentage;
            Moisture.setText(percentage + "%");
            updateMois.setVisibility(View.VISIBLE);
            updateMois.setClickable(true);
            progress.hide();
            Toast.makeText(getApplicationContext(), "Moisture level updated", Toast.LENGTH_LONG).show();
            try {
                mUrl = new URL("http://thegoviyawebservice.azurewebsites.net/api/Product/?productID="+product.productID+"&columnName1=MoistureLevel&value1="+moistureLevel+"&columnName2=MoisUpdate&value2="+moisUpdate);
                UpdateData up = new UpdateData();
                up.execute();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}




