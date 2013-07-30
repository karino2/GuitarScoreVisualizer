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
        public Matcher matcher;
    }

    public List<MatchResult> parseOneLineForMatches(String line) {
        ArrayList<MatchResult> res = new ArrayList<MatchResult>();
        for(int i = 0; i < chordsPat.size(); i++) {
            Pattern pat = chordsPat.get(i);
            Matcher matcher = pat.matcher(line);
            if(matcher.find()) {
                MatchResult mr = new MatchResult();
                mr.patternIndex = i;
                mr.matcher = matcher;
                res.add(mr);
            }
        }
        Collections.sort(res, new Comparator<MatchResult>() {
            @Override
            public int compare(MatchResult matchResult, MatchResult matchResult2) {
                return matchResult.matcher.start(1) - matchResult2.matcher.start(1);
            }
        });
        return res;
    }

}
