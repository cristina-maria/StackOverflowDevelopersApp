package com.cristina.developersapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static android.R.attr.bitmap;
import static com.cristina.developersapp.NetworkUtilities.getResponseFromHttpUrl;

/*
    In this application, the data retrieved in JSON format from the API is formatted into
        a hashMap which maps from keys (userName, location, bdges... constants in JSONUtilities class)
        to arraylists of strings containing the required info about the users
 */

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<HashMap<String, ArrayList<String>>>,
        UsersAdapter.UsersAdapterOnClickHandler{

    private RecyclerView mRecyclerView;
    private UsersAdapter mUsersAdapter;

    private HashMap<String, ArrayList<String>> dataHashMap;

    public static String filename = "JSONData";

    File file;

    private static int LOADER_ID = 10;

    private static long cachetime = 1800000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mUsersAdapter = new UsersAdapter(this, this);

        mRecyclerView.setAdapter(mUsersAdapter);

        int loaderId = LOADER_ID;

        file = new File(this.getFilesDir(), filename);

        long time = System.currentTimeMillis();
        long lastModified = file.lastModified();

        if (file.exists() && lastModified > time - cachetime ) {

            Log.i("Data", "retrieved from internal storage");

            String data = null;
            try {

                /*
                    read from internal storage the data kept in JSON format
                 */
                data = NetworkUtilities.readFromInternalStorage(this, filename);

            } catch (IOException e) {
                e.printStackTrace();
            }

            /*
                transform the JSON format into a hashMap
             */
            dataHashMap = JSONUtilities.
                    getUsersStringJson(this, data);

            mUsersAdapter.setUsersName(dataHashMap.get(JSONUtilities.nameTag));

            ArrayList<String> profilePictures = dataHashMap.get(JSONUtilities.profileImageTag);
            for (int i = 0; i < dataHashMap.get(JSONUtilities.nameTag).size(); i++) {

                try {
                    /*
                        retrieve the Base64 encoded strings from the internal storage
                        for the profile picturea
                     */
                    data = NetworkUtilities.readFromInternalStorage(this,
                            "profile_picture" + String.valueOf(i));
                    profilePictures.set(i, data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mUsersAdapter.setUsersProfilePicture(dataHashMap.get(JSONUtilities.profileImageTag));

        } else {

            Log.i("Data", "retrieved from internet");

            LoaderManager.LoaderCallbacks<HashMap<String, ArrayList<String>>> callback = MainActivity.this;

            Bundle bundleForLoader = null;

            getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
        }

    }


    public Loader<HashMap<String, ArrayList<String>>> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<HashMap<String, ArrayList<String>>>(this) {

            HashMap<String, ArrayList<String>> result = null;

            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            /*
                Download data as well as profile images
            */
            @Override
            public HashMap<String, ArrayList<String>> loadInBackground() {

                HashMap<String, ArrayList<String>> dataHashMap;

                URL requestUrl = NetworkUtilities.buildUrl();

                try {
                    String jsonDataResponse = NetworkUtilities
                            .getResponseFromHttpUrl(requestUrl);

                    NetworkUtilities.writeToInternalStorage(
                            getApplicationContext(), jsonDataResponse, filename);

                    dataHashMap = JSONUtilities.getUsersStringJson(
                            getApplicationContext(), jsonDataResponse);

                    ArrayList<String> profileImagesUrlStr = dataHashMap.get(JSONUtilities.profileImageTag);
                    ByteArrayOutputStream stream;

                    for (int i = 0; i < profileImagesUrlStr.size(); i++) {

                        /*
                            obtain the profile picture in Bitmap format from the link provided
                            by the API
                         */
                        URL url = new URL(profileImagesUrlStr.get(i));
                        Bitmap image = NetworkUtilities.getBitmapFromURL(url);

                        /*
                            compress the Bitmap object into a Base64 string in order to be
                            written in the internal storage
                         */
                        stream = new  ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte [] bytes = stream.toByteArray();
                        String result = Base64.encodeToString(bytes, Base64.DEFAULT);
                        stream.close();

                        NetworkUtilities.writeToInternalStorage(
                                getApplicationContext(), result, "profile_picture" + String.valueOf(i));

                        profileImagesUrlStr.set(i, result);
                    }

                    return dataHashMap;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }


            public void deliverResult(HashMap<String, ArrayList<String>> resultHashMap) {

                result = resultHashMap;
                super.deliverResult(resultHashMap);

            }
        };
    }

    /*
       Use the obtained data to populate the adapter
     */
    @Override
    public void onLoadFinished(Loader<HashMap<String, ArrayList<String>>> loader, HashMap<String, ArrayList<String>> data) {

        if (data != null) {
            dataHashMap = data;
            mUsersAdapter.setUsersName(data.get(JSONUtilities.nameTag));
            mUsersAdapter.setUsersProfilePicture(data.get(JSONUtilities.profileImageTag));
        } else {

            // show error
            Log.i("TAG1", "eroare");
        }
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, ArrayList<String>>> loader) {

    }

    /*
        called when the user presses one of the elements in the list
        all the details about the user are passed to the activity
     */
    @Override
    public void onClick(int position) {
        Context context = this;
        Class destinationClass = DetailUser.class;
        Intent newIntent = new Intent(context, destinationClass);

        newIntent.putExtra(JSONUtilities.nameTag, dataHashMap.get(JSONUtilities.nameTag).get(position));
        newIntent.putExtra(JSONUtilities.profileImageTag, dataHashMap.get(JSONUtilities.profileImageTag).get(position));
        newIntent.putExtra(JSONUtilities.locationTag, dataHashMap.get(JSONUtilities.locationTag).get(position));
        newIntent.putExtra(JSONUtilities.goldBadgeTag, dataHashMap.get(JSONUtilities.goldBadgeTag).get(position));
        newIntent.putExtra(JSONUtilities.silverBadgeTag, dataHashMap.get(JSONUtilities.silverBadgeTag).get(position));
        newIntent.putExtra(JSONUtilities.badgesTag, dataHashMap.get(JSONUtilities.bronzeBadgeTag).get(position));

        startActivity(newIntent);
    }
}
