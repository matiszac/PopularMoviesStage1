package com.theuhd.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {
    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.activity_details, new MovieDetailsFragment()).commit();
        }

        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch(NullPointerException e) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Fragment for DetailsActivity

    public static class MovieDetailsFragment extends Fragment {
        private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

        private Movies movie;

        public MovieDetailsFragment () {

        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if(savedInstanceState == null || !savedInstanceState.containsKey("detailsMovieData")) {
                // Do nothing
            } else {
                movie = savedInstanceState.getParcelable("detailsMovieData");
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putParcelable("detailsMovieData", movie);
            super.onSaveInstanceState(outState);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.details_fragment, container, false);
            Intent intent = getActivity().getIntent();

            if(intent != null && intent.hasExtra("movie")) {

                // get parcelable movie object which can  be used to get movie data
                movie = intent.getParcelableExtra("movie");

                ImageView poster = (ImageView) rootView.findViewById(R.id.imageView);
                TextView title = (TextView) rootView.findViewById(R.id.title_textView);
                TextView release = (TextView) rootView.findViewById(R.id.release_textView);
                TextView rating = (TextView) rootView.findViewById(R.id.rating_textView);
                TextView plot = (TextView) rootView.findViewById(R.id.plot_textView);

                Picasso.with(getActivity()).load(movie.getPoster()).into(poster);
                title.setText(movie.getTitle());
                release.setText(movie.getRelease());
                rating.setText(String.format("Rating: %.2f/10", movie.getAverage()));
                plot.setText(movie.getPlot());
            }



            return rootView;
        }
    }
}
