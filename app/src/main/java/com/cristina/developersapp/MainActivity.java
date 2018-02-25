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

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<HashMap<String, ArrayList<String>>>,
        UsersAdapter.UsersAdapterOnClickHandler{

    private RecyclerView mRecyclerView;
    private UsersAdapter mUsersAdapter;

    private static HashMap<String, ArrayList<String>> dataHashMap;
    public static ArrayList<Bitmap> pictures = new ArrayList<>();

    public static String filename = "JSONData";

    File file;

    private static int LOADER_ID = 10;

    private static long cachetime = 1800000;

    private static long len;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

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

            String data = "";

            Log.i("Data", "retrieved from internal storage");

            try {
                InputStream inputStream = this.openFileInput(filename);

                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        stringBuilder.append(receiveString);
                    }

                    inputStream.close();
                    inputStreamReader.close();
                    bufferedReader.close();
                    data = stringBuilder.toString();

                }
            }
            catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }

            dataHashMap = JSONUtilities.
                    getUsersStringJson(this, data);

            mUsersAdapter.setUsersName(dataHashMap.get(JSONUtilities.nameTag));
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

                    FileOutputStream outputStream;;

                    try {

                        file.delete();
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(jsonDataResponse.getBytes());
                        outputStream.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dataHashMap = JSONUtilities.getUsersStringJson(
                            getApplicationContext(), jsonDataResponse);

                    ArrayList<String> profileImagesUrlStr = dataHashMap.get(JSONUtilities.profileImageTag);

                    for (int i = 0; i < profileImagesUrlStr.size(); i++) {

                        URL url = new URL(profileImagesUrlStr.get(i));
                        Bitmap image = NetworkUtilities.getBitmapFromURL(url);

                        ByteArrayOutputStream stream = new  ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte [] bytes = stream.toByteArray();
                        String result= Base64.encodeToString(bytes, Base64.DEFAULT);
                        stream.close();

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
