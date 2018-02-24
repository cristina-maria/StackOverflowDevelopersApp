package com.cristina.developersapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.cristina.developersapp.NetworkUtilities.getResponseFromHttpUrl;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<HashMap<String, ArrayList<String>>>,
        UsersAdapter.UsersAdapterOnClickHandler{

    private RecyclerView mRecyclerView;
    private UsersAdapter mUsersAdapter;

    private static int LOADER_ID = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mUsersAdapter = new UsersAdapter(this);

        mRecyclerView.setAdapter(mUsersAdapter);

        int loaderId = LOADER_ID;

        LoaderManager.LoaderCallbacks<HashMap<String, ArrayList<String>>> callback = MainActivity.this;

        Bundle bundleForLoader = null;

        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
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

                    dataHashMap = JSONUtilities.getUsersStringJson(
                            getApplicationContext(), jsonDataResponse);

                    ArrayList<String> profileImagesUrlStr = dataHashMap.get(JSONUtilities.profileImageTag);

                    for (int i = 0; i < profileImagesUrlStr.size(); i++) {

                        URL url = new URL(profileImagesUrlStr.get(i));
                        String image = NetworkUtilities.getResponseFromHttpUrl(url);

                        profileImagesUrlStr.set(i, image);
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
    public void onClick() {
        Context context = this;
        Class destinationClass = DetailUser.class;
        Intent newIntent = new Intent(context, destinationClass);
        startActivity(newIntent);
    }
}
