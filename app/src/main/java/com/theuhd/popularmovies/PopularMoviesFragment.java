package com.theuhd.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PopularMoviesFragment extends Fragment {

    private final String LOG_TAG = PopularMoviesFragment.class.getSimpleName();

    private GridArrayAdapter mGridArrayAdapter;

    private ArrayList<Movies> moviedata;

    public PopularMoviesFragment () {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("moviedata")) {
            moviedata = new ArrayList<>();
        } else {
            moviedata = savedInstanceState.getParcelableArrayList("moviedata");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("moviedata", moviedata);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridArrayAdapter = new GridArrayAdapter(getActivity(), moviedata);


        GridView imageGridView = (GridView) rootView.findViewById(R.id.image_gridview);
        imageGridView.setAdapter(mGridArrayAdapter);

        imageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get the actual Movies object from the adapter based on poster item clicked
                Movies movie = mGridArrayAdapter.getItem(position);

                // Send parcelable movie object in intent to details activity
                try {
                    startActivity(new Intent(getActivity(), DetailsActivity.class).putExtra("movie", movie));
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }


    public void updateMovies() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortMode = pref.getString(getString(R.string.pref_general_sort_key), getString(R.string.pref_sort_popular));
        // start fetch movies task with either popular / top_rated as defined in general preferences
        new FetchMoviesTask().execute(sortMode);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movies>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private final String SITE_SCHEME = "https";
        private final String SITE_AUTHORITY = "api.themoviedb.org";
        private final String AUTH_VERSION = "3";
        private final String API_KEY_PARAM = "api_key";
        private final String LANG_PARAM = "language";

        @Override
        protected ArrayList<Movies> doInBackground(String... params) {

            if(params.length == 0) {
                return null;
            }

            // @param query : defines what is being queried, popular / top_rated
            String query = params[0];

            Uri.Builder builder = new Uri.Builder();
            builder.scheme(SITE_SCHEME)
                    .authority(SITE_AUTHORITY)
                    .appendPath(AUTH_VERSION)
                    .appendPath("movie")
                    .appendPath(query)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                    .appendQueryParameter(LANG_PARAM, "en-US");
            String queryUrl = builder.build().toString();

            Log.v(LOG_TAG, "@param queryUrl: " + queryUrl);

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStr = null;

            try {
                URL url = new URL(queryUrl);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                if(buffer.length() == 0) {
                    return null;
                }

                jsonStr = buffer.toString();

                Log.v(LOG_TAG, "@param jsonStr: " + jsonStr);

                try {
                    return getMovieData(jsonStr);
                } catch (JSONException e) {
                    return null;
                }

            } catch (IOException e) {
                return null;
            }
        }

        // Array list of movie info objects split into two separate arraylists to
        // be passed to updateUrls method in custom gridViewAdapter
        @Override
        protected void onPostExecute(ArrayList<Movies> movies) {
            if(movies != null) {
                moviedata = movies;
                mGridArrayAdapter.clear();
                // Using this method for API 10 support
                for(Movies movie : movies) {
                    mGridArrayAdapter.add(movie);
                }
            }
        }
    }

    // gets json string from asynctask and returns an arraylist of MovieInfo objects
    private ArrayList<Movies> getMovieData(String jsonStr) throws JSONException {

        final String TMDB_RESULTS = "results";

        final String TMDB_ID = "id";
        final String TMDB_TITLE = "title";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_POSTER_PATH = "poster_path";


        JSONObject json = new JSONObject(jsonStr);
        JSONArray movies = json.getJSONArray(TMDB_RESULTS);

        ArrayList<Movies> result = new ArrayList<>();

        for(int i = 0; i < movies.length(); i++) {
            JSONObject movie = movies.getJSONObject(i);


            int id = movie.getInt(TMDB_ID);
            String title = movie.getString(TMDB_TITLE);
            String release = movie.getString(TMDB_RELEASE_DATE);
            String plot = movie.getString(TMDB_OVERVIEW);
            double average = movie.getDouble(TMDB_VOTE_AVERAGE);
            String poster = buildPosterUrl(movie.getString(TMDB_POSTER_PATH));

            result.add(new Movies(id, title, release, plot, average, poster));
        }

        return result;
    }

    // Method to construct a proper url to image resource for poster
    private final String SITE_SCHEME = "http";
    private final String SITE_AUTHORITY = "image.tmdb.org";
    private final String IMAGE_RES = "w500";

    public String buildPosterUrl(String path) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SITE_SCHEME)
                .authority(SITE_AUTHORITY)
                .appendPath("t")
                .appendPath("p")
                .appendPath(IMAGE_RES)
                .appendPath(path.substring(1));
        return builder.build().toString();
    }

}
