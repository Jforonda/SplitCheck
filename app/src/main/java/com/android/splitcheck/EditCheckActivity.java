package com.android.splitcheck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.splitcheck.data.Check;

import butterknife.ButterKnife;

public class EditCheckActivity extends AppCompatActivity {

    TextView mCheckNameTextView;
    ImageView mAddItemImageView;

    int checkId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_check);

        if (savedInstanceState == null) {
            Intent intentThatStartedThisActivity = getIntent();
            checkId = intentThatStartedThisActivity.getIntExtra("checkId", -1);
        } else {
            checkId = savedInstanceState.getInt("checkId");
        }

        Check check = new Check(getContentResolver(), checkId);
        mCheckNameTextView = ButterKnife.findById(this, R.id.text_view_edit_check_name);
        mAddItemImageView = ButterKnife.findById(this, R.id.image_view_edit_check_add_item);

        mAddItemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AddItem
                FragmentManager fm = getSupportFragmentManager();
                CreateItemFragment createItemFragment = CreateItemFragment.newInstance("Item Name");
                createItemFragment.show(fm, "Item Name");
            }
        });

        mCheckNameTextView.setText(check.getName());

    }

    public void updateUI() {

    }

    @Override
    protected void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putInt("checkId", checkId);
    }
}
