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

    public void testParseOneLine_RealData() {
        // List<Chord> res = parseOneLine("[3]｜Em｜A7｜Am7 Bm7｜Cm D｜");
        List<Chord> res = parseOneLine("[3]\uFF5CEm\uFF5CA7\uFF5CAm7 Bm7\uFF5CCm D\uFF5C");
        assertEquals(6, res.size());
    }

    public void testMakeChordText() {
        veryMakeChordText(Chord.BASE_C, Chord.MODIFIER_MAJOR, "C");
        veryMakeChordText(Chord.BASE_C, Chord.MODIFIER_MINORSEVEN_FLATFIVE, "Cm7-5");
        veryMakeChordText(Chord.BASE_C, Chord.MODIFIER_MINOR_MAJORSEVENS, "CmM7");
        veryMakeChordText(Chord.BASE_C_SHARP, Chord.MODIFIER_MAJOR, "C#");
        veryMakeChordText(Chord.BASE_B, Chord.MODIFIER_MINOR_MAJORSEVENS, "BmM7");
    }

    private void veryMakeChordText(int baseIndex, int modIndex, String expect) {
        String res = Chord.makeChordText(baseIndex, modIndex);
        assertEquals(expect, res);
    }

    public void testPatIndexToChord_C() {
        // 0 C
        // 1 Cm
        // 2 Cm7
        // 0+MODIFIER_NUM-1 CmM7
        // 1*MODIFIER_NUM+0 C#
        // 1*MODIFIER_NUM+1 C#m

        verifyPatIndexToChord(new Chord(Chord.BASE_C, Chord.MODIFIER_MAJOR), 0);
        verifyPatIndexToChord(new Chord(Chord.BASE_C, Chord.MODIFIER_MINOR), 1);
        verifyPatIndexToChord(new Chord(Chord.BASE_C, Chord.MODIFIER_MINOR_MAJORSEVENS), Chord.MODIFIER_NUM-1);
        verifyPatIndexToChord(new Chord(Chord.BASE_C_SHARP, Chord.MODIFIER_MAJOR), Chord.MODIFIER_NUM);
        verifyPatIndexToChord(new Chord(Chord.BASE_B, Chord.MODIFIER_MINOR_MAJORSEVENS), Chord.BASE_NUM*Chord.MODIFIER_NUM-1);
        verifyPatIndexToChord(new Chord(Chord.BASE_Cm_ON_G, Chord.MODIFIER_MAJOR), Chord.BASE_NUM*Chord.MODIFIER_NUM);
    }

    private void verifyPatIndexToChord(Chord expect, int inputPatIndex) {
        Chord actual = Chord.patIndexToChord(inputPatIndex);
        assertEquals(expect, actual);
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
