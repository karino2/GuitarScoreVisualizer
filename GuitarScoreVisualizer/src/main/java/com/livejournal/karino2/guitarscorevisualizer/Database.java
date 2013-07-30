package com.livejournal.karino2.guitarscorevisualizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

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
        Score score = new Score(cursor.getLong(0), new Date(cursor.getLong(1)), cursor.getString(2), cursor.getString(3));
        cursor.close();
        return score;

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
        ContentValues values = new ContentValues();
        values.put("DATE", score.getCreated().getTime());
        values.put("TITLE", score.getTitle());
        values.put("SCORE", score.getEncodedTexts());
        values.put("CHORDLIST", score.getEncodedChordList());
        database.insert(SCORE_TABLE_NAME, null, values);
    }
}