package com.rahul.simpleapiapp.ui.mainActivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.rahul.simpleapiapp.R;
import com.rahul.simpleapiapp.core.ApiInterface;
import com.rahul.simpleapiapp.core.models.Results;
import com.rahul.simpleapiapp.core.response.RandomUserResponse;
import com.rahul.simpleapiapp.receiver.MyReceiver;
import com.rahul.simpleapiapp.services.RunningService;
import com.rahul.simpleapiapp.ui.mainActivity.adapter.UserAdapter;
import com.rahul.simpleapiapp.utils.AppConstant;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    UserAdapter userAdapter;
    List<Results> resultsList;
    @BindView(R.id.main_rv)
    RecyclerView mainRv;

    Realm realm ;
    private RealmResults<Results> requestRealmList;

    AlarmManager alarmManager;
    PendingIntent alarmIntent;
    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        realm=Realm.getDefaultInstance();


        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        serviceIntent = new Intent(this, RunningService.class);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                1);


        enableGps();

        intitRv();
        loadData();
        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime() + 1000,
                        1 * 60 * 1000, alarmIntent); // TODO: 01/08/18 use 5

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    startForegroundService(serviceIntent);
                }
                else
                {
                    startService(serviceIntent);
                }
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void enableGps()
    {
        GoogleApiClient googleApiClient = null;

        if (googleApiClient == null)
        {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>()
            {
                @Override
                public void onResult(LocationSettingsResult result)
                {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode())
                    {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            toast("GPS is not on");
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try
                            {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MainActivity.this, 1000);

                            }
                            catch (IntentSender.SendIntentException e)
                            {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            toast("Setting change not allowed");
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }
    private void toast(String message)
    {
        try
        {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {
            Log.d("closed", "Window has been closed");
        }
    }

    private void loadData() {
        Log.d("TAG", "onResponse: " + "check");

        ApiInterface apiInterface = ApiInterface.retrofit.create(ApiInterface.class);
        final Call<RandomUserResponse> fetchFeedData = apiInterface.randomUserResponseCall();
        fetchFeedData.enqueue(new Callback<RandomUserResponse>() {
            @Override
            public void onResponse(Call<RandomUserResponse> call, final Response<RandomUserResponse> response) {
                try {

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            int i =0;
                            for (Results results : response.body().results) {
                                results.setId(i);
                                realm.copyToRealmOrUpdate(results);
                                i++;
                            }
                        }
                    });

                    init();

                    Log.d("TAG", "onResponse: " + resultsList.size());

                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<RandomUserResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, getString(R.string.no_network_msg), Toast.LENGTH_LONG).show();
                Log.d("TAG", "onFailure: " + t.getLocalizedMessage().toString());
            }
        });

    }

    private void init() {

        if (resultsList != null) {
             requestRealmList = realm.where(Results.class).findAll();
             sortMyData("Default" );

            Log.d("mytag", "init:1 ");
            Log.d("mytag", "init: " + requestRealmList.size());


        }


    }

    private void intitRv() {
        resultsList = new ArrayList<>();
        userAdapter = new UserAdapter(resultsList, this);
        mainRv.setAdapter(userAdapter);
        mainRv.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onDestroy() {
        if(realm!=null){
            realm.close();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.sortby_name: {
                sortMyData(AppConstant.NAME);
                return true;
            }

            case R.id.sortby_mobile: {
                sortMyData(AppConstant.MOBILE);
                return true;
            }

            case R.id.sortby_email: {
                sortMyData(AppConstant.EMAIL);
                return true;
            }

            case R.id.sortby_dob: {
                sortMyData(AppConstant.DATE);
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortMyData(String name) {

        if (requestRealmList==null ) {
            return;
        }
        switch (name) {
            case AppConstant.NAME:
                requestRealmList = requestRealmList.sort(AppConstant.NAME_SORT);
                resultsList = requestRealmList.subList(0, requestRealmList.size());

                break;
            case AppConstant.EMAIL:
                requestRealmList = requestRealmList.sort(AppConstant.EMAIL_SORT);
                resultsList = requestRealmList.subList(0, requestRealmList.size());

                break;
            case AppConstant.DATE:

                requestRealmList = requestRealmList.sort(AppConstant.DOB_SORT);

                resultsList = requestRealmList.subList(0, requestRealmList.size());

                break;
            case AppConstant.MOBILE:
                requestRealmList = requestRealmList.sort(AppConstant.PHONE_SORT);

                resultsList = requestRealmList.subList(0, requestRealmList.size());

                break;
            default:
                resultsList = requestRealmList.subList(0, requestRealmList.size());
                break;

        }




        userAdapter.setFullList(resultsList);


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                1 * 60 * 1000, alarmIntent); // TODO: 01/08/18 use 5

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            startForegroundService(serviceIntent);
        }
        else
        {
            startService(serviceIntent);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
