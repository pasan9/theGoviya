package com.example.saucecode.thegoviya;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CurrentUser current = new CurrentUser();
    private Button loginBtn;
    private EditText nic;
    private EditText password;
    private TextView regInForm;
    private ProgressDialog progress;
    private String uNic;
    private String uPass;
    private int count = 1;
    public static final String PREFS_NAME = "MyLoginStatusFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        /*if(true)*/
        Person person = (Person) intent.getSerializableExtra("person");
        if(person != null) {
            sharedPreferenceStore(person);
        }

        SharedPreferences settings = getSharedPreferences(this.PREFS_NAME, 0);

        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

        if (hasLoggedIn) {
            current.setNicNumber(settings.getString("nicNumber", null));
            current.setfName(settings.getString("fName", null));
            current.setAddress(settings.getString("address", null));
            current.setType(settings.getString("type", null));
            current.setMobileNumber(settings.getInt("mobileNumber", 0));

            if (current.getType().equalsIgnoreCase("farmer")) loadHome();
            else buyerHome();

        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        progress = new ProgressDialog(this);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        nic = (EditText) findViewById(R.id.nic);
        password = (EditText) findViewById(R.id.password);
        regInForm = (TextView) findViewById(R.id.txtViewlogin);

        loginBtn.setOnClickListener(this);
        regInForm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == loginBtn) {
            loginUser();
            hideKeyboard(this);
        }

        if (v == regInForm) {
            Intent reginIntent = new Intent(this, regActivity.class);
            startActivity(reginIntent);
            this.finish();
        }
    }

    private void loginUser() {
        uNic = nic.getText().toString().trim();
        uPass = password.getText().toString().trim();

        if (TextUtils.isEmpty(uNic)) {
            Toast.makeText(this, "Please enter an email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(uPass)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setMessage("Signing...");
        progress.show();

        CheckLogin checkLogin = new CheckLogin();
        checkLogin.execute("");

    }

    @Override
    public void onBackPressed() {
        if (count == 1)
            count++;
        else
            super.onBackPressed();
    }

    public void loadHome() {
        Intent homeIntent = new Intent(this, homeActivity.class);
        startActivity(homeIntent);
        this.finish();
    }

    public void buyerHome() {
        Intent homeIntent = new Intent(this, BuyerHome.class);
        startActivity(homeIntent);
        this.finish();
    }


    public class CheckLogin extends AsyncTask<String, String, String> {
        String result = "";

        @Override
        protected String doInBackground(String... params) {
            /*Crud con = new Crud();
            result = con.connectDBLogin("SELECT * FROM users WHERE NIC='"+uNic+"' AND password='"+uPass+"';");*/
            URL mUrl = null;
            try {
                mUrl = new URL("http://thegoviyawebservice.azurewebsites.net/api/Farmer/?NIC="+uNic+"&password="+uPass);
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

                    return sb.toString();

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Success";
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println("===========================================================");
            System.out.println(s);

            JSONObject personObject = null;
            Person person = new Person();

            try {
                personObject = new JSONObject(s);
                JSONArray dataObject = personObject.getJSONArray("Table");
                JSONObject json = dataObject.getJSONObject(0);
                person.setNicNumber(json.getString("NIC"));
                person.setfName(json.getString("fullName"));
                person.setAddress(json.getString("address"));
                person.setMobileNumber(json.getInt("mobile"));
                person.setType(json.getString("type"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (person.getType().equalsIgnoreCase("farmer")) {
                sharedPreferenceStore(person);
                loadHome();
            } else if (person.getType().equalsIgnoreCase("buyer")) {
                sharedPreferenceStore(person);
                buyerHome();
            } else {
                progress.hide();
                Toast.makeText(MainActivity.this, "Failed to login! try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void sharedPreferenceStore(Person person) {

        SharedPreferences settings = getSharedPreferences(this.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        current.setNicNumber(person.getNicNumber());
        current.setfName(person.getfName());
        current.setAddress(person.getAddress());
        current.setType(person.getType());
        current.setMobileNumber(person.getMobileNumber());

        editor.putBoolean("hasLoggedIn", true);
        editor.putString("nicNumber", person.getNicNumber());
        editor.putString("fName", person.getfName());
        editor.putString("address", person.getAddress());
        editor.putString("type", person.getType());
        editor.putInt("mobileNumber", person.getMobileNumber());
        editor.commit();
    }
}
