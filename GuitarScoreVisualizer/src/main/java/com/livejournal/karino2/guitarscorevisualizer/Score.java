package com.livejournal.karino2.guitarscorevisualizer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by karino on 7/26/13.
 */
public class Score {
    long id = -1;
    private Date created;
    private String title;
    private List<String> texts;
    private List<Integer> chords;

    public Score(long id, Date createdDate, String inTitle, List<String> inTexts) {
        setId(id);
        setCreated(createdDate);
        setTitle(inTitle);
        setTexts(inTexts);
        setChords(null);
    }

    public Score(long id, Date createdDate, String inTitle, String inTexts) {
        this(id, createdDate, inTitle, decodeTexts(inTexts));
    }

    public Score(Date createdDate, String inTitle, List<String> inTexts) {
        this(-1, createdDate, inTitle, inTexts);
    }

    public Score(Date createdDate, String inTitle, String inTexts) {
        this(createdDate, inTitle, decodeTexts(inTexts));
    }

    private static ArrayList<String> decodeTexts(String inTexts) {
        ArrayList<String> texts = new ArrayList<String>();
        int pos = 0;
        int index = inTexts.indexOf('\n', pos);
        while(index != -1) {
            texts.add(inTexts.substring(pos, index));
            pos = index+1;
            index = inTexts.indexOf('\n', pos);
        }
        texts.add(inTexts.substring(pos));
        return texts;
    }


    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTexts() {
        return texts;
    }

    public String getEncodedTexts() {
        StringBuilder builder = new StringBuilder();
        for(String text : texts) {
            if(builder.length() != 0)
                builder.append("\n");
            builder.append(text);
        }
        return builder.toString();
    }

    public String getEncodedChordList() {
        return "";
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }

    public List<Integer> getChords() {
        return chords;
    }

    public void setChords(List<Integer> chords) {
        this.chords = chords;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
