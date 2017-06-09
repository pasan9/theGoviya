package com.example.saucecode.thegoviya;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MonitorMoisture extends AppCompatActivity {

    private Toolbar toolbar;
    private boolean isBTConnected = false;
    private ProgressDialog progress;
    public static final String PREFS_NAME = "ConnectedAddressFile";
    BluetoothSocket btSocket = null;
    BluetoothAdapter myBT = null;
    String address = "";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String bluDevicename = "";
    ConnectBT conn = new ConnectBT();
    private InputStream in;
    private OutputStream out;
    private int[] values;
    int count = 0;
    private Double moistureLevel = 0.0;
    TextView txtDevice;
    Button btnDetect;
    GetBReading reading = new GetBReading();
    String humidity,temperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check for bt device

        //initialize progress dialog
                progress = new ProgressDialog(this);



        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        boolean hasAddress = prefs.getBoolean("hasAddress", false);
        if (hasAddress) {
            hasBluetoothDevice();
        } else {
            selectBluetoothDevice();
        }









    }


    private void hasBluetoothDevice() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        address = prefs.getString("address", "");
        bluDevicename = prefs.getString("deviceName", "");
        if ((!isBTConnected || btSocket == null) && !address.equalsIgnoreCase(""))
            setLayout(); //setting the layout if device is available
        conn = new ConnectBT();
        conn.execute();

    }



    private void selectBluetoothDevice() { //Asking fot the device BT Address
            Intent intent = new Intent(getApplicationContext(), BluetoothDevices.class);
            startActivityForResult(intent, 1);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //Gettig back address from the BluetoothDevices activity
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                address = data.getStringExtra(BluetoothDevices.EXTRA_ADDRESS);
                hasBluetoothDevice();
            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }








    private class ConnectBT extends AsyncTask<Void, Void, Void> {//UI thread
        String fileName = PREFS_NAME;
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {

          //  progress.setMessage("Initiating connection...");
          //  progress.show();
           //progress.dismiss();
            //System.out.println("hereeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            //System.out.println("******************************************************************************");
            try {
                if (btSocket == null || !isBTConnected) {
                    //System.out.println("))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))");
                    //progress.setMessage("Connecting to moisture sensor.\nPlease switch ON your device");
                    //progress.show();
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
                selectBluetoothDevice();
            } else {
                Toast.makeText(MonitorMoisture.this, "Connected", Toast.LENGTH_SHORT);
                isBTConnected = true;
                reading = new GetBReading();
                //reading.write("#");
                reading.execute();
                /*reading = new SellHarvest.GetBReading();
                //reading.write("#");
                reading.execute();*/
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
            //progress.setMessage("Getting moisture sensor readings.\nPlease keep it inside the container until the values are calculated");
            //progress.show();
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

            try {
                Thread.sleep(3000);
                write("4");
                bytes = in.read(buffer);
                String strReceived = new String(buffer, 0, bytes);
                Thread.sleep(2000);
                write("4");
                bytes = in.read(buffer);
                strReceived = new String(buffer, 0, bytes);
                msgReceived = strReceived;
                humidity = msgReceived;
                Thread.sleep(3000);
                write("3");
                bytes = in.read(buffer);
                strReceived = new String(buffer, 0, bytes);
                Thread.sleep(2000);
                write("3");
                bytes = in.read(buffer);
                strReceived = new String(buffer, 0, bytes);
                msgReceived = strReceived;
                temperature = msgReceived;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
           // progress.setMessage("Calculating average moisture level");
           // progress.show();
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
            //mois.setText(percentage + "%");
            //detect.setVisibility(View.VISIBLE);
            //detect.setClickable(true);
            txtDevice.setText("Temperature : "+temperature+" Humidity : "+humidity+" moisture : "+percentage);
            progress.hide();
            Toast.makeText(getApplicationContext(), "Moisture Detected", Toast.LENGTH_LONG).show();

        }
    }








    void setLayout(){
        setContentView(R.layout.activity_monitor_moisture);
        //App Bar
        toolbar = (Toolbar) findViewById(R.id.my_action_bar_tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        //Buttons and Text Views
        txtDevice = (TextView)findViewById(R.id.txtDevice);
        btnDetect = (Button)findViewById(R.id.btnDetect);

        txtDevice.setText(txtDevice.getText()+" "+address);
        txtDevice.setText(txtDevice.getText()+" "+moistureLevel);


    }


    void setValues(){

    }






}
