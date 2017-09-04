package com.android.splitcheck;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.splitcheck.data.Participant;
import com.android.splitcheck.data.ParticipantContract;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class ParticipantsActivity extends AppCompatActivity {

    private Cursor mCursor;
    private long mStartId;

    ParticipantsAdapter mParticipantsAdapter;
    MaterialSearchView mSearchView;
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    boolean mParticipantDeleted;
    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        mParticipantDeleted = false;
        mToolbar = ButterKnife.findById(this, R.id.participants_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressBar = ButterKnife.findById(this, R.id.progress_bar_search_participants);
        mContext = this;

        // Set Adapter
        Participant participant = new Participant();
        ArrayList<Participant> participants = participant.getListOfParticipantsFromDatabase(getContentResolver());
        mParticipantsAdapter = new ParticipantsAdapter(mContext, participants);
        mParticipantsAdapter.setOnParticipantDeletedListener(new ParticipantsAdapter.OnParticipantDeletedListener() {
            @Override
            public void onParticipantDeleted() {
                mParticipantDeleted = true;
            }
        });
        mRecyclerView = ButterKnife.findById(this, R.id.recycler_view_search_participants);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mParticipantsAdapter);

        mSearchView = ButterKnife.findById(this, R.id.participants_search_view);
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                new FetchParticipantsTask().execute(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                Participant participant = new Participant();
                ArrayList<Participant> participants = participant.getListOfParticipantsFromDatabase(getContentResolver());
                mParticipantsAdapter = new ParticipantsAdapter(mContext, participants);
                mRecyclerView.setAdapter(mParticipantsAdapter);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_participants, menu);

        MenuItem item = menu.findItem(R.id.participants_search);
        mSearchView.setMenuItem(item);

        return true;
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showParticipants() {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public class FetchParticipantsTask extends AsyncTask<String, Void, ArrayList<Participant>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar();
        }

        @Override
        protected ArrayList<Participant> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String search;
            String searchParam;
            if (params[0].equals("")) {
                searchParam = null;
            } else {
                search = params[0];
                searchParam = ParticipantContract.ParticipantEntry.FIRST_NAME + " LIKE \'%" + search + "%\' OR "
                        + ParticipantContract.ParticipantEntry.LAST_NAME + " LIKE \'%" + search + "%'";
            }

            Uri uri = ParticipantContract.ParticipantEntry.CONTENT_URI;
            Cursor c = getContentResolver().query(uri, null, searchParam, null, null);
            ArrayList<Participant> participants = new ArrayList<>();
            if (c != null) {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    String firstName = c.getString(c.getColumnIndex(ParticipantContract.
                            ParticipantEntry.FIRST_NAME));
                    String lastName = c.getString(c.getColumnIndex(ParticipantContract.
                            ParticipantEntry.LAST_NAME));
                    int id = c.getInt(c.getColumnIndex(ParticipantContract.
                            ParticipantEntry._ID));
                    Participant participant = new Participant(firstName, lastName, id);
                    participants.add(participant);
                    c.moveToNext();
                }
            }
            return participants;
        }

        @Override
        protected void onPostExecute(ArrayList<Participant> participants) {
            if (!participants.isEmpty()) {
                showParticipants();
                // Set Adapter
                mParticipantsAdapter = new ParticipantsAdapter(mContext, participants);
                mRecyclerView.setAdapter(mParticipantsAdapter);
                mParticipantsAdapter.setParticipantData(participants);
            } else {
                //ShowError
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (mParticipantDeleted) {
            finish();
            overridePendingTransition(R.animator.anim_enter_right, R.animator.anim_exit_left);
            Intent intent = new Intent(ParticipantsActivity.this, CheckListActivity.class);
            startActivity(intent);
            return super.onSupportNavigateUp();
        } else {
            finish();
            overridePendingTransition(R.animator.anim_enter_right, R.animator.anim_exit_left);
            return super.onSupportNavigateUp();
        }
    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        } else {
            if (mParticipantDeleted) {
                finish();
                overridePendingTransition(R.animator.anim_enter_right, R.animator.anim_exit_left);
                Intent intent = new Intent(ParticipantsActivity.this, CheckListActivity.class);
                startActivity(intent);
            } else {
                super.onBackPressed();
                finish();
                overridePendingTransition(R.animator.anim_enter_right, R.animator.anim_exit_left);
            }
        }
    }
}
