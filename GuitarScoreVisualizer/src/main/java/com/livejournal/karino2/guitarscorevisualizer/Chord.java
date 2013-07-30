package com.livejournal.karino2.guitarscorevisualizer;

/**
 * Created by karino on 7/30/13.
 */
public class Chord {
    public static final int BASE_C = 1;
    public static final int BASE_C_SHARP = 2;
    public static final int BASE_D = 3;
    public static final int BASE_D_SHARP = 4;
    public static final int BASE_E = 5;
    public static final int BASE_F = 6;
    public static final int BASE_F_SHARP = 7;
    public static final int BASE_G = 8;
    public static final int BASE_G_SHARP = 9;
    public static final int BASE_A = 10;
    public static final int BASE_A_SHARP = 11;
    public static final int BASE_B = 12;

    public static final int MODIFIER_MAJOR = 1;
    public static final int MODIFIER_MINOR = 2;
    public static final int MODIFIER_MINORSEVENS = 3;
    public static final int MODIFIER_MAJORSEVENS = 4;
    public static final int MODIFIER_SEVENS = 5;
    public static final int MODIFIER_SUSFOUR = 6;
    public static final int MODIFIER_ADDNINE = 7;
    public static final int MODIFIER_DIM = 8;
    public static final int MODIFIER_AUG = 9;
    public static final int MODIFIER_MINORSEVEN_FLATFIVE = 10;
    public static final int MODIFIER_SIX = 11;
    public static final int MODIFIER_MINORSIX = 12;
    public static final int MODIFIER_MINOR_MAJORSEVENS = 13;


    int baseTone;
    int modifier;
    public Chord(int baseVal, int modVal) {
        baseTone = baseVal;
        modifier = modVal;
    }

    public int getBase() {
        return baseTone;
    }

    public int getModifier() {
        return modifier;
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == null) return false;
        if (ob.getClass() != getClass()) return false;
        Chord that = (Chord)ob;

        return getBase() == that.getBase() &&
                getModifier() == that.getModifier();
    }

    @Override
    public int hashCode() {
        return ((Integer)getBase()).hashCode() ^ ((Integer)getModifier()).hashCode();
    }

}
