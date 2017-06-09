package com.example.saucecode.thegoviya;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public class BluetoothDevices extends Activity {

    private ListView pairedList ;
    public static final String PREFS_NAME = "ConnectedAddressFile";
    private String address =null;
    private BluetoothAdapter myBT = null;
    private Set<BluetoothDevice> pairedDevices;
    private OutputStream outStream = null;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth_devices);
        System.out.println("=============hereeeeeeeeee=====================================================================");
        pairedList = (ListView)findViewById(R.id.pairedList);
        myBT = BluetoothAdapter.getDefaultAdapter();
        if(myBT == null){
            Toast.makeText(getApplicationContext(), "This device does not have a bluetooth adapter", Toast.LENGTH_LONG).show();
        } else {
            if(!myBT.isEnabled()){
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            }
        }

        pairedDevicesList();

    }


    private void pairedDevicesList(){

        pairedDevices = myBT.getBondedDevices();
        ArrayList list = new ArrayList();

        if(pairedDevices.size()>0){

            for(BluetoothDevice bt:pairedDevices){ //loop through the paired list

                list.add(bt.getName()+"\n"+bt.getAddress()); // get device name and address
            }
        }

        else{
            //print a message : No paired devices
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        pairedList.setAdapter(adapter);
        pairedList.setOnItemClickListener(myListClickListener); //called when one of the devices is clicked

    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener(){
        String fileName = PREFS_NAME;
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Getting the mac address : The last 17 Chars in the view

            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length()-17);

            SharedPreferences prefs = getSharedPreferences(this.fileName, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("hasAddress", true);
            editor.putString("deviceName", info);
            editor.putString("address", address);
            editor.commit();
            passAddress();
        }
    };

    private void passAddress(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_ADDRESS,address);
        setResult(Activity.RESULT_OK,returnIntent);
        System.out.println("Came here! 1 =============================================1111111111111111111111111111111111111111111======");
        finish();
    }
}
