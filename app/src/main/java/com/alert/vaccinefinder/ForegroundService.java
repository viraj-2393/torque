package com.alert.vaccinefinder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ForegroundService extends Service {
    private static final String USER_AGENT = "Mozilla/5.0";
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    Intent intent;
    Thread thread;
    MediaPlayer mediaPlayer;

    public ForegroundService() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        public void run() {
            int result;
            result = get_required_details(intent);
            if (result == 1) {
                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.vacc_avl);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            stopForeground(STOP_FOREGROUND_DETACH);
                        }
                        stopSelf();

                    }

                });
                handler.removeCallbacks(runnable);

            } else {
                //Log.i("Testing callback", "It's working as expected.");
                handler.postDelayed(this, 90000);
            }

        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean is_Internet_Available = activeNetworkInfo != null && activeNetworkInfo.isConnected();


            createNotificationChannel();
            this.intent = intent;
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Checking for slots")
                    .setContentText("I will notify you once the slots are available.")
                    .setSmallIcon(R.drawable.injection)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);
            //do heavy work on a background thread
        if(!is_Internet_Available) {
            Toast.makeText(getApplicationContext(),"No Internet! Please try connecting to a network.",Toast.LENGTH_LONG).show();
            stopSelf();
        }
        else {
            Toast.makeText(getApplicationContext(),"I will notify you once the slots are available.",Toast.LENGTH_LONG).show();
            thread = new Thread(runnable);
            thread.start();
        }
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    //get all the required details
    private int get_required_details(Intent intent){
        String pincode=(String) intent.getExtras().get("Pincode");
        String age = (String) intent.getExtras().get("Age");
        String dose_num = (String) intent.getExtras().get("Dose");

        if(dose_num.equals(" Dose 1")){
            dose_num = "available_capacity_dose1";
        }
        else if(dose_num.equals(" Dose 2")){
            dose_num = "available_capacity_dose2";
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String date = dateFormat.format(calendar.getTime());
        int result = call_the_api(pincode, age, date,dose_num, this);
        return result;
    }
    //-----------------

    //api call begins here
    public int call_the_api(String pincode,String age, String date,String dose, Context context) {
        try {

            String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByPin";
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
                    JSONArray arr = jsonObject.getJSONArray("centers");
                    int capacity = 0;
                    int min_age;
                    for (int i = 0; i < arr.length(); i++) {
                        JSONArray arr_session = arr.getJSONObject(i).getJSONArray("sessions");

                        for(int j = 0; j < arr_session.length(); j++){
                            min_age = Integer.parseInt(arr_session.getJSONObject(j).getString("min_age_limit"));
                            if(min_age == Integer.parseInt(age))
                            {
                                capacity += Integer.parseInt(arr_session.getJSONObject(j).getString(dose));
                            }

                        }

                    }
                    if (capacity > 0) {
                        //Toast.makeText(context,String.valueOf(capacity),Toast.LENGTH_LONG).show();
                        fire_the_notification(capacity,context,pincode,date,age);
                        return 1;
                    }

                } catch (Exception ex) {
                    Toast.makeText(context, String.valueOf(ex), Toast.LENGTH_LONG).show();
                }
            } else {
                //Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            //Toast.makeText(context, String.valueOf(ex), Toast.LENGTH_LONG).show();
            //Log.i("ex",String.valueOf(ex));
        }
        return 0;
    }
    //api calling ends here

    public void fire_the_notification(int available_vaccines, Context context,String code,String date,String age){

        Intent intent = new Intent(context, AlertDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.injection)
                .setContentTitle(String.valueOf(available_vaccines)+" Vaccines available!")
                .setContentText("Please schedule your appointment now.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }
}
