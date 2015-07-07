package com.livejournal.karino2.guitarscorevisualizer.test;

import com.livejournal.karino2.guitarscorevisualizer.Chord;
import com.livejournal.karino2.guitarscorevisualizer.Score;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by karino on 7/26/13.
 */
public class ScoreTest extends TestCase {
    public void testEncodedTexts() {
        ArrayList<String> texts = new ArrayList<String>();
        texts.add("Hello");
        texts.add("World");
        Score score = new Score(new Date(), "Test title",  texts);
        assertEquals(score.getEncodedTexts(), "Hello\nWorld");
    }

    public void testDecodeTexts() {
        Score score = new Score(new Date(), "Test title", "Hello\nWorld\nThird\n");
        List<String> texts = score.getTexts();
        assertEquals(4, texts.size());
        assertEquals("Hello", texts.get(0));
        assertEquals("World", texts.get(1));
        assertEquals("Third", texts.get(2));
        assertEquals("", texts.get(3));
    }

    public void testEncodeDecodeChords() {
        Chord expect1 = new Chord(Chord.BASE_G, Chord.MODIFIER_MAJOR);
        Chord expect2 = new Chord(Chord.BASE_G, Chord.MODIFIER_MAJOR, Chord.ALTERNATE_HICODE);
        Chord expect3 = new Chord(Chord.BASE_B, Chord.MODIFIER_MINOR_MAJORSEVENS);
        Chord expect4 = new Chord(Chord.BASE_Cm_ON_G, Chord.MODIFIER_MAJOR);

        Score score = new Score(new Date(), "Not used", "Not used");
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids.add(expect1.encodeToInt());
        ids.add(expect2.encodeToInt());
        ids.add(expect3.encodeToInt());
        ids.add(expect4.encodeToInt());
        score.setChords(ids);
        Score score2 = new Score(-1, new Date(), "Not used", "Not used", score.getEncodedChordList());
        assertEquals(expect1, Chord.decodeInt(score2.getChords().get(0)));
        assertEquals(expect2, Chord.decodeInt(score2.getChords().get(1)));
        assertEquals(expect3, Chord.decodeInt(score2.getChords().get(2)));
        assertEquals(expect4, Chord.decodeInt(score2.getChords().get(3)));
    }
}
