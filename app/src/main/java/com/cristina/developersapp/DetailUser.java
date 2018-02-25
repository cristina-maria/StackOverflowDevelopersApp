package com.cristina.developersapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailUser extends AppCompatActivity {

    private TextView userName;
    private TextView userLocation;
    private ImageView userProfilePicture;
    private TextView bronzeBadges;
    private TextView silverBadges;
    private TextView goldBadges;

    private static String userNameString;
    private static String profilePictureString;
    private static String locationString;
    private static String goldBadgesString;
    private static String silverBadgesString;
    private static String bronzeBadgesString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);

        userName = (TextView) findViewById(R.id.userName);
        userLocation = (TextView) findViewById(R.id.userLocation);
        userProfilePicture = (ImageView) findViewById(R.id.imageView);
        bronzeBadges = (TextView) findViewById(R.id.bronzeBadgesNumber);
        silverBadges = (TextView) findViewById(R.id.silverBadgesNumber);
        goldBadges = (TextView) findViewById(R.id.goldBadgesNumber);

        Intent parentIntent = getIntent();

        if (parentIntent != null) {
            if (parentIntent.hasExtra(JSONUtilities.nameTag)) {
                userNameString = parentIntent.getStringExtra(JSONUtilities.nameTag);
                userName.setText(userNameString);
            }

            if (parentIntent.hasExtra(JSONUtilities.profileImageTag)) {
                profilePictureString = parentIntent.getStringExtra(JSONUtilities.profileImageTag);

                byte [] encodeByte= Base64.decode(profilePictureString,Base64.DEFAULT);
                Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                userProfilePicture.setImageBitmap(bitmap);
            }

            if (parentIntent.hasExtra(JSONUtilities.locationTag)) {
                locationString = parentIntent.getStringExtra(JSONUtilities.locationTag);
                userLocation.setText(locationString);
            }

            if (parentIntent.hasExtra(JSONUtilities.bronzeBadgeTag)) {
                bronzeBadgesString = parentIntent.getStringExtra(JSONUtilities.bronzeBadgeTag);
                bronzeBadges.setText(bronzeBadgesString);
            }

            if (parentIntent.hasExtra(JSONUtilities.silverBadgeTag)) {
                silverBadgesString = parentIntent.getStringExtra(JSONUtilities.silverBadgeTag);
                silverBadges.setText(silverBadgesString);
            }

            if (parentIntent.hasExtra(JSONUtilities.goldBadgeTag)) {
                goldBadgesString = parentIntent.getStringExtra(JSONUtilities.goldBadgeTag);
                goldBadges.setText(goldBadgesString);
            }
        }
    }
}
