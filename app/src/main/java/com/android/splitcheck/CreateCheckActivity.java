package com.android.splitcheck;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.splitcheck.data.CheckContract;

public class CreateCheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_check);

        final EditText titleEditText = (EditText) findViewById(R.id.edit_text_create_check_title);
        Button titleButton = (Button) findViewById(R.id.button_create_check_title);
        titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!titleEditText.getText().toString().isEmpty()) {
                    createCheck(titleEditText.getText().toString());
                    finish();
                }
            }
        });

    }

    public void createCheck(String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CheckContract.CheckEntry.NAME,
                name);
        contentValues.put(CheckContract.CheckEntry.TOTAL,
                "0");
        contentValues.put(CheckContract.CheckEntry.PARTICIPANTS,
                "None");
        contentValues.put(CheckContract.CheckEntry.ITEMS,
                "None");
        contentValues.put(CheckContract.CheckEntry.TIME_CREATED,
                System.currentTimeMillis());
        Uri uri = getContentResolver().insert(CheckContract.CheckEntry.CONTENT_URI,
                contentValues);
        if (uri != null) {
            Toast.makeText(CreateCheckActivity.this, "Created a check.", Toast.LENGTH_SHORT).show();
        }
    }


}
