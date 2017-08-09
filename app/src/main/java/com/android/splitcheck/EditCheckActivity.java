package com.android.splitcheck;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.splitcheck.data.Check;
import com.android.splitcheck.data.Item;
import com.android.splitcheck.data.ItemContract;

import butterknife.ButterKnife;

public class EditCheckActivity extends AppCompatActivity {

    static final int NUM_ITEMS = 3;

    EditCheckItemsFragment mEditCheckItemsFragment;
    FragmentManager mFragmentManager;
    TextView mCheckNameTextView;
    TextView mCheckTotalTextView;
    FragmentPagerAdapter mFragmentPagerAdapter;

    static int checkId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_check);

        // Save instance to keep checkId
        if (savedInstanceState == null) {
            Intent intentThatStartedThisActivity = getIntent();
            checkId = intentThatStartedThisActivity.getIntExtra("checkId", -1);
        } else {
            checkId = savedInstanceState.getInt("checkId");
        }
//        mCheckNameTextView = ButterKnife.findById(this, R.id.text_view_edit_check_name);
        mCheckTotalTextView = ButterKnife.findById(this, R.id.text_view_edit_check_total);

        Check check = new Check(getContentResolver(), checkId);
//        mCheckNameTextView.setText(check.getName());

        // REMOVE ONCE DONE, USED FOR TOAST MESSAGES FOR NOW
        Uri uri = ItemContract.ItemEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(checkId)).build();
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        if (c == null) {
            Toast.makeText(this, "Check ID: " + checkId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Items: " + c.getCount(), Toast.LENGTH_SHORT).show();
        }

        ViewPager vp = ButterKnife.findById(this, R.id.pager);
        TabLayout tabLayout = ButterKnife.findById(this, R.id.pager_tabs);
        Toolbar toolbar = ButterKnife.findById(this, R.id.edit_check_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(check.getName());
        mFragmentPagerAdapter = new EditCheckPagerAdapter(getSupportFragmentManager());
        tabLayout.setupWithViewPager(vp);
        vp.setAdapter(mFragmentPagerAdapter);

        updateUI();

    }

    public void updateUI() {
        Item item = new Item();
        // Check Total IMPLEMENT YOUR TOTAL, LEFT SIDE?
        String total = item.getTotalAsStringFromCheckId(getContentResolver(), checkId);
        mCheckTotalTextView.setText(total);
//        Bundle bundle = new Bundle();
//        bundle.putInt("checkId", checkId);
//        mFragmentManager = getSupportFragmentManager();
//        mEditCheckItemsFragment = new EditCheckItemsFragment();
//        mEditCheckItemsFragment.setArguments(bundle);
//        mFragmentManager.beginTransaction()
//                .add(R.id.container_edit_check_items, mEditCheckItemsFragment)
//                .commit();

        // INSTEAD OF FRAGMENT FOR ITEMS, CHANGE TO ADAPTER FOR PAGEVIEWER
    }

    @Override
    protected void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putInt("checkId", checkId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateUI();
    }

    public static class EditCheckPagerAdapter extends FragmentPagerAdapter {
        FragmentManager fragmentManager;

        public EditCheckPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentManager = fm;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("checkId", checkId);
            switch (position) {
                case 0:
                    EditCheckItemsFragment editCheckItemsFragment = new EditCheckItemsFragment();
                    editCheckItemsFragment.setArguments(bundle);
                    return editCheckItemsFragment;
//                    return EditCheckItemsFragment.newInstance(checkId);
                case 1:
                    EditCheckParticipantsFragment editCheckParticipantsFragment = new EditCheckParticipantsFragment();
                    editCheckParticipantsFragment.setArguments(bundle);
                    return editCheckParticipantsFragment;
                case 2:
                    EditCheckItemsFragment fragment = new EditCheckItemsFragment();
                    fragment.setArguments(bundle);
                    return fragment;
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Page 1";
                case 1:
                    return "Page 2";
                case 2:
                    return "Page 3";
                default:
                    return null;
            }
        }
    }
}
