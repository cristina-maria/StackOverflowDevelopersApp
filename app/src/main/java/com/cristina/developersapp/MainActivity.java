package com.cristina.developersapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.cristina.developersapp.NetworkUtilities.getResponseFromHttpUrl;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private UsersAdapter mUsersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mUsersAdapter = new UsersAdapter();

        mRecyclerView.setAdapter(mUsersAdapter);

        new FetchDataTask().execute();
    }

    public class FetchDataTask extends AsyncTask<String, Void, HashMap<String, ArrayList<String>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /*
            Download data as well as profile images
         */
        @Override
        protected HashMap<String, ArrayList<String>> doInBackground(String... Params) {

            HashMap<String, ArrayList<String>> dataHashMap;

            URL requestUrl = NetworkUtilities.buildUrl();

            try {
                String jsonDataResponse = NetworkUtilities
                        .getResponseFromHttpUrl(requestUrl);

                dataHashMap = JSONUtilities.getUsersStringJson(
                        getApplicationContext(), jsonDataResponse);

                ArrayList<String> profileImagesUrlStr = dataHashMap.get("profile");

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

        /*
            Use the obtained data to populate the adapter
         */
        @Override
        protected void onPostExecute(HashMap<String, ArrayList<String>> resultHashMap) {

            if (resultHashMap != null) {
                mUsersAdapter.setUsersName(resultHashMap.get(JSONUtilities.nameTag));
                mUsersAdapter.setUsersProfilePicture(resultHashMap.get(JSONUtilities.profileImageTag));
            }

        }
    }
}
