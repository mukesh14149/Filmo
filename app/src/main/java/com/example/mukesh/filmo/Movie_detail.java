package com.example.mukesh.filmo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class Movie_detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("tripute111"+savedInstanceState);
            setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null) {
            Bundle b = getIntent().getExtras();
            final Movie movie = b.getParcelable("MOVIE");
            System.out.println("tripute3"+movie.getTitle());
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
        return true;
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
