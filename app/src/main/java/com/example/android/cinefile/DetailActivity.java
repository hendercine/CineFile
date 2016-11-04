package com.example.android.cinefile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.cinefile.objects.Movie;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    private DetailFragment mDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));

        mDetailFragment = new DetailFragment();
        if (savedInstanceState != null) {
            getSupportFragmentManager().getFragment(savedInstanceState, "fragment");
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelable("movie", Parcels.wrap(movie));
            mDetailFragment.setArguments(bundle);
        }
        getSupportFragmentManager().beginTransaction().
                replace(R.id.container, mDetailFragment).commit();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mDetailFragment.getFragmentManager().putFragment(outState, "fragment", mDetailFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}