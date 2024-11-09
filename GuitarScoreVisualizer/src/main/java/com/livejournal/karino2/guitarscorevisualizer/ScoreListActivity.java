package com.livejournal.karino2.guitarscorevisualizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


/**
 * An activity representing a list of Scores. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ScoreDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ScoreListFragment} and the item details
 * (if present) is a {@link ScoreDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link ScoreListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ScoreListActivity extends FragmentActivity
        implements ScoreListFragment.Callbacks {

    final int ACTIVITY_ID_NEW = 1;
    final int REQUEST_PICK_FILE = 2;


    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_list);

        if (findViewById(R.id.score_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // for deletion, I just ignore this.
            /*
            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ScoreListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.score_list))
                    .setActivateOnItemClick(true);
                    */
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    @Override
    public void onBackPressed() {
        if(doProcessBack()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean doProcessBack() {
        if(mTwoPane) {
            try {
                return ((ScoreDetailFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.score_detail_container)).doBackProcess();
            }catch(NullPointerException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.score_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_add:
                startEditActivity();
                return true;
            case R.id.action_export:
                exportToJson();
                return true;
            case R.id.action_import:

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, REQUEST_PICK_FILE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    Database getDatabase(Context ctx) { return Database.getInstance(ctx); }


    public void exportToJson() {
        Database db = getDatabase(this);
        File file = null;
        try {
            file = db.exportAllToJson();
            if(file == null) {
                showMessage("No score.");
                return;
            }
            showMessage("saved at " + file.getAbsolutePath());
        } catch (IOException e) {
            showMessage("IOException: " + e.getMessage());
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    private void reloadList() {
        ((ScoreListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.score_list)).reloadCursor();
    }

    private void startEditActivity() {
        Intent intent = new Intent(this, EditActivity.class);
        startActivityForResult(intent, ACTIVITY_ID_NEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case ACTIVITY_ID_NEW:
                reloadList();
                return;
            case REQUEST_PICK_FILE:
                if(resultCode == Activity.RESULT_OK) {
                    String path = data.getData().getPath();
                    try {
                        importFromJson(new FileReader(path));
                        reloadList();
                    } catch (FileNotFoundException e) {
                        showMessage("File not found: " + e.getMessage());
                    }
                }
                return;
        }
    }

    private void importFromJson(Reader reader) {
        Gson gson = new Gson();

        Type collectionType = new TypeToken<Collection<Database.ScoreDto>>(){}.getType();

        Database db = getDatabase(this);

        Collection<Database.ScoreDto> col = gson.fromJson(reader, collectionType);
        Database.ScoreDto[] results = col.toArray(new Database.ScoreDto[0]);
        for(int i = 0; i < results.length; i++) {
            Database.ScoreDto dto = results[results.length-1-i];
            db.insertScoreDto(dto);
        }
    }

    /**
     * Callback method from {@link ScoreListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(long id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            updateDetailFragment(id);

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ScoreDetailActivity.class);
            detailIntent.putExtra(ScoreDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onItemsDeleted() {
        if(mTwoPane) {
            updateDetailFragment(-1);
        }
    }

    private void updateDetailFragment(long id) {
        Bundle arguments = new Bundle();
        arguments.putLong(ScoreDetailFragment.ARG_ITEM_ID, id);
        ScoreDetailFragment fragment = new ScoreDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.score_detail_container, fragment)
                .commit();
    }
}
