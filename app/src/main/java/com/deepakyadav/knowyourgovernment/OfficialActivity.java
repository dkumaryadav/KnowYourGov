package com.deepakyadav.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private static final String TAG = "OfficialActivity";
    String dataNotFound = "No Data Provided";
    Official official;
    ImageView photo;
    ImageView partyLogo;
    TextView location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OfficialActivity: STARTED");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        location = findViewById(R.id.locationDisplay);
        TextView office = findViewById(R.id.office);
        TextView name = findViewById(R.id.name);
        TextView party = findViewById(R.id.party);
        partyLogo = findViewById(R.id.partyLogo);
        TextView addressLabel = findViewById(R.id.address);
        TextView address = findViewById(R.id.addressText);
        TextView phoneLabel = findViewById(R.id.phone);
        TextView phone = findViewById(R.id.phoneText);
        TextView emailLabel = findViewById(R.id.email);
        TextView email = findViewById(R.id.emailText);
        TextView webLabel = findViewById(R.id.website);
        TextView webURL = findViewById(R.id.webURL);
        ImageView facebook = findViewById(R.id.facebook);
        ImageView twitter = findViewById(R.id.twitter);
        ImageView googlePlus = findViewById(R.id.googlePlus);
        ImageView youtube = findViewById(R.id.youtube);
        ConstraintLayout layout = findViewById(R.id.layout);
        photo = findViewById(R.id.officialPhoto);
        Intent intent = getIntent();

        // Check if location is being passed and update it
        if (intent.hasExtra("location"))
            location.setText(intent.getStringExtra("location"));

        // Check if we have official details being passed
        if (intent.hasExtra(Official.class.getName())) {

            official = (Official) intent.getSerializableExtra(Official.class.getName());

            office.setText(official.getOffice());
            name.setText(official.getOfficialName());

            if (!official.getOfficialParty().equalsIgnoreCase("Unknown") &&
                    !official.getOfficialParty().equalsIgnoreCase("No data provided")) {
                String partyText = official.getOfficialParty();
                party.setText("(" + partyText + ")");

                Log.d(TAG, "OfficialActivity: party is: " + partyText);

                if (partyText.contains("Republican")) {
                    layout.setBackgroundColor(Color.RED);
                    partyLogo.setImageResource(R.drawable.rep_logo);
                } else if (partyText.contains("Democratic") ||
                        partyText.contains("Democrat")) {
                    layout.setBackgroundColor(Color.BLUE);
                    partyLogo.setImageResource(R.drawable.dem_logo);
                } else {
                    layout.setBackgroundColor(Color.BLACK);
                    partyLogo.setVisibility(View.INVISIBLE);
                }
            }

            populateImage(official.getOfficialPhotoURL());

            // Update address if provided else don't display it
            if( official.getOfficialAddress().equals( dataNotFound) ){
                addressLabel.setVisibility(View.INVISIBLE);
                address.setVisibility(View.INVISIBLE);
            } else{
                addressLabel.setVisibility(View.VISIBLE);
                address.setVisibility(View.VISIBLE);
                address.setText(official.getOfficialAddress());
            }

            // Update phone number if provided else don't display it
            if( official.getOfficialPhone().equals( dataNotFound) ){
                phoneLabel.setVisibility(View.INVISIBLE);
                phone.setVisibility(View.INVISIBLE);
            } else{
                phoneLabel.setVisibility(View.VISIBLE);
                phone.setVisibility(View.VISIBLE);
                phone.setText(official.getOfficialPhone());
            }

            // Update email  if provided else don't display it
            if( official.getOfficialEmail().equals( dataNotFound) ){
                emailLabel.setVisibility(View.INVISIBLE);
                email.setVisibility(View.INVISIBLE);
            } else{
                emailLabel.setVisibility(View.VISIBLE);
                email.setVisibility(View.VISIBLE);
                email.setText(official.getOfficialEmail());
            }

            // Update website if provided else don't display it
            if( official.getOfficialWebURL().equals( dataNotFound) ){
                webLabel.setVisibility(View.INVISIBLE);
                webURL.setVisibility(View.INVISIBLE);
            } else{
                webLabel.setVisibility(View.VISIBLE);
                webURL.setVisibility(View.VISIBLE);
                webURL.setText(official.getOfficialWebURL());
            }

            // Update social media if provided else don't display it
            if (official.getOfficialFB() != null &&
                    !official.getOfficialFB().equals(""))
                facebook.setVisibility(View.VISIBLE);
            else
                facebook.setVisibility(View.INVISIBLE);

            if (official.getOfficialGPlus() != null && !official.getOfficialGPlus().equals(""))
                googlePlus.setVisibility(View.VISIBLE);
            else
                googlePlus.setVisibility(View.INVISIBLE);

            if (official.getOfficialYouTube() != null && !official.getOfficialYouTube().equals(""))
                youtube.setVisibility(View.VISIBLE);
            else
                youtube.setVisibility(View.INVISIBLE);

            if (official.getOfficialTwitter() != null && !official.getOfficialTwitter().equals(""))
                twitter.setVisibility(View.VISIBLE);
            else
                twitter.setVisibility(View.INVISIBLE);

        }

        // Linkify the address phone email and website
        Linkify.addLinks(webURL, Linkify.WEB_URLS);
        Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
        Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
        Linkify.addLinks(address, Linkify.MAP_ADDRESSES);

        // Set the link colors to white
        webURL.setLinkTextColor(Color.WHITE);
        phone.setLinkTextColor(Color.WHITE);
        address.setLinkTextColor(Color.WHITE);
        email.setLinkTextColor(Color.WHITE);

        Log.d(TAG, "OfficialActivity: COMPLETED");
    }

    // Function to populate the image from the photo URL
    private void populateImage(final String officialPhotoURL) {

        // If Photo URL is present
        if (officialPhotoURL != null) {

            // To handle image load failures
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {

                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    exception.printStackTrace();
                }
            }).build();
            picasso.setLoggingEnabled(true);
            // Load image from the URL, if error load the broken image
            picasso.load(officialPhotoURL)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(photo);
        } else { // Image URL is not present for these officials
            photo.setImageResource(R.drawable.missing);
        }
    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + official.getOfficialFB();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + official.getOfficialFB();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = official.getOfficialTwitter();
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void googlePlusClicked(View v) {
        String name = official.getOfficialGPlus();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus", "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + name)));
        }
    }

    public void youTubeClicked(View v) {
        String name = official.getOfficialYouTube();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    // Function to load the complete photo
    public void photoActivity(View v) {

        // Check if the photo is available then only load it
        if ( official.getOfficialPhotoURL() == null ){
            Toast.makeText(getApplicationContext(), "Official image not available. Photo activity will not be loaded", Toast.LENGTH_LONG).show();
        } else{
            Intent intent = new Intent(this, PhotoDetailActivity.class);
            intent.putExtra("location", location.getText());
            intent.putExtra(Official.class.getName(), official);
            startActivity(intent);
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
