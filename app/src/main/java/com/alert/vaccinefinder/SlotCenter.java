package com.alert.vaccinefinder;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SlotCenter extends AppCompatActivity {
    private static final String USER_AGENT = "Mozilla/5.0";
    String dose_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slotcenter);
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean is_Internet_Available = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if(is_Internet_Available) {
            //restore the visibility of listview
            ListView lst_v = (ListView)findViewById(R.id.list);
            lst_v.setVisibility(View.VISIBLE);
            //-----------



            //allow network on main thread
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //-------------------------

            Bundle bundle = getIntent().getExtras();
            String p = bundle.getString("Pincode");
            String d = bundle.getString("Date");
            String a = bundle.getString("Age");
            dose_num = bundle.getString("Dose");
            if(dose_num.equals(" Dose 1")){
                dose_num = "available_capacity_dose1";
            }
            else if(dose_num.equals(" Dose 2")){
                dose_num = "available_capacity_dose2";
            }

            getAllSlots(p, d, a);
        }
        else{
            ImageView img_v = (ImageView)findViewById(R.id.no_internet);
            img_v.setVisibility(View.VISIBLE);
            img_v.setImageResource(R.drawable.no_internet);
            TextView textView = (TextView)findViewById(R.id.no_internet_text);
            textView.setVisibility(View.VISIBLE);

        }
    }

    public  void getAllSlots(String pincode, String date, String age){
        ArrayList<Slot> slots = new ArrayList<>();
        try {
            int flag = 0;
            String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByPin";
            url += "?pincode=" + pincode + "&date=" + date;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);

                }
                in.close();

                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray arr = jsonObject.getJSONArray("sessions");

                    for (int i = 0; i < arr.length(); i++)
                    {
                        if(age.equals(arr.getJSONObject(i).getString("min_age_limit"))){
                            if(Integer.parseInt(arr.getJSONObject(i).getString(dose_num)) > 0 ) {
                                Slot slt = new Slot(arr.getJSONObject(i).getString("name"), arr.getJSONObject(i).getString(dose_num),
                                        arr.getJSONObject(i).getString("min_age_limit"), arr.getJSONObject(i).getString("vaccine"),
                                        arr.getJSONObject(i).getString("fee_type"), arr.getJSONObject(i).getString("fee"),
                                        arr.getJSONObject(i).getString("from"),
                                        arr.getJSONObject(i).getString("to"), arr.getJSONObject(i).getString("pincode"),
                                        arr.getJSONObject(i).getString("block_name"), arr.getJSONObject(i).getString("district_name"));
                                slots.add(slt);
                            }
                        }


                    }
                  SlotAdapter adapter = new SlotAdapter(this,slots);
                    ListView list = (ListView)findViewById(R.id.list);
                    if(slots.size() == 0) {
                        list.setVisibility(View.GONE);
                        ImageView img_v = (ImageView)findViewById(R.id.no_internet);
                        img_v.setVisibility(View.VISIBLE);
                        img_v.setImageResource(R.drawable.sad);
                        TextView textView = (TextView)findViewById(R.id.no_internet_text);
                        textView.setText("No Vaccines Available...");
                        textView.setVisibility(View.VISIBLE);
                    }
                    else {
                        list.setAdapter(adapter);
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(),String.valueOf(ex),Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception ex){
            Toast.makeText(getApplicationContext(),String.valueOf(ex),Toast.LENGTH_LONG).show();
        }
    }


}
