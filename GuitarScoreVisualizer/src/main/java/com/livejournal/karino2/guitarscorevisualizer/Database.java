package com.livejournal.karino2.guitarscorevisualizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by karino on 7/26/13.
 */
public class Database {
    private static final String DATABASE_NAME = "guitar_score_visualizer.db";
    private static final int DATABASE_VERSION = 1;
    private static final String SCORE_TABLE_NAME = "score";

    public Score getScoreById(long id) {
        Cursor cursor = database.query(SCORE_TABLE_NAME, new String[] {"_id", "DATE", "TITLE", "SCORE", "CHORDLIST"}, "_id = ?", new String[] { String.valueOf(id) }, null, null, null);
        cursor.moveToFirst();
        Score score = new Score(cursor.getLong(0), new Date(cursor.getLong(1)), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        cursor.close();
        return score;

    }

    public void deleteScore(long id) {
        database.delete(SCORE_TABLE_NAME, "_id=?", new String[]{ String.valueOf(id) });
    }

    public void saveScore(Score score) {
        if(score.getId() == -1)
            insertScore(score);
        else
            updateScore(score);
    }

    public void insertScoreDto(ScoreDto dto) {
        ContentValues values = new ContentValues();
        values.put("DATE", dto.date);
        values.put("TITLE", dto.title);
        values.put("SCORE", dto.score);
        values.put("CHORDLIST", dto.chordList);
        database.insert(SCORE_TABLE_NAME, null, values);
    }

    static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + SCORE_TABLE_NAME + " ("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "DATE INTEGER," // date created.
                    + "TITLE TEXT,"
                    + "SCORE TEXT,"
                    + "CHORDLIST TEXT"
                    + ");");


        }

        public void recreate(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + SCORE_TABLE_NAME);
            onCreate(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            recreate(sqLiteDatabase);
        }


    }

    public static synchronized Database getInstance(Context ctx) {
        if(sInstance == null) {
            sInstance = new Database();
            sInstance.open(ctx);
        }
        return sInstance;
    }

    static Database sInstance = null;
    private Database(){}



    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    public void open(Context context) {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void recreate() {
        dbHelper.recreate(database);
    }

    public Cursor getScoreInfos() {
        return database.query(SCORE_TABLE_NAME, new String[] {"_id", "DATE", "TITLE", "SCORE"}, null, null, null, null, "DATE DESC, _id DESC");
    }

    public void insertScore(Score score) {
        ContentValues values = toContentValues(score);
        database.insert(SCORE_TABLE_NAME, null, values);
    }

    public void updateScore(Score score) {
        ContentValues values = toContentValues(score);
        database.update(SCORE_TABLE_NAME, values, "_id=?", new String[]{String.valueOf(score.getId())});
    }

    private ContentValues toContentValues(Score score) {
        ContentValues values = new ContentValues();
        values.put("DATE", score.getCreated().getTime());
        values.put("TITLE", score.getTitle());
        values.put("SCORE", score.getEncodedTexts());
        values.put("CHORDLIST", score.getEncodedChordList());
        return values;
    }

    public static class ScoreDto {
        public long date;
        public String title;
        public String score;
        public String chordList;
        ScoreDto() {}
    }

    //         Score score = new Score(cursor.getLong(0), new Date(cursor.getLong(1)), cursor.getString(2), cursor.getString(3), cursor.getString(4));
    public ScoreDto toDto(Cursor cursor) {
        ScoreDto dto = new ScoreDto();
        dto.date = cursor.getLong(1);
        dto.title = cursor.getString(2);
        dto.score = cursor.getString(3);
        dto.chordList = cursor.getString(4);
        return dto;
    }

    public Cursor retrieveAllForSerialize() {
        return database.query(SCORE_TABLE_NAME, new String[] {"_id", "DATE", "TITLE", "SCORE", "CHORDLIST"}, null, null, null, null, "DATE DESC, _id DESC");
    }

    private boolean contains(long[] idSet, long testId) {
        for(long id : idSet) {
            if(id == testId)
                return true;
        }
        return false;
    }

    public File exportAllToJson() throws IOException {
        return exportToJson(null);
    }

    // if exportIds == null, export all.
    public File exportToJson(long[] exportIds) throws IOException {
        Gson gson = new Gson();

        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSS");
        String filename = timeStampFormat.format(new Date()) + ".json";
        File file = new File(Environment.getExternalStorageDirectory(), filename);


        ArrayList<ScoreDto> scoreList = new ArrayList<Database.ScoreDto>();

        Cursor cursor = retrieveAllForSerialize();
        try {
            if(!cursor.moveToFirst())
            {
                return null;
            }
            do {
                if(exportIds == null || contains(exportIds, cursor.getLong(0)))
                    scoreList.add(toDto(cursor));
            }while(cursor.moveToNext());
        }finally {
            cursor.close();
        }

        FileWriter writer = new FileWriter(file);
        // BufferedWriter bw = new BufferedWriter(new FileWriter(file), 8*1024);
        gson.toJson(scoreList, writer);
        writer.close();
        return file;
    }


}