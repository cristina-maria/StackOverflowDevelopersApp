package com.cristina.developersapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Cristina on 2/23/2018.
 */

public class NetworkUtilities {

    private static Integer nrOfBadges = 3;

    private static final String BASE_URL = "https://api.stackexchange.com/2.2/users?";
    private static final String ORDER = "order";
    private static final String SORT_CRITERIA = "sort";
    private static final String SITE = "site";
    private static final String PAGE_SIZE = "pagesize";

    private static String order_value = "desc";
    private static String sort_criteria_value = "reputation";
    private static String site_value = "stackoverflow";
    private static String page_size_value = "10";

    /*
        Build the URL using BASE_URL and the rest of the parameters needed
     */
    public static URL buildUrl() {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PAGE_SIZE, page_size_value)
                .appendQueryParameter(ORDER, order_value)
                .appendQueryParameter(SORT_CRITERIA, sort_criteria_value)
                .appendQueryParameter(SITE, site_value)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v("Tag", "Built URI " + url);

        return url;
    }

    /*
        Fetch the data
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream in = urlConnection.getInputStream();

        try {

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            in.close();
            urlConnection.disconnect();
        }
    }

    public static Bitmap getBitmapFromURL(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();;
        try {

            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            input.close();
            return myBitmap;
        } finally {
            connection.disconnect();
        }
    }



}
