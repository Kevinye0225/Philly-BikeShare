package com.example.karin.googlemaps;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geojson.GeoJsonFeature;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geojson.GeoJsonPointStyle;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * This class is the main Activity. It downloads contents of API and processes data.
 * The processed data is presented as markers on the Google Map.
 * A Google Play Services API client is created to request user location data.
 * The class contains an Asnyctask subclass that performs network operation
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng location_data;
    private final String jsonURL = "https://api.phila.gov/bike-share-stations/v1";
    private final static String mLogTag = "GeoJson";
    private GeoJsonLayer mLayer;
    private JSONObject json;

    /**
     * On creation of the main activity, this method
     * calls buildGoogleApi to build Google API client;
     * Views for the Google Map and the tool bar are set up;
     * A SupportMapFragment is created which will later be used
     * to get a GoogleMap object.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //call method buildGoogleApi
        buildGoogleApi();
        //set contentview to google map and add toolbar
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * This method builds a Google API client to connect to
     * Google Play services
     */
    public void buildGoogleApi(){
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    /**
     * This callback is triggered when the map is ready to be used.
     * It  assigns the GoogleMap object to the class instance variable mMap
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    /**
     * This callback is triggered once Google Play services is connected;
     * The method requests for user location via LocationServices
     * and uses the location data to set the default camera view;
     * It also calls the startdemo method to begin accessing bike network API
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        //google api client connected; get user location data
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        //get latlng data from mLastLocation variable
        location_data = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        //set default camera view; move camera to user location
        //set zoom level to 14
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location_data, 14));
        //enable user location to appear on map
        mMap.setMyLocationEnabled(true);
        //call startdemo() to start another thread to perform network operation
        startdemo();
    }

    /**
     * This method connects to Google Play Services
     * onStart is part of the main Activity cycle
     */
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     * When the main activity is stopped Google Play Services
     * is disconnected
     * onStop is part of the main Activity cycle
     */
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu
     * @return boolean
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * This methods starts the execution of Asynctask
     * and passes in the bike network API URL
     */
    public void startdemo() {
        DownloadGeoJsonFile downloadGeoJsonFile = new DownloadGeoJsonFile();
        //start another thread to access bike network api and
        //download the GeoJSON file
        downloadGeoJsonFile.execute(jsonURL);
    }

    /**
     * This method sets the feature of each marker;
     * each marker's info window is set to display key info about
     * the bike station it represents.
     */
    public void addInfoToMarkers() {
        for (GeoJsonFeature feature : mLayer.getFeatures()) {
            if (feature.hasProperty("bikesAvailable") && feature.hasProperty("docksAvailable")) {
                //set marker color
                BitmapDescriptor pointIcon = BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED);
                GeoJsonPointStyle pointStyle = new GeoJsonPointStyle();
                //set marker's title
                pointStyle.setTitle(feature.getProperty("addressStreet"));
                pointStyle.setIcon(pointIcon);
                //set parker's snippet to include 'Bikes Available' and 'Docks Available'
                pointStyle.setSnippet("Bikes Available: " + feature.getProperty("bikesAvailable") + " |Docks Available: " + feature.getProperty("docksAvailable"));
                feature.setPointStyle(pointStyle);
            }
        }
    }

    /**
     * This is a subclass that inherits AsyncTask
     * A new thread is created to run in the background
     * to access the bike network URL.
     * The processed data will be displayed as markers on the Google Map
     */
    private class DownloadGeoJsonFile extends AsyncTask<String, Void, JSONObject> {

        @Override
        /**
         * This method is one of the crucial steps of AsyncTask.
         * It performs background computation that takes a long time.
         * This method takes in the URL as arguments and downloads contents of
         * the bike network API. The return is a JSON object which will be passed to
         * another step OnPostExecute as a result.
         */
        protected JSONObject doInBackground(String... params) {
            try {
                //pass argument(url) to inputstream
                InputStream stream = new URL(params[0]).openStream();
                String line;
                StringBuilder result = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                //write contents of api to String
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                // Close the BufferedReader and the InputStream
                reader.close();
                stream.close();
                //Convert result to a JSONObject
                json = new JSONObject(result.toString());
                return json;
            } catch (IOException e) {
                Log.e(mLogTag, "GeoJSON file could not be read");
            } catch (JSONException e) {
                Log.e(mLogTag, "GeoJSON file could not be converted to a JSONObject");
            }
            return null;
        }

        @Override
        /**
         * The result of the background computation is passed
         * to this step as a parameter. It runs on the main thread.
         * This step renders the JSON object and adds a GeoJSON layer
         * to the map.
         */
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                mLayer = new GeoJsonLayer(mMap, jsonObject);
                //update info window of each marker
                addInfoToMarkers();
                //add GeoJSON layer on top of map
                mLayer.addLayerToMap();
            }

        }
    }

    /**
     * This method controls the onclick button which
     * directs to another activity: app info page
     * @param item
     */
    public void makeBy(MenuItem item){
        Intent intent = new Intent(this,Madeby.class);
        startActivity(intent);
    }

    /**
     * This method handles the 'REFRESH' button
     * which on clicked will reconnect to Google Play Services
     * @param item
     */
    public void Refresh(MenuItem item){
        mGoogleApiClient.disconnect();
        mGoogleApiClient.connect();

    }

    /**
     * This method is created to facilitate testing
     * @return
     */
    public GoogleMap getmMap(){
        return mMap;
    }
    /**
     * This method is created to facilitate testing
     * @return
     */
    public GoogleApiClient getmGoogleApiClient(){
        return this.mGoogleApiClient;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
