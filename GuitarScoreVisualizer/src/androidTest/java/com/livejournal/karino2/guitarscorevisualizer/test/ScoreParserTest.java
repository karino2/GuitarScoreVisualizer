package com.livejournal.karino2.guitarscorevisualizer.test;

import android.util.Log;

import com.livejournal.karino2.guitarscorevisualizer.Chord;
import com.livejournal.karino2.guitarscorevisualizer.ScoreDetailFragment;
import com.livejournal.karino2.guitarscorevisualizer.ScoreParser;

import junit.framework.TestCase;

import java.util.ArrayList;
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
        // This test is fragile, I decide not maintain.
        // assertEquals(1, mr.patternIndex);
        assertEquals(1, mr.start);
        assertEquals(2, mr.end);
    }

    public void testParseOneLineForMatches_GC() {
        List<ScoreParser.MatchResult> res = parseOneLineForMatches(" G C ");
        assertEquals(2, res.size());
        ScoreParser.MatchResult mr = res.get(0);
        // assertEquals(2, mr.patternIndex);
        assertEquals(1, mr.start);
        assertEquals(2, mr.end);

        mr = res.get(1);
        // assertEquals(1, mr.patternIndex);
        assertEquals(3, mr.start);
        assertEquals(4, mr.end);
    }

    public void testParseOneLineForMatches_CG() {
        List<ScoreParser.MatchResult> res = parseOneLineForMatches(" C G ");
        assertEquals(2, res.size());
        ScoreParser.MatchResult mr = res.get(0);
        // assertEquals(1, mr.patternIndex);
        assertEquals(1, mr.start);
        assertEquals(2, mr.end);

        mr = res.get(1);
        // assertEquals(2, mr.patternIndex);
        assertEquals(3, mr.start);
        assertEquals(4, mr.end);
    }

    public void testParseOneLineForMatches_CGC() {
        List<ScoreParser.MatchResult> res = parseOneLineForMatches(" C G C ");
        assertEquals(3, res.size());
        // assertEquals(1, res.get(2).patternIndex);
    }

    private List<Chord> parseOneLine(String input) {
        ScoreParser parser = new ScoreParser();
        return parser.parseOneLine(input);
    }

    public void testParseOneLine_Empty() {
        List<Chord> res = parseOneLine("");
        assertEquals(0, res.size());
    }

    public void testParseOneLine_NoMatch() {
        List<Chord> res = parseOneLine(" Hello ");
        assertEquals(0, res.size());
    }

    public void testParseOneLine_C() {
        List<Chord> res = parseOneLine("|C|");
        assertEquals(1, res.size());
        assertEquals(new Chord(Chord.BASE_C, Chord.MODIFIER_MAJOR), res.get(0));
    }

    public void testParseOneLine_CGC() {
        List<Chord> res = parseOneLine("|C G|C|");
        assertEquals(3, res.size());
        assertEquals(new Chord(Chord.BASE_C, Chord.MODIFIER_MAJOR), res.get(0));
        assertEquals(new Chord(Chord.BASE_G, Chord.MODIFIER_MAJOR), res.get(1));
        assertEquals(new Chord(Chord.BASE_C, Chord.MODIFIER_MAJOR), res.get(2));
    }

    public void testParseOneLine_CmOnG() {
        List<Chord> res = parseOneLine("|Cm/G G|C|");
        assertEquals(3, res.size());
        assertEquals(new Chord(Chord.BASE_Cm_ON_G, Chord.MODIFIER_MAJOR), res.get(0));
    }

    public void testParseOneLine_ChordBorder() {
        verifyParseOneLine_OneChord("|C|", new Chord(Chord.BASE_C, Chord.MODIFIER_MAJOR));
        verifyParseOneLine_OneChord("|Cm|", new Chord(Chord.BASE_C, Chord.MODIFIER_MINOR));
        verifyParseOneLine_OneChord("|CmM7|", new Chord(Chord.BASE_C, Chord.MODIFIER_MINOR_MAJORSEVENS));
        verifyParseOneLine_OneChord("|C#|", new Chord(Chord.BASE_C_SHARP, Chord.MODIFIER_MAJOR));
        verifyParseOneLine_OneChord("|BmM7|", new Chord(Chord.BASE_B, Chord.MODIFIER_MINOR_MAJORSEVENS));
        verifyParseOneLine_OneChord("|Cm/G|", new Chord(Chord.BASE_Cm_ON_G, Chord.MODIFIER_MAJOR));
        verifyParseOneLine_OneChord("|A+F|", new Chord(Chord.BASE_A_PLUS_F, Chord.MODIFIER_MAJOR));
        verifyParseOneLine_OneChord("|Bb/A|", new Chord(Chord.BASE_A_SHARP_ON_A, Chord.MODIFIER_MAJOR));
        verifyParseOneLine_OneChord("|A#/A|", new Chord(Chord.BASE_A_SHARP_ON_A, Chord.MODIFIER_MAJOR));

    }

    private void verifyParseOneLine_OneChord(String input, Chord expect) {
        List<Chord> res = parseOneLine(input);
        assertEquals(1, res.size());
        assertEquals(expect, res.get(0));
    }

    public void testParseOneLine_RealData() {
        // List<Chord> res = parseOneLine("[3]｜Em｜A7｜Am7 Bm7｜Cm D｜");
        List<Chord> res = parseOneLine("[3]\uFF5CEm\uFF5CA7\uFF5CAm7 Bm7\uFF5CCm D\uFF5C");
        assertEquals(6, res.size());
    }

    public void testParseOneLine_EndShouldMatch() {
        verifyParseOneLine_OneChord("|C", new Chord(Chord.BASE_C, Chord.MODIFIER_MAJOR));
    }

    public void testParseOneLine_BeginShouldMatch() {
        verifyParseOneLine_OneChord("C|", new Chord(Chord.BASE_C, Chord.MODIFIER_MAJOR));
    }

    public void testChordEncodeDecodeInt() {
        verifyEncodeDecode(Chord.BASE_C, Chord.MODIFIER_MAJOR);
        verifyEncodeDecode(Chord.BASE_B, Chord.MODIFIER_AUG);
        verifyEncodeDecode(Chord.BASE_B, Chord.MODIFIER_MINOR_MAJORSEVENS);
        verifyEncodeDecode(Chord.BASE_Cm_ON_G, Chord.MODIFIER_MAJOR);
    }

    private void verifyEncodeDecode(int base, int mod) {
        Chord chord = new Chord(base, mod);
        Chord actual = Chord.decodeInt(chord.encodeToInt());
        assertEquals(chord, actual);
    }

    public void testUniqueChordIterator() {
        ArrayList<Chord> chords = new ArrayList<Chord>();
        chords.add(new Chord(Chord.BASE_C, Chord.MODIFIER_MAJOR));
        chords.add(new Chord(Chord.BASE_G, Chord.MODIFIER_MAJOR));
        chords.add(new Chord(Chord.BASE_C, Chord.MODIFIER_MAJOR));
        chords.add(new Chord(Chord.BASE_G, Chord.MODIFIER_MAJOR));
        chords.add(new Chord(Chord.BASE_C, Chord.MODIFIER_MINOR));

        ScoreDetailFragment.UniqueChordIterator iterator = new ScoreDetailFragment.UniqueChordIterator(chords);
        assertTrue(iterator.hasNext());
        assertEquals(new Chord(Chord.BASE_C, Chord.MODIFIER_MAJOR), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(new Chord(Chord.BASE_G, Chord.MODIFIER_MAJOR), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(new Chord(Chord.BASE_C, Chord.MODIFIER_MINOR), iterator.next());
        assertFalse(iterator.hasNext());

    }
}
