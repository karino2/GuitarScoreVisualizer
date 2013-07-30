package com.livejournal.karino2.guitarscorevisualizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by karino on 7/30/13.
 */
public class ScoreParser {
    String[] chordsPatText = {"Cm/G", "C", "G"};
    List<Pattern> chordsPat = new ArrayList<Pattern>();

    public ScoreParser() {
        for(String txtPat : chordsPatText) {
            chordsPat.add(Pattern.compile("[ \\|](" + txtPat+ ")[ \\|]"));
        }
    }

    public static class MatchResult {
        public int patternIndex;
        public int start;
        public int end;
    }

    public List<Chord> parseOneLine(String line) {
        List<MatchResult> mrs = parseOneLineForMatches(line);
        return matchResultListToChordList(mrs);
    }

    List<Chord> matchResultListToChordList(List<MatchResult> matches) {
        ArrayList<Chord> res = new ArrayList<Chord>();
        for(MatchResult match : matches) {
            res.add(matchResultToChord(match));
        }
        return res;
    }

    Chord matchResultToChord(MatchResult match) {
        switch(match.patternIndex) {
            case 0:
                return new Chord(Chord.BASE_Cm_ON_G, Chord.MODIFIER_MAJOR);
            case 1:
                return new Chord(Chord.BASE_C, Chord.MODIFIER_MAJOR);
            case 2:
                return new Chord(Chord.BASE_G, Chord.MODIFIER_MAJOR);
        }
        throw new IllegalArgumentException();
    }

    public List<MatchResult> parseOneLineForMatches(String line) {
        ArrayList<MatchResult> res = new ArrayList<MatchResult>();
        for(int i = 0; i < chordsPat.size(); i++) {
            Pattern pat = chordsPat.get(i);
            Matcher matcher = pat.matcher(line);
            int from = 0;
            while(matcher.find(from)) {
                MatchResult mr = new MatchResult();
                mr.patternIndex = i;
                mr.start = matcher.start(1);
                mr.end = matcher.end(1);
                res.add(mr);
                from = matcher.end(1)+1;
            }
        }
        Collections.sort(res, new Comparator<MatchResult>() {
            @Override
            public int compare(MatchResult matchResult, MatchResult matchResult2) {
                return matchResult.start - matchResult2.start;
            }
        });
        return res;
    }

}
