package com.livejournal.karino2.guitarscorevisualizer.test;

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
}
