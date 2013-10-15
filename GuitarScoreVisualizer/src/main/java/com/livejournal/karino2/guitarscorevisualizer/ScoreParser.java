package com.livejournal.karino2.guitarscorevisualizer;

import android.util.Log;

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
    List<Pattern> chordsPat = new ArrayList<Pattern>();


    public ScoreParser() {
        for(String txtPat : Chord.chordsText()) {
            chordsPat.add(Pattern.compile("(^|[(\\uFF5C \\|])(" + txtPat+ ")([\\uFF5C \\|]|$)"));
        }
    }

    public List<Chord> parseAll(List<String> texts) {
        ArrayList<Chord> res = new ArrayList<Chord>();
        for(String line : texts) {
            List<Chord> oneRes = parseOneLine(line);
            res.addAll(oneRes);
        }
        return res;
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
        return Chord.patIndexToChord(match.patternIndex);
    }

    public List<MatchResult> parseOneLineForMatches(String line) {
        ArrayList<MatchResult> res = new ArrayList<MatchResult>();
        for(int i = 0; i < chordsPat.size(); i++) {
            Pattern pat = chordsPat.get(i);
            Matcher matcher = pat.matcher(line);
            int from = 0;
            try {
                while(matcher.find(from)) {
                    MatchResult mr = new MatchResult();
                    mr.patternIndex = i;
                    mr.start = matcher.start(2);
                    mr.end = matcher.end(2);
                    res.add(mr);
                    from = matcher.end(2)+1;
                }
            }catch(IndexOutOfBoundsException e) {
                // I don't know why this situation happen, but there is error report.
                // So I just ignore this exception because this is vital while ignore is only missing last chord at worst.
                Log.d("GuitarScoreVisualizer", "Unknown exception case: " + e.getMessage());
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
