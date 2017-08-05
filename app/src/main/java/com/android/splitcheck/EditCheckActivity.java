package com.android.splitcheck;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.splitcheck.data.Check;
import com.android.splitcheck.data.Item;
import com.android.splitcheck.data.ItemContract;

import butterknife.ButterKnife;

public class EditCheckActivity extends AppCompatActivity {

    EditCheckItemsFragment mEditCheckItemsFragment;
    FragmentManager mFragmentManager;
    TextView mCheckNameTextView;
    TextView mCheckTotalTextView;

    int checkId;

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
        mCheckNameTextView = ButterKnife.findById(this, R.id.text_view_edit_check_name);
        mCheckTotalTextView = ButterKnife.findById(this, R.id.text_view_edit_check_total);

        Check check = new Check(getContentResolver(), checkId);
        mCheckNameTextView.setText(check.getName());

        // REMOVE ONCE DONE, USED FOR TOAST MESSAGES FOR NOW
        Uri uri = ItemContract.ItemEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(checkId)).build();
//        uri = uri.buildUpon().build();
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        if (c == null) {
            Toast.makeText(this, "Check ID: " + checkId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Items: " + c.getCount(), Toast.LENGTH_SHORT).show();
        }

        updateUI();

    }

    public void updateUI() {
        Item item = new Item();
        // Check Total IMPLEMENT YOUR TOTAL, LEFT SIDE?
        mCheckTotalTextView.setText(item.getTotalAsStringFromCheckId(getContentResolver(), checkId));
        Bundle bundle = new Bundle();
        bundle.putInt("checkId", checkId);
        mFragmentManager = getSupportFragmentManager();
        mEditCheckItemsFragment = new EditCheckItemsFragment();
        mEditCheckItemsFragment.setArguments(bundle);
        mFragmentManager.beginTransaction()
                .add(R.id.container_edit_check_items, mEditCheckItemsFragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putInt("checkId", checkId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}
