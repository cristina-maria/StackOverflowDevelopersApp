package com.cristina.developersapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Cristina on 2/23/2018.
 */

public class JSONUtilities {

    private static int developers_number = 10;

    public static String nameTag = "display_name";
    public static String locationTag = "location";
    public static String profileImageTag = "profile_image";
    public static String badgesTag = "badge_counts";
    public static String bronzeBadgeTag = "bronze";
    public static String silverBadgeTag = "silver";
    public static String goldBadgeTag = "gold";

    /*
        Parse the data obtained in JSON format. Return the info about names, location,
        profile pictures and badges in a HashMap mapping from keys to arraylists of
        the data obtained for each user.
     */
    public static HashMap<String, ArrayList<String>> getUsersStringJson(Context context, String usersJsonStr) {

        HashMap<String, ArrayList<String>> result = new HashMap<>();

        ArrayList<String> usersNames = new ArrayList<String>();
        ArrayList<String> usersLocations = new ArrayList<String>();
        ArrayList<String> usersProfileImages = new ArrayList<String>();
        ArrayList<String> usersBronzeBadges = new ArrayList<String>();
        ArrayList<String> usersSilverBadges = new ArrayList<String>();
        ArrayList<String> usersGoldBadges = new ArrayList<String>();

        try {
            JSONObject usersJson = new JSONObject(usersJsonStr);

            JSONArray usersArray = usersJson.getJSONArray("items");

            for (int i = 0; i < developers_number; i++) {

                JSONObject user = usersArray.getJSONObject(i);

                String userName = user.getString(nameTag);
                String location = user.getString(locationTag);
                String profileImage = user.getString(profileImageTag);

                usersNames.add(userName);
                usersLocations.add(location);
                usersProfileImages.add(profileImage);

                JSONObject badges = user.getJSONObject(badgesTag);
                int bronzeBadges = badges.getInt(bronzeBadgeTag);
                int silverBadges = badges.getInt(silverBadgeTag);
                int goldBadges = badges.getInt(goldBadgeTag);

                usersBronzeBadges.add(String.valueOf(bronzeBadges));
                usersSilverBadges.add(String.valueOf(silverBadges));
                usersGoldBadges.add(String.valueOf(goldBadges));

            }

            result.put(nameTag, usersNames);
            result.put(locationTag, usersLocations);
            result.put(profileImageTag, usersProfileImages);
            result.put(bronzeBadgeTag, usersBronzeBadges);
            result.put(silverBadgeTag, usersSilverBadges);
            result.put(goldBadgeTag, usersGoldBadges);

            return result;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
