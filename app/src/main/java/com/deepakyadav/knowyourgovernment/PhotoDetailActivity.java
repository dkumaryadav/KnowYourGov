package com.deepakyadav.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity {

    ImageView photo;
    TextView location;
    private static final String TAG = "PhotoDetailActivity";
    public static final String PREFS_NAME = "PERSISTED_DATA";
    Official official;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        location = findViewById(R.id.locationDisplay);
        TextView office = findViewById(R.id.office);
        TextView name = findViewById(R.id.name);
        ImageView partyLogo = findViewById(R.id.partyLogo);
        ConstraintLayout layout = findViewById(R.id.layout);
        photo = findViewById(R.id.officialPhoto);

        Intent intent = getIntent();

        // Check if location is being passed and update it
        if(intent.hasExtra("location"))
            location.setText(intent.getStringExtra("location"));

        // Check if we have official details being passed
        if (intent.hasExtra(Official.class.getName())) {
            official = (Official) intent.getSerializableExtra(Official.class.getName());

            office.setText(official.getOffice());
            name.setText(official.getOfficialName());

            if(!official.getOfficialParty().equalsIgnoreCase("Unknown") &&
                    !official.getOfficialParty().equalsIgnoreCase("No data provided")) {

                String partyText = official.getOfficialParty();

                Log.d(TAG, "OfficialActivity: party is: "+ partyText);

                if( partyText.contains("Republican")){
                    layout.setBackgroundColor(Color.RED);
                    partyLogo.setImageResource(R.drawable.rep_logo);
                } else if ( partyText.contains("Democratic") ||
                        partyText.contains("Democrat") ){
                    layout.setBackgroundColor(Color.BLUE);
                    partyLogo.setImageResource(R.drawable.dem_logo);
                } else {
                    layout.setBackgroundColor(Color.BLACK);
                    partyLogo.setVisibility(View.INVISIBLE);
                }
            }

            // If Photo URL is present
            if (official.getOfficialPhotoURL() != null) {

                // To handle image load failures
                Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {

                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        exception.printStackTrace();
                    }
                }).build();
                picasso.setLoggingEnabled(true);
                // Load image from the URL, if error load the broken image
                picasso.load(official.getOfficialPhotoURL())
                        .fit()
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(photo);
            } else { // Image URL is not present for these officials
                photo.setImageResource(R.drawable.missing);
            }
        }
    }

    // onClick navigate to respective party website
    public void partyWebsite(View v){
        Log.d(TAG, "partyWebsite: STARTED");

        String party = official.getOfficialParty();
        String url = "";

        // If party is known Republican / Democrat*
        if (!party.equalsIgnoreCase("Unknown") && !party.equalsIgnoreCase("No data provided")) {

            if (party.contains("Republican"))
                url = "https://www.gop.com";
            else if (party.contains("Democratic") || party.contains("Democrat"))
                url = "https://democrats.org";

            Intent partyWebSite = new Intent(Intent.ACTION_VIEW);
            partyWebSite.setData(Uri.parse(url));
            startActivity(partyWebSite);
        }

        Log.d(TAG, "partyWebsite: COMPLETED");
    }

}
