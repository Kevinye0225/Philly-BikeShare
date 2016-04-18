package com.example.karin.googlemaps;

import android.app.Fragment;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;
import android.widget.Toolbar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;

import java.util.Map;

/**
 * Created by kevin on 12/14/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 18)
/**
 * This test is created to test Mapsactivity class
 */
public class NewTest extends ActivityUnitTestCase<MapsActivity>{

    /*
     * super class constructor
     */
    public NewTest() {
        super(MapsActivity.class);
    }
    /*
     * activity that is being tested
     */
    private MapsActivity activity;
    private Fragment map;


    /*
     *Set up activity to be tested
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();

        activity = Robolectric.buildActivity(MapsActivity.class).get();
//        activity = new MapsActivity();
//        activity.onCreate(null);
        map = activity.getFragmentManager().findFragmentById(R.id.map);
//        toolbar = (Toolbar) activity.findViewById(R.id.toolbar);

    }

    /*
     *see if activity has been set up
     */
    @Test
    public void testActivity() {
        assertNotNull(activity);
    }

    /*
     *test mMap is NULL before calling set up method
     */
    @Test
    public void testGoogleMap(){
        assertNull(activity.getmMap());
    }


    /*
     *test if build google api works properly
     */
    @Test
    public void testBuildGoogleApi(){
        activity.buildGoogleApi();
        assertNotNull(activity.getmGoogleApiClient());

    }


    @After
    public void tearDown() throws Exception {

    }


}
