package com.deepakyadav.knowyourgovernment;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class OfficialMasterList extends AsyncTask<String,Void,String> {

    private static final String TAG = "OfficialMasterList";
    private MainActivity mainActivity;
    String API_KEY = "AIzaSyCOZ8n6mCWV6FY8qPkrXRgpaPs8zvznT7k";
    String CIVIC_API_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key="+API_KEY;
    Object[] results = new Object[2];

    // Constructor
    public OfficialMasterList(MainActivity mainActivity) {
        this.mainActivity =mainActivity;
    }

    // onPostExecution of the method
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mainActivity.populateOfficialList(results);
    }

    // doInBackground internet download activity
    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: STARTED");

        Uri.Builder uriBuilt = Uri.parse(CIVIC_API_URL).buildUpon();
        String result = connectToAPI( Uri.parse(CIVIC_API_URL).buildUpon(), strings[0]).trim();
        Log.d(TAG, "doInBackground: result "+result);

        if( result.length() > 0)
            jsonMapper( result );
        Log.d(TAG, "doInBackground: COMPLETED");

        return null;
    }

    // Function to get the data for the given zip code
    public String connectToAPI(Uri.Builder uri, String zipCode) {
        Log.d(TAG, "connectToAPI: STARTED");

        uri = uri.appendQueryParameter("address", zipCode);
        String finalURL = uri.build().toString();
        Log.d(TAG, "connectToAPI: finalURL is: " + finalURL);
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(finalURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if(conn.getResponseCode() != HttpURLConnection.HTTP_NOT_FOUND) {
                conn.setRequestMethod("GET");
                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferReader = new BufferedReader((new InputStreamReader(inputStream)));
                String line;

                while ((line = bufferReader.readLine()) != null)
                    stringBuilder.append(line).append('\n');
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "connectToAPI: COMPLETED");

        return stringBuilder.toString();
    }

    // jsonMapper to handle the JSON and get the required information
    private void jsonMapper(String input) {

        Log.d(TAG, "jsonMapper: STARTED");

        String officeName;
        String address = "";
        String dataNotFound = "No Data Provided";
        ArrayList<Official> officialList= new ArrayList <>();

        try {
                JSONObject jsonObject = new JSONObject(input);
                JSONObject locJson = jsonObject.getJSONObject("normalizedInput");
                JSONArray officesJson = jsonObject.getJSONArray("offices");
                JSONArray officialJson = jsonObject.getJSONArray("officials");

                // location that will be displayed in the app as per the given format Chicago IL 60661
                String locObtained = "";
                if( locJson.getString("city").trim().length() != 0 )
                    locObtained += locJson.getString("city")+", ";
                if( locJson.getString("state").trim().length() != 0 )
                    locObtained += locJson.getString("state")+" ";
                if( locJson.getString("zip").trim().length() !=0 )
                    ;locObtained += locJson.getString("zip");

                for(int item = 0; item < officesJson.length(); item++) {

                    JSONObject object = (JSONObject) officesJson.get(item);
                    officeName = object.getString("name");
                    JSONArray indices = object.getJSONArray("officialIndices");

                    for(int index=0; index<indices.length(); index++) {

                        Official official = new Official();

                        // update office
                        official.setOffice(officeName);

                        JSONObject officialData = (JSONObject) officialJson.get(indices.getInt(index));

                        // update official name
                        if ( officialData.getString("name") == null ||
                                officialData.getString("name").trim().length() == 0 )
                            official.setOfficialName(dataNotFound);
                        else
                            official.setOfficialName(officialData.getString("name"));

                        // update official Address
                        if(officialData.has("address")) {
                            JSONArray addressJson = officialData.getJSONArray("address");
                            JSONObject addressObject = (JSONObject) addressJson.get(0);
                            address = officeName+"\n";
                            if (addressObject.has("line1"))
                                address +=  addressObject.getString("line1")+ '\n';
                            if (addressObject.has("line2"))
                                address +=  addressObject.getString("line2") + '\n';
                            if (addressObject.has("line3"))
                                address +=  addressObject.getString("line3")+ '\n';
                            address += addressObject.getString("city")+", ";
                            address += addressObject.getString("state")+" ";
                            address += addressObject.getString("zip");
                            official.setOfficialAddress(address);
                        }
                        else
                            official.setOfficialAddress(dataNotFound);

                        // update official party
                        if(officialData.has("party"))
                            official.setOfficialParty( officialData.getString("party"));
                        else
                            official.setOfficialParty(dataNotFound);

                        // update official phone
                        if (officialData.has("phones")) {
                            JSONArray phoneArray = officialData.getJSONArray("phones");
                            official.setOfficialPhone( phoneArray.get(0).toString() );
                        } else
                            official.setOfficialPhone(dataNotFound);

                        // update official email
                        if (officialData.has("emails")) {
                            JSONArray emailArray = officialData.getJSONArray("emails");
                            official.setOfficialEmail(emailArray.get(0).toString());
                        } else
                            official.setOfficialEmail(dataNotFound);

                        // update official photo
                        if (officialData.has("photoUrl"))
                            official.setOfficialPhotoURL(officialData.get("photoUrl").toString());

                        // update official url
                        if (officialData.has("urls")) {
                            JSONArray urlArray = officialData.getJSONArray("urls");
                            official.setOfficialWebURL(urlArray.get(0).toString());
                        } else
                            official.setOfficialWebURL(dataNotFound);

                        //update official social media
                        if (officialData.has("channels")) {
                            JSONArray socialMedia = officialData.getJSONArray("channels");

                            for (int media = 0; media < socialMedia.length(); media++) {
                                JSONObject mediaObject = (JSONObject) socialMedia.get(media);
                                String mediaType = (String) mediaObject.get("type");
                                String mediaInfo = mediaObject.get("id").toString();
                                if ( mediaType.equals("YouTube"))
                                    official.setOfficialYouTube( mediaInfo ) ;
                                else if ( mediaType.equals("Facebook"))
                                    official.setOfficialFB(mediaInfo);
                                else if ( mediaType.equals("Twitter"))
                                    official.setOfficialTwitter(mediaInfo);
                                else if ( mediaType.equals("GooglePlus"))
                                    official.setOfficialGPlus(mediaInfo);
                            }
                        }
                        officialList.add(official);
                    }
                }
                results[0] = locObtained;
                results[1] = officialList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "jsonMapper: COMPLETED");
    }
}
