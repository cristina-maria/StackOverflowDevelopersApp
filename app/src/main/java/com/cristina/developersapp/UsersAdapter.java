package com.cristina.developersapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Cristina on 2/23/2018.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.AdapterViewHolder> {

    private ArrayList<String> usersName;
    private ArrayList<String> usersProfilePicture;

    private final UsersAdapterOnClickHandler onClickHandler;

    private Context context;

    public interface UsersAdapterOnClickHandler {
        void onClick(int position);
    }

    public UsersAdapter(UsersAdapterOnClickHandler onClickHandler, Context context) {

        this.onClickHandler = onClickHandler;
        this.context = context;
    }
    /*
        Used when a new View is created - inflate the layout
     */
    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.user;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);
        return new AdapterViewHolder(view);
    }

    /*
        Used to bind the components of the view with the corresponding data from the arraylists
        with info about the user
     */
    @Override
    public void onBindViewHolder(AdapterViewHolder holder, int position) {

        String userName = usersName.get(position);
        String pictureString = usersProfilePicture.get(position);
        holder.mUserNameTextView.setText(userName);

        byte [] encodeByte = Base64.decode(pictureString,Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        holder.mProfileImageTextView.setImageBitmap(bitmap);

    }

    @Override
    public int getItemCount() {

        if (usersName == null) return 0;
        return usersName.size();
    }

    public class AdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mUserNameTextView;
        public ImageView mProfileImageTextView;

            public AdapterViewHolder(View view) {
                super(view);
                mUserNameTextView = (TextView) view.findViewById(R.id.nameTextView);
                mProfileImageTextView = (ImageView) view.findViewById(R.id.profileImageView);
                view.setOnClickListener(this);
            }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            onClickHandler.onClick(position);
        }
    }

    /*
        Update the contents of the arraylists which hold info about the name and
        profile pictures of the users
     */
    public void setUsersName(ArrayList<String> tuserName) {

        usersName = tuserName;
        notifyDataSetChanged();
    }

    public void setUsersProfilePicture(ArrayList<String> tusersProfilePicture) {

        usersProfilePicture = tusersProfilePicture;
        notifyDataSetChanged();
    }

}
