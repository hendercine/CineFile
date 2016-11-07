package com.example.android.cinefile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.cinefile.adapters.MainViewAdapter;
import com.example.android.cinefile.data.MovieDbHandler;
import com.example.android.cinefile.objects.Movie;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private boolean mIsTablet, mSnackbarShown;
    private Utility mUtility;
    private MovieDbHandler mMovieDbHandler;
    private GridView mGridView;
    private SharedPreferences mSharedPref;
    private String mSortCriteria;
    private ArrayList<String> mPosterPaths, mMovieIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIsTablet = findViewById(R.id.detail_frame) != null;
        mSharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        mUtility = new Utility();
        mGridView = (GridView) findViewById(R.id.grid_view_posters);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadDetails(position);
            }
        });

        if (savedInstanceState != null) {
            mSortCriteria = savedInstanceState.getString("SORT_CRITERIA");
            mPosterPaths = savedInstanceState.getStringArrayList("POSTER_PATHS");
            if (savedInstanceState.getBoolean("SNACKBAR_VISIBLE"))
                showOfflineSnackbar();
            if (mPosterPaths != null) {
                MainViewAdapter adapter = new MainViewAdapter(this, mPosterPaths);
                mGridView.setAdapter(adapter);
                mGridView.setSelection(savedInstanceState.getInt("GRID_SCROLL_STATE"));
                if (mIsTablet) {
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.detail_frame,
                                    getSupportFragmentManager().
                                            getFragment(savedInstanceState, "DETAIL_FRAGMENT")).
                            commit();
                }
            }
        } else refreshContent();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("SORT_CRITERIA", mSortCriteria);
        outState.putStringArrayList("POSTER_PATHS", mPosterPaths);
        outState.putInt("GRID_SCROLL_STATE", mGridView.getFirstVisiblePosition());
        outState.putBoolean("SNACKBAR_VISIBLE", mSnackbarShown);
        if (mIsTablet && getSupportFragmentManager().findFragmentById(R.id.detail_frame) != null)
            getSupportFragmentManager().putFragment(outState,
                    "DETAIL_FRAGMENT",
                    getSupportFragmentManager().findFragmentById(R.id.detail_frame));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.menu_popup, menu);
        String sortCriteria = mSharedPref.getString("sort_criteria", "pop");
        switch (sortCriteria) {
            case "pop":
                menu.findItem(R.id.menu_popular).setChecked(true);
                break;
            case "rat":
                menu.findItem(R.id.menu_top_rated).setChecked(true);
                break;
            case "fav":
                menu.findItem(R.id.menu_favorites).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_popular || id == R.id.menu_top_rated || id == R.id.menu_favorites) {
            SharedPreferences.Editor editor = mSharedPref.edit();

            String criteria;
            if (id == R.id.menu_popular)
                criteria = "pop";
            else if (id == R.id.menu_top_rated)
                criteria = "rat";
            else criteria = "fav";

            String temp = mSharedPref.getString("sort_criteria", "pop");
            if (!criteria.equals(temp)) {
                editor.putString("sort_criteria", criteria);
                editor.apply();
                invalidateOptionsMenu();
                refreshContent();
            }
        } else if (id == R.id.action_settings)
            Toast.makeText(MainActivity.this, "Coming soon.", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    public void refreshContent() {
        mSortCriteria = mSharedPref.getString("sort_criteria", "pop");
        if (mUtility.isNetworkAvailable(this) || mSortCriteria.equals("fav"))
            new ImageLoadTask().execute(mSortCriteria);
            // This also loads first movie's details on tablet devices
        else showOfflineSnackbar();
    }

    public void showOfflineSnackbar() {
        Snackbar.make(findViewById(R.id.container),
                R.string.offline_message,
                Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSnackbarShown = false;
                refreshContent();
            }
        }).show();
        mSnackbarShown = true;
    }

    public void loadDetails(final int position) {
        if (mUtility.isNetworkAvailable(this) || mSortCriteria.equals("fav")) {
            if (mIsTablet && (getSupportFragmentManager().findFragmentById(R.id.detail_frame) != null)) {
                getSupportFragmentManager().
                        beginTransaction().
                        remove(getSupportFragmentManager().findFragmentById(R.id.detail_frame)).
                        commit();
            }
        } else { Snackbar.make(findViewById(R.id.container),
                R.string.offline_message,
                Snackbar.LENGTH_LONG).
                show();}

        new FetchDetailsTask(mSortCriteria, position).execute();
    }

    public class ImageLoadTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            ArrayList<String> posterPaths = new ArrayList<>();

            if (!params[0].equals("fav")) {
                if (mUtility.isNetworkAvailable(MainActivity.this)) {
                    boolean sortByPopularity = params[0].equals("pop");
                    try {
                        posterPaths = mUtility.getPosterPaths(sortByPopularity);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                mMovieDbHandler = new MovieDbHandler(getApplicationContext());
                mMovieIds = mMovieDbHandler.fetchFavouriteIds();

                for (String Id : mMovieIds)
                    posterPaths.add(mMovieDbHandler.fetchPosterPath(Id));
            }
            if (params[0].equals("fav"))
                mPosterPaths = posterPaths;
            else if (posterPaths.size() != 0)
                mPosterPaths = posterPaths;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            MainViewAdapter adapter = new MainViewAdapter(MainActivity.this, mPosterPaths);
            mGridView.setAdapter(adapter);
            if (adapter.getCount() == 0)
                findViewById(R.id.noItems_textView).setVisibility(View.VISIBLE);
            else {
                findViewById(R.id.noItems_textView).setVisibility(View.GONE);
                //Load first movie's details on tablet if no selection had been made yet
                if (mIsTablet && (getSupportFragmentManager().
                        findFragmentById(R.id.detail_frame)== null)) {
                    new FetchDetailsTask(mSortCriteria, 0).execute();
                }
            }
        }
    }

    public class FetchDetailsTask extends AsyncTask<Void, Void, Movie> {

        private String mSortCriteria;
        private int position;

        FetchDetailsTask(String sortCriteria, int position) {
            this.mSortCriteria = sortCriteria;
            this.position = position;
        }

        @Override
        protected Movie doInBackground(Void... params) {
            Movie movie = new Movie();

            if (!mSortCriteria.equals("fav")) {
                if (mUtility.isNetworkAvailable(MainActivity.this))
                    try {
                        movie = mUtility.getMovieData(mSortCriteria.equals("pop"), position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } else movie = null;
            } else if (mSortCriteria.equals("fav")) {
                mMovieDbHandler = new MovieDbHandler(getApplicationContext());
                mMovieIds = mMovieDbHandler.fetchFavouriteIds();
                if (mMovieIds.size() != 0) {
                    movie = mMovieDbHandler.fetchMovieDetails(mMovieIds.get(position));
                }
            } else movie = null;
            return movie;
        }

        @Override
        protected void onPostExecute(Movie result) {
            if (result != null) {
                if (mIsTablet) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("movie", Parcels.wrap(result));
                    DetailFragment detailFragment = new DetailFragment();
                    detailFragment.setArguments(bundle);
                    getSupportFragmentManager().
                            beginTransaction().
                            replace(R.id.detail_frame, detailFragment).
                            commit();
                } else {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("movie", Parcels.wrap(result));
                    startActivity(intent);
                }
            }
        }

    }
}
