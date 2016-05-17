package com.example.mukesh.filmo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{

    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (findViewById(R.id.fragmentdetail) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
               getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentdetail, new Movie_detailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
           mTwoPane = false;

        }


    }

    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("MOVIE", movie);
            Movie_detailFragment fragment = new Movie_detailFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentdetail, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, Movie_detail.class);
            Bundle b = new Bundle();
            b.putParcelable("MOVIE", movie);
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings_Activity.class));
            return true;
        }
        if(id==R.id.action_about){
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.about_message),
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 10);
            toast.show();

        }
        return super.onOptionsItemSelected(item);
    }
}
