package com.deepakyadav.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    // Navigate to google civic information page
    public void navToBrowser(View v){
        Uri uriURL = Uri.parse("https://developers.google.com/civic-information");
        Intent intent = new Intent(Intent.ACTION_VIEW, uriURL);
        startActivity(intent);
    }

}
