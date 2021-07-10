package com.alert.vaccinefinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    TextView txt;
    int flag = 0;
    // String pincode = "", age = "", date = "";
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt = (TextView)findViewById(R.id.details);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //calling the required service
        Button start = (Button)findViewById(R.id.start_service);
        Button stop = (Button)findViewById(R.id.stop_service);
        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startservice();
            }
        });

        stop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                stopservice();
            }
        });
        //------------------

    }

    public void to_slot_page(View v){
        Intent intent = new Intent(getApplicationContext(),SlotCenter.class);

        //get the current date
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(calendar.getTime());
        //---------------------------

        //get age of the user
        Spinner mySpinner = (Spinner) findViewById(R.id.age_of_user_currrent);
        String age_temp = mySpinner.getSelectedItem().toString();
        String age = age_temp.replace("+","");
        //----------------


        //get pincode selected by the user
        EditText pincode = (EditText)findViewById(R.id.bypincode);
        String code = pincode.getText().toString();
        //--------------

        //get the date from the user
        DatePicker picker = (DatePicker)findViewById(R.id.datePickerSpinner);
        String day = String.valueOf(picker.getDayOfMonth()+"/");
        int mnth = picker.getMonth();
        mnth++;
        String month = String.valueOf(mnth)+"/";
        String year = String.valueOf(picker.getYear());
        String req_date = day+month+year;

        //------------

        //get which dose the user wants
        String dose = "";
        RadioGroup rdg = (RadioGroup)findViewById(R.id.rad_grp);
        int selected_id = rdg.getCheckedRadioButtonId();
        if(selected_id != -1){
            RadioButton rdb = (RadioButton)findViewById(selected_id);
            dose = String.valueOf(rdb.getText());
        }
        //---------------------

        if(date.equals("") || pincode.length() != 6 || age.equals("Age") || req_date.equals("") || selected_id == -1){
            Toast.makeText(getApplicationContext(),"Please Fill details properly",Toast.LENGTH_SHORT).show();
            return;
        }

        //create bundle and bind data to it
        Bundle bundle = new Bundle();
        bundle.putString("Pincode",code);
        bundle.putString("Date",req_date);
        bundle.putString("Age",age);
        bundle.putString("Dose",dose);
        intent.putExtras(bundle);
        //---------------

        startActivity(intent);
    }




    public void startservice(){
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service");
        //the data
        Spinner mySpinner = (Spinner) findViewById(R.id.age_of_user);
        String age_temp = mySpinner.getSelectedItem().toString();
        String age = age_temp.replace("+","");
        EditText get_notification_pin = (EditText)findViewById(R.id.pincode);
        String pin = get_notification_pin.getText().toString();
        //get which dose the user wants
        String dose = "";
        RadioGroup rdg = (RadioGroup)findViewById(R.id.rad_grp2);
        int selected_id = rdg.getCheckedRadioButtonId();
        if(selected_id != -1){
            RadioButton rdb = (RadioButton)findViewById(selected_id);
            dose = String.valueOf(rdb.getText());
        }
        //---------------------

        if(age.equals("Age") || pin.length() != 6 || selected_id == -1){
            Toast.makeText(getApplicationContext(),"Please fill the details correctly.",Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("Pincode", pin);
        bundle.putString("Age", age);
        bundle.putString("Dose",dose);
        serviceIntent.putExtras(bundle);
        //---------
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopservice(){
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
        Toast.makeText(getApplicationContext(),"Request cancelled successfully!",Toast.LENGTH_LONG).show();
    }






   /* //share when clicked
    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.menu is a reference to an xml file named menu.xml which should be inside res/menu directory.

        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);


    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.app_share_button) {
            String data = "https://play.google.com/store/apps/details?id=com.starfleck.vaccinemitra";
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,data);
            sendIntent.setType("text/plain");
            Intent.createChooser(sendIntent,"Share via");
            startActivity(sendIntent);
        }
        return super.onOptionsItemSelected(item);
    }*/

}