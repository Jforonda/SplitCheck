package com.android.splitcheck;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.splitcheck.data.Participant;

public class CheckListActivity extends AppCompatActivity {

    CheckListFragment mCheckListFragment;
    FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        if (savedInstanceState == null) {
            updateUI();
        }
        Participant participant = new Participant();
        if (!participant.selfHasBeenAdded(getContentResolver())) {
            Intent intent = new Intent(this, LoginActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getBaseContext(), R.animator.anim_enter_left, R.animator.anim_exit_right);
            startActivity(intent, options.toBundle());
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
            case R.id.log_in:
                intent = new Intent(this, LoginActivity.class);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(getBaseContext(), R.animator.anim_enter_left, R.animator.anim_exit_right);
                startActivity(intent, options.toBundle());
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
}
