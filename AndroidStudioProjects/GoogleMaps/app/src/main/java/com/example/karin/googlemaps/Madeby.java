package com.example.karin.googlemaps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * This class represents the app info page
 * The content of the page is specified in
 * the layout file activity_maps.xml
 */
public class Madeby extends AppCompatActivity {
    private String linkText;

    @Override
    /**
     * On creation of this activity
     * the View is set to display app info page
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_madeby);
        setLink();


    }

    /**
     * This method links text to rideindego.com
     */
    public void setLink(){

        TextView bikeLink = (TextView) findViewById(R.id.philly_bike);
        linkText = "<a href='https://www.rideindego.com/'>Indego</a>-Philly's Bike Share ";
        bikeLink.setText(Html.fromHtml(linkText));
        bikeLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public String getLinkText(){
        return this.linkText;
    }




}
