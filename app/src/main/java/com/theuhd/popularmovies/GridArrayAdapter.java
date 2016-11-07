package com.theuhd.popularmovies;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;



/**
 * Created by Zachary on 11/5/2016.
 * Custom ArrayAdapter of Movies object
 */

public class GridArrayAdapter extends ArrayAdapter<Movies> {

    private static final String LOG_TAG = GridArrayAdapter.class.getSimpleName();

    public GridArrayAdapter(Activity context, List<Movies> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movies movies = getItem(position);
        View rootView = convertView;

        // if rootView is not a recycled view create a new view
        if(rootView == null) {
            rootView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
        }

        ImageView imageView = (ImageView) rootView.findViewById(R.id.grid_imageview);

        Picasso.with(getContext()).load(movies.getPoster()).into(imageView);

        return rootView;
    }
}
