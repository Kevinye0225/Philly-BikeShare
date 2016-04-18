package com.example.karin.googlemaps;

import android.test.ActivityUnitTestCase;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.Button;

import junit.framework.TestCase;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by kevin on 12/14/15.
 */
// Robolectric set up
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, emulateSdk = 18)
public class MadebyTest extends ActivityUnitTestCase<Madeby> {

    /*
     *Super class constructor
     */
    public MadebyTest() {
        super(Madeby.class);
    }

    /*
     * activity and views that will be tested
     */
    private Madeby activity;
    private Button button;
    private View team;
    private View indego;
    private View credit;
    private View api;


    /*
     *Set up activity to be tested
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();

        activity = Robolectric.setupActivity(Madeby.class);


        team = activity.findViewById(R.id.team);
        indego = activity.findViewById(R.id.philly_bike);
        credit = activity.findViewById(R.id.credit);
        api = activity.findViewById(R.id.api);
        button = (Button) activity.findViewById(R.id.home);

    }

    /*
     *Test if activity is set up
     */
    @Test
    public void testActivity(){
        assertNotNull(activity);
    }

    /*
     *Test if set up is correct
     */
    @Test
    public void testSetupIsCorrect() {

        assertTrue(true);
    }

    /*
     *This method will test whether views will appear on screen
     */
    @Test
    public void testViewsAppearOnScreen(){
        View decorView = activity.getWindow().getDecorView();
        //  Verify that the button and textview are on screen
        ViewAsserts.assertOnScreen(decorView, button);
        ViewAsserts.assertOnScreen(decorView, team);
        ViewAsserts.assertOnScreen(decorView, credit);
        ViewAsserts.assertOnScreen(decorView, api);


    }


    /*
     *Test setLink method
     * linktext should have hyperlink value
     */
    @Test
    public void testSetLink() throws Exception{
        assertNotNull(activity.getLinkText());
    }

//    @Test
//    public void testGoBackButton() throws Exception {
//        // register next activity that need to be monitored.
//
//        button.performClick();
//
//    }
}
