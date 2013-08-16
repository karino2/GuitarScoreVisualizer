package com.livejournal.karino2.guitarscorevisualizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by karino on 7/30/13.
 */
public class Chord {
    public static final int MODIFIER_MAJOR = 0;
    public static final int MODIFIER_MINOR = 1;
    public static final int MODIFIER_MINORSEVENS = 2;
    public static final int MODIFIER_MAJORSEVENS = 3;
    public static final int MODIFIER_SEVENS = 4;
    public static final int MODIFIER_SUSFOUR = 5;
    public static final int MODIFIER_ADDNINE = 6;
    public static final int MODIFIER_SEVENSUSFOUR = 7;
    public static final int MODIFIER_DIM = 8;
    public static final int MODIFIER_AUG = 9;
    public static final int MODIFIER_MINORSEVEN_FLATFIVE = 10;
    public static final int MODIFIER_SIX = 11;
    public static final int MODIFIER_MINORSIX = 12;
    public static final int MODIFIER_MINOR_MAJORSEVENS = 13;
    public static final int MODIFIER_NUM = 14;

    public static final int BASE_C = 0;
    public static final int BASE_C_SHARP = 1;
    public static final int BASE_D = 2;
    public static final int BASE_D_SHARP = 3;
    public static final int BASE_E = 4;
    public static final int BASE_F = 5;
    public static final int BASE_F_SHARP = 6;
    public static final int BASE_G = 7;
    public static final int BASE_G_SHARP = 8;
    public static final int BASE_A = 9;
    public static final int BASE_A_SHARP = 10;
    public static final int BASE_B = 11;
    public static final int BASE_NUM = 12;

    // These have to match to fracPatText
    public static final int BASE_FRAC_BEGIN = BASE_NUM;
    public static final int BASE_Cm_ON_G = BASE_FRAC_BEGIN;
    public static final int BASE_G_ON_D = BASE_FRAC_BEGIN+1;
    public static final int BASE_A_PLUS_F = BASE_FRAC_BEGIN+2;
    public static final int BASE_C_ON_E = BASE_FRAC_BEGIN+3;
    public static final int BASE_Am_ON_C = BASE_FRAC_BEGIN+4;
    public static final int BASE_FMASEVEN_ON_C = BASE_FRAC_BEGIN+5;
    public static final int BASE_G_ON_B = BASE_FRAC_BEGIN+6;
    public static final int BASE_FSHARPMINORSEVENS_ON_B = BASE_FRAC_BEGIN+7;
    public static final int BASE_Em7_ON_A = BASE_FRAC_BEGIN+8;
    public static final int BASE_FRAC_END = BASE_FRAC_BEGIN+8;




    final static String[] basePatText = { "C", "C#", "Db", "D", "D#", "Eb", "E", "F", "F#", "Gb", "G", "G#", "Ab", "A", "A#", "Bb", "B"};
    final static String[] modPatText =  { "", "m", "m7", "M7", "7", "sus4", "add9", "7sus4", "dim", "aug", "m7-5", "6", "m6", "mM7"};
    final static String[] fracPatText= {"Cm/G", "G/D", "A\\+F", "C/E", "Am/C", "FM7/C", "G/B", "F#m7/B", "Em7/A"};
    final static Integer[] patToChordIndexTable = {0, 1, 1, 2, 3, 3, 4, 5, 6, 6, 7, 8, 8, 9, 10, 10, 11};

    public static Chord patIndexToChord(int patIndex) {
        int base = patIndex/MODIFIER_NUM;
        if(base >= patToChordIndexTable.length)
            return new Chord(BASE_FRAC_BEGIN+ patIndex - patToChordIndexTable.length*MODIFIER_NUM, MODIFIER_MAJOR);

        base = patToChordIndexTable[base];
        int mod = patIndex%MODIFIER_NUM;
        return new Chord(base, mod);
    }

    public static List<String> chordsText() {
        ArrayList<String> chords = new ArrayList<String>();
        for(String base: basePatText) {
            for(String mod : modPatText) {
                chords.add(base +mod);
            }
        }
        for(int fracIndex = BASE_FRAC_BEGIN; fracIndex <= BASE_FRAC_END; fracIndex++ ) {
            chords.add(fracPatText[fracIndex-BASE_FRAC_BEGIN]);
        }
        return chords;
    }

    public int encodeToInt() {
        return 0x10000*alternate+0x100*modifier+baseTone;
    }

    public static Chord decodeInt(int encodedInt) {
        int base = encodedInt & 0xff;
        int mod = (0xff00&encodedInt)/0x100;
        int alternate = encodedInt/0x10000;
        return new Chord(base, mod, alternate);
    }


    int baseTone;
    int modifier;
    int alternate;

    public final static int ALTERNATE_HICODE = 1;
    public Chord(int baseVal, int modVal, int alternate) {
        baseTone = baseVal;
        modifier = modVal;
        this.alternate = alternate;
    }

    public Chord(int baseVal, int modVal) {
        this(baseVal, modVal, 0);
    }

    public int getBase() {
        return baseTone;
    }

    public int getModifier() {
        return modifier;
    }

    // hicode, power code, etc.
    public int getAlternate() {
        return alternate;
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == null) return false;
        if (ob.getClass() != getClass()) return false;
        Chord that = (Chord)ob;

        return getBase() == that.getBase() &&
                getModifier() == that.getModifier() &&
                getAlternate() == that.getAlternate();
    }

    @Override
    public int hashCode() {
        return ((Integer)encodeToInt()).hashCode();
    }

}
