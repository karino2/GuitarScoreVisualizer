package com.livejournal.karino2.guitarscorevisualizer;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

public class EditActivity extends Activity {
    public static final String ARG_ITEM_ID = "item_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ((Button)findViewById(R.id.buttonSave)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveScoreIfNecessary();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
        ((Button)findViewById(R.id.buttonCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        long id = getIntent().getLongExtra(ARG_ITEM_ID, -1);
        if(id == -1) {
            score = new Score(new Date(), "", "");
        } else {
            score = Database.getInstance(this).getScoreById(id);
            EditText etTitle = (EditText)findViewById(R.id.editTextTitle);
            EditText etScore = (EditText)findViewById(R.id.editTextScore);
            etTitle.setText(score.getTitle());
            etScore.setText(score.getEncodedTexts());

        }
    }

    Score score;


    private void saveScoreIfNecessary() {
        EditText etTitle = (EditText)findViewById(R.id.editTextTitle);
        EditText etScore = (EditText)findViewById(R.id.editTextScore);

        score.setTitle(etTitle.getText().toString());
        score.setTextsString(etScore.getText().toString());
        score.setModifiedAt(new Date());
        score.setChords(null);

        Database.getInstance(this).saveScore(score);
    }


}
