package com.rahul.simpleapiapp.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.rahul.simpleapiapp.R;
import com.rahul.simpleapiapp.ui.mainActivity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;

public class RunningService extends Service
{
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;


    public RunningService()
    {
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.i("MyTag", "Service Started");


        // Notification work
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationBuilder.setContentIntent(PendingIntent.getActivity(this, 799, i, 0));

        final Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();
        startForeground(10012, notification);

        requestUpdates();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (shouldRun)
                {
                    try
                    {
                        Thread.sleep(1000);
                        count++;
                        Log.i("MyTag", "" + count);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void requestUpdates()
    {
        // Google services
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        mLocationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                if (locationResult == null)
                {
                    Log.i("MyTag2", "Error in onLocationChanged()");
                    return;
                }
                 postNotification(locationResult.getLastLocation());
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();


        mLocationRequest.setInterval(2 * 60 * 1000);
        mLocationRequest.setFastestInterval(2 * 60 * 1000);
        mLocationRequest.setSmallestDisplacement( 100 );

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null);
    }

    private void postNotification(Location lastLocation) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationBuilder.setContentIntent(PendingIntent.getActivity(this, 799, i, 0));
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        final Notification notification = notificationBuilder
                .setSound(uri)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MAX)
                .setContentTitle("Location Update ")
                .setContentText("Lat : - " +lastLocation.getLatitude() + "  \n"+"Longit : - "+lastLocation.getLongitude())
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        notificationManager.notify(45 , notification);
    }

    int count;
    boolean shouldRun = true;

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i("MyTag", "onStartCommand");
        mLocationCallback = null;
        mFusedLocationClient = null;
        mLocationRequest = null;
        requestUpdates();

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);
        Log.i("MyTag", "Service removed from Task");
    }

    @Override
    public void onDestroy()
    {
        Log.i("MyTag", "Service destroyed");

        shouldRun = false;

        if (mFusedLocationClient != null)
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }




    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager)
    {
        String channelId = "my_service_channelid";
        String channelName = "My Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }
}
