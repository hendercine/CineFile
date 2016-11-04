package com.example.android.cinefile;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.cinefile.adapters.MainViewAdapter;
import com.example.android.cinefile.data.MovieDbHandler;
import com.example.android.cinefile.objects.Movie;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private boolean mIsTablet, mSnackbarShown;
    private Utility mUtility;
    private MovieDbHandler mMovieDbHandler;
    private GridView mGridView;
    private SharedPreferences mSharedPref;
    private String mSortOrder;
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
            mSortOrder = savedInstanceState.getString("SORT_ORDER");
            mPosterPaths = savedInstanceState.getStringArrayList("POSTER_PATHS");
            if (savedInstanceState.getBoolean("SNACKBAR_VISIBLE"))
                showOfflineSnackbar();
            if (mPosterPaths != null) {
                MainViewAdapter adapter = new MainViewAdapter(this, mPosterPaths);
                mGridView.setAdapter(adapter);
                if (mIsTablet) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.detail_frame,
                                    getFragmentManager().
                                            getFragment(savedInstanceState, "DETAIL_FRAGMENT")).
                            commit();
                }
            }
        } else refreshContent();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("SORT_ORDER", mSortOrder);
        outState.putStringArrayList("POSTER_PATHS", mPosterPaths);
        outState.putInt("GRID_SCROLL_STATE", mGridView.getFirstVisiblePosition());
        outState.putBoolean("SNACKBAR_VISIBLE", mSnackbarShown);
        if (mIsTablet && getFragmentManager().findFragmentById(R.id.detail_frame) != null)
            getFragmentManager().putFragment(outState,
                    "DETAIL_FRAGMENT",
                    getFragmentManager().findFragmentById(R.id.detail_frame));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_popup, menu);
        String sortOrder = mSharedPref.getString("sort_criteria", "popular");
        switch (sortOrder) {
            case "popular":
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

            String order;
            if (id == R.id.menu_popular)
                order = "menu_popular";
            else if (id == R.id.menu_top_rated)
                order = "menu_top_rated";
            else order = "menu_favorites";

            String temp = mSharedPref.getString("sort_order", "menu_popular");
            if (!order.equals(temp)) {
                editor.putString("sort_order", order);
                editor.apply();
                invalidateOptionsMenu();
                refreshContent();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshContent() {
        mSortOrder = mSharedPref.getString("sort_criteria", "menu_popular");
        if (mUtility.isNetworkAvailable(this) || mSortOrder.equals("menu_favorites"))
            new ImageLoadTask().execute(mSortOrder);
            // This also loads first movie's details on tablet devices
        else showOfflineSnackbar();
    }

    public void showOfflineSnackbar() {
        Snackbar.make(findViewById(R.id.grid_view_posters),
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
        if (mUtility.isNetworkAvailable(this) || mSortOrder.equals("menu_favorites")) {
            if (mIsTablet && (getFragmentManager().findFragmentById(R.id.detail_frame) != null)) {
                getFragmentManager().
                        beginTransaction().
                        remove(getFragmentManager().findFragmentById(R.id.detail_frame)).
                        commit();
                new FetchDetailsTask(mSortOrder, position).execute();
            } else Snackbar.make(findViewById(R.id.container),
                    R.string.offline_message,
                    Snackbar.LENGTH_LONG).
                    show();
        }
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
                if (mIsTablet && (getFragmentManager().findFragmentById(R.id.detail_frame)== null)) {
                    new FetchDetailsTask(mSortOrder, 0).execute();
                }
            }
        }
    }

    public class FetchDetailsTask extends AsyncTask<Void, Void, Movie> {

        private String sortCriteria;
        private int position;

        public FetchDetailsTask(String sortCriteria, int position) {
            this.sortCriteria = sortCriteria;
            this.position = position;
        }

        @Override
        protected Movie doInBackground(Void... params) {
            Movie movie = new Movie();

            if (!sortCriteria.equals("menu_favorites")) {
                if (mUtility.isNetworkAvailable(MainActivity.this))
                    try {
                        movie = mUtility.getMovieData(sortCriteria.equals("menu_popular"), position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                else movie = null;
            } else if (mMovieIds.size() != 0)
                movie = mMovieDbHandler.fetchMovieDetails(mMovieIds.get(position));
            else movie = null;
            return movie;
        }

        @Override
        protected void onPostExecute(Movie result) {
            if (result != null) {
                if (mIsTablet) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("movie", Parcels.wrap(result));
                    Fragment fragment = new Fragment();
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.detail_frame, fragment).
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
