package com.android.splitcheck;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.splitcheck.data.CheckContract;

public class CheckListActivity extends AppCompatActivity {

    CheckListFragment mCheckListFragment;
    FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        // Set up Fragment for Check List
        /**mFragmentManager = getSupportFragmentManager();
        mCheckListFragment = new CheckListFragment();
        mFragmentManager.beginTransaction()
                .add(R.id.fragment_check_list_container, mCheckListFragment)
                .commit();**/

        if (savedInstanceState == null) {
            updateUI();
        }


        Uri uri = CheckContract.CheckEntry.CONTENT_URI;
        uri = uri.buildUpon().build();
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        if (c == null) {

        } else {
            Toast.makeText(this, "Checks: " + c.getCount(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_check_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.log_in:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateUI() {
        mFragmentManager = getSupportFragmentManager();
        mCheckListFragment = new CheckListFragment();
        mFragmentManager.beginTransaction()
                .add(R.id.fragment_check_list_container, mCheckListFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateUI();
    }
}
