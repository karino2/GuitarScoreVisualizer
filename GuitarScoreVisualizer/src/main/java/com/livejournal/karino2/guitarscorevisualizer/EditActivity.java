package com.livejournal.karino2.guitarscorevisualizer;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.EditText;

import java.util.Date;

public class EditActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveScoreIfNecessary();
    }

    private void saveScoreIfNecessary() {
        EditText etTitle = (EditText)findViewById(R.id.editTextTitle);
        EditText etScore = (EditText)findViewById(R.id.editTextScore);

        Score score = new Score(new Date(), etTitle.getText().toString(), etScore.getText().toString());
        Database.getInstance(this).insertScore(score);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }
    
}
