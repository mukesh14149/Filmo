package com.example.mukesh.filmo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class Movie_detail extends AppCompatActivity {

    private ShareActionProvider mShareActionProvider;
    Movie movie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            Bundle b = getIntent().getExtras();
            movie = b.getParcelable("MOVIE");
            Bundle bundle = new Bundle();
            bundle.putParcelable("MOVIE",
                    getIntent().getParcelableExtra("MOVIE"));

            Movie_detailFragment fragment = new Movie_detailFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentdetail, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

       setShareIntent();


        return true;
    }

    private void setShareIntent() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, movie.getTitle() + " " +
                "http://www.youtube.com/watch?v=" + movie.getVideourl());

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int selected_id = item.getItemId();
        if (selected_id == R.id.action_settings)
        {
            startActivity(new Intent(this, Settings_Activity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
