package com.livejournal.karino2.guitarscorevisualizer.test;

import com.livejournal.karino2.guitarscorevisualizer.Chord;
import com.livejournal.karino2.guitarscorevisualizer.ScoreParser;

import junit.framework.TestCase;

import java.util.List;

/**
 * Created by karino on 7/30/13.
 */
public class ScoreParserTest extends TestCase {
    public void testChordEquals() {
        Chord chordOne = new Chord(Chord.BASE_C, Chord.MODIFIER_MAJOR);
        Chord chordTwo = new Chord(Chord.BASE_C, Chord.MODIFIER_MAJOR);
        assertEquals(chordOne, chordTwo);
        assertTrue(chordOne.equals(chordTwo));
        // NG, as expected assertTrue(chordOne == chordTwo);
    }

    public void testParseOneLineForMatches_Empty() {
        String input = "";
        List<ScoreParser.MatchResult> res = parseOneLineForMatches(input);
        assertEquals(0, res.size());
    }

    private List<ScoreParser.MatchResult> parseOneLineForMatches(String input) {
        ScoreParser parser = new ScoreParser();
        return parser.parseOneLineForMatches(input);
    }

    public void testParseOneLineForMatches_NotACode() {
        List<ScoreParser.MatchResult> res = parseOneLineForMatches("hello");
        assertEquals(0, res.size());
    }

    public void testParseOneLineForMatches_C() {
        List<ScoreParser.MatchResult> res = parseOneLineForMatches(" C ");
        assertEquals(1, res.size());
        ScoreParser.MatchResult mr = res.get(0);
        assertEquals(1, mr.patternIndex);
        assertEquals(1, mr.matcher.start(1));
        assertEquals(2, mr.matcher.end(1));
    }

    public void testParseOneLineForMatches_GC() {
        List<ScoreParser.MatchResult> res = parseOneLineForMatches(" G C ");
        assertEquals(2, res.size());
        ScoreParser.MatchResult mr = res.get(0);
        assertEquals(2, mr.patternIndex);
        assertEquals(1, mr.matcher.start(1));
        assertEquals(2, mr.matcher.end(1));

        mr = res.get(1);
        assertEquals(1, mr.patternIndex);
        assertEquals(3, mr.matcher.start(1));
        assertEquals(4, mr.matcher.end(1));
    }

    public void testParseOneLineForMatches_CG() {
        List<ScoreParser.MatchResult> res = parseOneLineForMatches(" C G ");
        assertEquals(2, res.size());
        ScoreParser.MatchResult mr = res.get(0);
        assertEquals(1, mr.patternIndex);
        assertEquals(1, mr.matcher.start(1));
        assertEquals(2, mr.matcher.end(1));

        mr = res.get(1);
        assertEquals(2, mr.patternIndex);
        assertEquals(3, mr.matcher.start(1));
        assertEquals(4, mr.matcher.end(1));
    }
}
