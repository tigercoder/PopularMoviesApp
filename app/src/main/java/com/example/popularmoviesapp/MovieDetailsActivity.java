package com.example.popularmoviesapp;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.popularmoviesapp.Database.Favorite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity implements TrailersRecyclerViewAdapter.TrailersRecyclerViewAdapterOnClickHandler {

    @BindView(R.id.title)  TextView titleTextView;
    @BindView(R.id.rating)  TextView ratingTextView;
    @BindView(R.id.overview)  TextView overviewTextView;
    @BindView(R.id.release_date)  TextView releaseDateTextView;
    @BindView(R.id.image)  ImageView imageView;
    @BindView(R.id.recycler_view2) RecyclerView recyclerView2;
    private TrailersRecyclerViewAdapter trailersRecyclerViewAdapter;
    private String url2;
    private String apiKey;
    private String posterUrl;
    private Boolean isFavorite;
    private static final String TAG = "NathanLog";
    @BindView(R.id.togglebutton) ToggleButton toggle;
    private Favorite theFavorite;
    private Favorite originalFavorite;
    private int idReturnedFromFavorite;
    private TrailersRecyclerViewAdapter.TrailersRecyclerViewAdapterOnClickHandler clickHandler;
    private String movieId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        clickHandler = this;
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        recyclerView2.setLayoutManager(linearLayoutManager2);
        recyclerView2.setHasFixedSize(true);
        String title;
        String rating;
        String overview;
        String releaseDate;
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            posterUrl= null;
            title= null;
            rating= null;
            overview= null;
            releaseDate = null;
            movieId = null;
        } else {
            posterUrl= extras.getString(getString(R.string.poster_url_variable_name));
            title= extras.getString(getString(R.string.title_variable_name));
            rating= extras.getString(getString(R.string.rating_variable_name));
            overview= extras.getString(getString(R.string.overview_variable_name));
            releaseDate= extras.getString(getString(R.string.release_date_variable_name));
            movieId= extras.getString("movieId");
        }
        titleTextView.setText(title);
        ratingTextView.setText(rating);
        overviewTextView.setText(overview);
        releaseDateTextView.setText(releaseDate);
        //Picasso.get().load(posterUrl).into(imageView);
        Glide.with(this).load(posterUrl).into(imageView);
        apiKey = BuildConfig.ApiKey;
        url2 = "http://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=" + apiKey;
        theFavorite = new Favorite();
        isFavorite=false;
        isFavorite();
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    addFavorite();
                    // The toggle is enabled
                } else {
                    deleteFavorite();
                    // The toggle is disabled
                }
            }
        });
        new FetchReviewTask().execute(url2);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.review_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.review){
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("movieId", movieId);
            startActivity(intent);
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    public void addFavorite(){
        Favorite favorite = new Favorite();
        favorite.setMovieId(movieId);
        favorite.setMovieName(titleTextView.getText().toString());
        favorite.setOverview(overviewTextView.getText().toString());
        favorite.setPosterUrl(posterUrl);
        favorite.setReleaseDate(releaseDateTextView.getText().toString());
        favorite.setVoteAverage(ratingTextView.getText().toString());
        MainActivity.myAppDatabase.myDao().addFavorite(favorite);
    }

    public void isFavorite(){
        MovieDetailsViewModelFactory movieDetailsViewModelFactory = new MovieDetailsViewModelFactory(MainActivity.myAppDatabase, movieId);
        final MovieDetailsViewModel movieDetailsViewModel = ViewModelProviders.of(this, movieDetailsViewModelFactory).get(MovieDetailsViewModel.class);
        movieDetailsViewModel.getFavorite().observe(this, new Observer<Favorite>() {
            @Override
            public void onChanged(@Nullable Favorite favorite) {
                movieDetailsViewModel.getFavorite().removeObserver(this);
                if (favorite == null){
                    toggle.setChecked(false);
                }
                else if ((favorite.getMovieId().equals(movieId)) && !toggle.isChecked()) {
                    toggle.setChecked(true);
                }
                else {
                }
            }
        });
    }

    public void deleteFavorite(){
        MainActivity.myAppDatabase.myDao().deleteFavorite(movieId);
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    @Override
    public void onClick(String movieId) {
        watchYoutubeVideo(this, movieId);
    }


    public class FetchReviewTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String newJsonResponseVariable = new String();
            URL url = null;
            try {
                Uri uri = Uri.parse(strings[0]);
                url = new URL(uri.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                newJsonResponseVariable = getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newJsonResponseVariable;
        }

        @Override
        protected void onPostExecute(String s) {
            JSONObject jsonObjectTrailer;
            JSONArray jsonArrayTrailer;
            JSONObject jsonObjectTrailer2;
            String key;
            ArrayList<String> keys = new ArrayList<String>();
            try {
                jsonObjectTrailer = new JSONObject(s);
                jsonArrayTrailer = jsonObjectTrailer.getJSONArray(getString(R.string.results));
                for (int i=0; i<jsonArrayTrailer.length(); i++){
                    jsonObjectTrailer2 = jsonArrayTrailer.getJSONObject(i);
                    key = jsonObjectTrailer2.getString("key");
                    keys.add(key);
                }
                trailersRecyclerViewAdapter = new TrailersRecyclerViewAdapter(keys, clickHandler);
                recyclerView2.setAdapter(trailersRecyclerViewAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
