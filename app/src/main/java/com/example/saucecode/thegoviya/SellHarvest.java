package com.example.saucecode.thegoviya;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class SellHarvest extends AppCompatActivity {

    private Spinner cropList;
    private Spinner sellMethod;
    private Button addBtn;
    private Button detect, setTime, setDate;
    private EditText qty, mois, minPrice, bidTime;
    private ProgressDialog progress;

    private int selectedCrop = 0;
    private int selectedMethod = 0;
    private List<String> list = new ArrayList<String>();
    private List<String> sellMethodList = new ArrayList<String>();

    private String userNIC = "";
    private String userFName = "";
    private Double unitPrice = 0.0;
    private Double moistureLevel = 0.0;
    private Double quantitiy = 0.0;
    private String bidDateTime = "";
    private int[] values;
    int count = 0;

    public static final String PREFS_NAME = "ConnectedAddressFile";
    String bluDevicename = "";
    String address = "";
    BluetoothAdapter myBT = null;
    BluetoothSocket btSocket = null;
    private boolean isBTConnected = false;
    private InputStream in;
    private OutputStream out;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ConnectBT conn = new ConnectBT();
    GetBReading reading = new GetBReading();

    private Calendar cal = Calendar.getInstance();
    private Dialog dialog;
    private int currentHour = 0;
    private int currentMinute = 0;
    private int givenHour = cal.get(Calendar.HOUR_OF_DAY);
    private int givenMinute = cal.get(Calendar.MINUTE);
    private int givenDay = cal.get(Calendar.DAY_OF_MONTH);
    private int givenMonth = cal.get(Calendar.MONTH)+1;
    private int givenYear = cal.get(Calendar.YEAR);
    private String dateTime = givenYear+"-"+givenMonth+"-"+givenDay+" "+givenHour+":"+givenMinute+":"+"00";
    private LinearLayout bidTimeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_harvest);

        bidTimeLayout = (LinearLayout) findViewById(R.id.bidTimeLayout);

        CurrentUser user = new CurrentUser();
        userNIC = user.getNicNumber();
        userFName = user.getfName();
        detect = (Button) findViewById(R.id.detectMois);
        progress = new ProgressDialog(this);
        cropList = (Spinner) findViewById(R.id.cropList);
        addBtn = (Button) findViewById(R.id.addBtn);
        qty = (EditText) findViewById(R.id.qty);
        mois = (EditText) findViewById(R.id.mois);
        minPrice = (EditText) findViewById(R.id.minPrice);
        bidTime = (EditText) findViewById(R.id.bidTime);
        sellMethod = (Spinner) findViewById(R.id.sellMethod);
        setTime = (Button) findViewById(R.id.setTime);
        setDate = (Button) findViewById(R.id.setDate);

        list.add("Samba -White Rice");
        list.add("Samba - Red Rice");
        list.add("Baasmathi - Red Rice");
        list.add("Rathu Kekulu");
        list.add("Sudu Kekulu");
        list.add("Naadu Rice");
        list.add("Kalu Heenati");
        list.add("Suwandel");
        //list.add("Onions");

        sellMethodList.add("Sell it now");
        sellMethodList.add("Auction");

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDate();
            }
        });

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogTime();
            }
        });


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cropList.setAdapter(dataAdapter);

        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, sellMethodList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sellMethod.setAdapter(dataAdapter);

        cropList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSelectedItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
                boolean hasAddress = prefs.getBoolean("hasAddress", false);
                if (hasAddress) {
                    hasBluetoothDevice();
                } else {
                    selectBluetoothDevice();
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantitiy = Double.parseDouble(qty.getText().toString());
                //if(selectedCrop == 1)moistureLevel = Double.parseDouble(mois.getText().toString());
                unitPrice = Double.parseDouble(minPrice.getText().toString());
                dateTime = bidTime.getText().toString();
                progress.setMessage("Adding product...");
                progress.show();
                AddProduct add = new AddProduct();
                add.execute("");

            }
        });

        sellMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSellingMethod(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showDialogDate() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.datepicker);
        dialog.setTitle("Select Date");
        Calendar mcurrentTime = Calendar.getInstance();
        currentHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        currentMinute = mcurrentTime.get(Calendar.MINUTE);
        // set the custom dialog components - text, image and button
        final DatePicker thedatePick = (DatePicker) dialog.findViewById(R.id.datepick);
        Button done = (Button) dialog.findViewById(R.id.timepicked);

        // if button is clicked, close the custom dialog
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                givenDay = thedatePick.getDayOfMonth();
                givenMonth = thedatePick.getMonth()+1;
                givenYear = thedatePick.getYear();
                bidTime.setText(givenYear+"-"+givenMonth+"-"+givenDay+" "+givenHour+":"+givenMinute+":"+"00");
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showDialogTime() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.datetimepicker);
        dialog.setTitle("Select Time");
        Calendar mcurrentTime = Calendar.getInstance();
        currentHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        currentMinute = mcurrentTime.get(Calendar.MINUTE);
        // set the custom dialog components - text, image and button
        TimePicker thetimePick = (TimePicker) dialog.findViewById(R.id.timepick);
        thetimePick.setIs24HourView(true);
        Button done = (Button) dialog.findViewById(R.id.timepicked);
        thetimePick.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                givenHour = hourOfDay;
                givenMinute = minute;
                bidTime.setText(givenYear+"-"+givenMonth+"-"+givenDay+" "+givenHour+":"+givenMinute+":"+"00");
            }
        });

        // if button is clicked, close the custom dialog
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void hasBluetoothDevice() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        address = prefs.getString("address", "");
        bluDevicename = prefs.getString("deviceName", "");
        if ((!isBTConnected || btSocket == null) && !address.equalsIgnoreCase(""))
            detect.setVisibility(View.INVISIBLE);
        conn = new ConnectBT();
        conn.execute();

    }

    private void selectBluetoothDevice() {
        Intent intent = new Intent(getApplicationContext(), BluetoothDevices.class);
        startActivityForResult(intent, 1);
    }

    @Override
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

    public void getSelectedItem(int pos) {
        selectedCrop = pos;
    }

    public void getSellingMethod(int pos) {
        selectedMethod = pos;
        if (pos == 1) {
            bidTime.setText(givenYear+"-"+givenMonth+"-"+givenDay+" "+givenHour+":"+givenMinute+":"+"00");
            bidTimeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, homeActivity.class);
        startActivity(intent);
        this.finish();
    }

    private String productType(int selectedCrop) {
        String crop = "";
        crop = list.get(selectedCrop);
        return crop;
    }


    public class AddProduct extends AsyncTask<String, String, String> {

        public JSONObject getJSONObject() {
            Double value = 0.0;
            String productType = productType(selectedCrop);
            //String sellingMethod = (selectedMethod == 1) ? "Auction" : "Sell it now";
            Products prod = new Products(userNIC, quantitiy, unitPrice, moistureLevel, productType, sellMethodList.get(selectedMethod),dateTime);

            JSONObject obj = new JSONObject();
            try {
                obj.put("farmerID", userNIC);
                obj.put("Quantity", quantitiy);
                obj.put("UnitPrice", unitPrice);
                obj.put("MoistureLevel", moistureLevel);
                obj.put("ProductType", productType);
                obj.put("SellingMethod", sellMethodList.get(selectedMethod));
                obj.put("BidDuration", dateTime);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return obj;
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection httpConnection;
            URL mUrl;
            try {
                mUrl = new URL("http://thegoviyawebservice.azurewebsites.net/api/Product");
                httpConnection = (HttpURLConnection) mUrl.openConnection();
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "application/json");
                httpConnection.setRequestProperty("Content-length", "" + getJSONObject().toString().length() + "");
                httpConnection.setDoInput(true);
                httpConnection.setDoOutput(true);
                httpConnection.connect();
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
            if (s.equalsIgnoreCase("success")) {
                progress.setMessage("Added");
                progress.show();
                progress.dismiss();
                Toast.makeText(SellHarvest.this, "Product added!", Toast.LENGTH_SHORT);
                Intent intent = new Intent(SellHarvest.this, homeActivity.class);
                startActivity(intent);
                SellHarvest.this.finish();
            } else {
                progress.setMessage("Failed");
                progress.show();
                progress.dismiss();
                Toast.makeText(SellHarvest.this, "Product add failed!", Toast.LENGTH_SHORT);
                Intent intent = new Intent(SellHarvest.this, homeActivity.class);
                startActivity(intent);
                SellHarvest.this.finish();
            }
        }
    }

    public void hideProgress() {
        progress.hide();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {//UI thread
        String fileName = PREFS_NAME;
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            detect.setClickable(false);
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
                detect.performClick();
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
            mois.setText(percentage + "%");
            detect.setVisibility(View.VISIBLE);
            detect.setClickable(true);
            progress.hide();
            Toast.makeText(getApplicationContext(), "Moisture level updated", Toast.LENGTH_LONG).show();

        }
    }
}
