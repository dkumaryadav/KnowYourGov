package com.deepakyadav.knowyourgovernment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int MY_LOCATION_REQUEST_CODE_ID = 329;

    List<Official> officialList = new ArrayList<>();
    OfficialAdapter officialAdapter;
    RecyclerView recyclerView;
    TextView currentLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;
    String dataNotFound = "No Data for location";

    @Override
    protected void onCreate(Bundle outState) {

        Log.d(TAG, "onCreate: STARTED");
        super.onCreate(outState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: "+outState);

        recyclerView = findViewById(R.id.recyclerView);
        currentLocation = findViewById(R.id.locationDisplay);
        officialAdapter = new OfficialAdapter(officialList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(officialAdapter);

        // Check if we have n/w connectivity
        if ( ! checkConnectivity() )
            dataNotAvailable();
        else{
            // Check for permissions
            if( !checkForPermission() ){
                requestLocationPermission();
            } else
                getZipCode();
            }
    }

    // Adding options menu to add note and go to help
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Handling click events on the options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.searchLocation:
                getLocationFromUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Alert dialog box to get location from the user
    private void getLocationFromUser() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText inputLocation = new EditText(this);

        inputLocation.setInputType(InputType.TYPE_CLASS_TEXT);
        inputLocation.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(inputLocation);

        // Positive button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Check if internet connectivity is there
                if ( ! checkConnectivity() )
                    dataNotAvailable(); // if no connectivity notify the user
                else // Try getting the data for the given location
                    new OfficialMasterList(MainActivity.this).execute(inputLocation.getText().toString());
            }
        });

        // Cancel button
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Nothing to do user has cancelled the action
            }
        });

        builder.setMessage("Enter City, State or a Zip Code:");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildLayoutPosition(v);
        Official official = officialList.get(position);
        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("location", currentLocation.getText());
        intent.putExtra(Official.class.getName(), official);
        startActivity(intent);
    }

    // Method to check for connectivity
    private boolean checkConnectivity()  {
        Log.d(TAG, "checkConnectivity: STARTED");

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
            return false;

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if( networkInfo != null && networkInfo.isConnected() ){
            Log.d(TAG, "checkConnectivity: COMPLETED");
            return true;
        } else {
            Log.d(TAG, "checkConnectivity: COMPLETED");
            return false;
        }
    }

    // Alert dialog box saying no connectivity
    public void dataNotAvailable() {
        Log.d(TAG, "dataNotAvailable: STARTED");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Data cannot be accessed or loaded without internet connection");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Log.d(TAG, "dataNotAvailable: COMPLETED");
    }

    // Check if permissions are granted
    private boolean checkForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED)
            return false;
        else
            return true;
    }

    // Request permissions
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        getZipCode();
                    } else {
                        Toast.makeText(this, "Location permission was denied - cannot determine address", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onRequestPermissionsResult: NO PERM");
                    }
                }
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: Exiting onRequestPermissionsResult");
    }

    // find the current zip code and get location details, official list
    @SuppressLint("MissingPermission")
    private void getZipCode(){
        Log.d(TAG, "getZipCode: STARTED");
        try{
            locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

            if (locationManager != null) {
                // Checking if we can get location from NETWORK
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    List<Address> addresses = null;
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Address ad = addresses.get(0);
                    String zipCode = ad.getPostalCode();
                    if( zipCode !=null || zipCode.trim().length() > 0 )
                    new OfficialMasterList(MainActivity.this).execute(ad.getPostalCode());
                    return;
                } else{
                    dataNotAvailable();
                }
            } else{
                Log.d(TAG, "getZipCode: locationManger is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // update the location and official list
    public void populateOfficialList(Object[] results) {
        if(results != null && results.length!= 0)  {

            if(results[0] != null){
                currentLocation.setText(results[0].toString());
            } else
                currentLocation.setText(dataNotFound);

            officialList.clear();
            if( results[1] != null )
                officialList.addAll((ArrayList)results[1] );

            officialAdapter.notifyDataSetChanged();

        }  else {
            currentLocation.setText(dataNotFound);
            officialList.clear();
            officialAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
