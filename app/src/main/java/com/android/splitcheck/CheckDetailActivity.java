package com.android.splitcheck;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.android.splitcheck.data.Check;
import com.android.splitcheck.data.CheckParticipant;
import com.android.splitcheck.data.Item;
import com.android.splitcheck.data.ItemParticipant;

import butterknife.ButterKnife;

public class CheckDetailActivity extends AppCompatActivity implements 
        CheckDetailModifiersFragment.ModifierChangeListener,
        CheckDetailItemsFragment.ItemChangeListener,
        CheckDetailParticipantsFragment.ParticipantChangeListener {
    static final int NUM_ITEMS = 3;

    FragmentManager mFragmentManager;
    TextView mCheckYourTotalTextView, mCheckSubtotalTextView, mCheckTotalTextView;
    FragmentPagerAdapter mFragmentPagerAdapter;

    static int checkId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_check_detail);

        // Save instance to keep checkId
        if (savedInstanceState == null) {
            Intent intentThatStartedThisActivity = getIntent();
            checkId = intentThatStartedThisActivity.getIntExtra("checkId", -1);
        } else {
            checkId = savedInstanceState.getInt("checkId");
        }
        mCheckYourTotalTextView = ButterKnife.findById(this, R.id.text_view_edit_check_your_total);
        mCheckSubtotalTextView = ButterKnife.findById(this, R.id.text_view_edit_check_subtotal);
        mCheckTotalTextView = ButterKnife.findById(this, R.id.text_view_edit_check_total);

        Check check = new Check(getContentResolver(), checkId);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_check_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent intent = new Intent(this, ParticipantsActivity.class);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(getBaseContext(), R.animator.anim_enter_left, R.animator.anim_exit_right);
                startActivity(intent, options.toBundle());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateUI() {
        Item item = new Item();
        ItemParticipant itemParticipant = new ItemParticipant();
        Check check = new Check();
        CheckParticipant checkParticipant = new CheckParticipant();
        checkParticipant.updateCheckParticipantTotals(getContentResolver(), checkId);
        String yourTotal = checkParticipant.getParticipantTotalWithModifier(getContentResolver(), checkId, 1);
        if (yourTotal == null) {
            yourTotal = getString(R.string.check_your_total_default);
        } else {
            yourTotal = getString(R.string.check_your_total, yourTotal);
        }
        // Update CheckParticipant Totals
        String subtotal = item.getSubtotal(getContentResolver(), checkId);
        String total = item.getTotal(getContentResolver(), checkId);
        mCheckYourTotalTextView.setText(yourTotal);
        mCheckSubtotalTextView.setText(subtotal);
        mCheckTotalTextView.setText(total);
        check.updateTotal(getContentResolver(), checkId, total);
    }

    @Override
    protected void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putInt("checkId", checkId);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class EditCheckPagerAdapter extends FragmentPagerAdapter {
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
                    // Items
                    CheckDetailItemsFragment checkDetailItemsFragment = new CheckDetailItemsFragment();
                    checkDetailItemsFragment.setArguments(bundle);
                    return checkDetailItemsFragment;
                case 1:
                    // Participants
                    CheckDetailParticipantsFragment checkDetailParticipantsFragment = new CheckDetailParticipantsFragment();
                    checkDetailParticipantsFragment.setArguments(bundle);
                    return checkDetailParticipantsFragment;
                case 2:
                    // Modifiers
                    CheckDetailModifiersFragment checkDetailModifiersFragment = new CheckDetailModifiersFragment();
                    checkDetailModifiersFragment.setArguments(bundle);
                    return checkDetailModifiersFragment;
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_items);
                case 1:
                    return getString(R.string.tab_group);
                case 2:
                    return getString(R.string.tab_modifiers);
                default:
                    return null;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    // Callback Methods on Database Change

    @Override
    public void onChangeItem() {
        updateUI();
        mFragmentPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChangeParticipant() {
        updateUI();
        mFragmentPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChangeModifier() {
        updateUI();
        mFragmentPagerAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.animator.anim_enter_right, R.animator.anim_exit_left);
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.animator.anim_enter_right, R.animator.anim_exit_left);
    }
}
