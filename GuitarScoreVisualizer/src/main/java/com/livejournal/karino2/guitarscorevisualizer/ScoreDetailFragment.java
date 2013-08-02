package com.livejournal.karino2.guitarscorevisualizer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A fragment representing a single Score detail screen.
 * This fragment is either contained in a {@link ScoreListActivity}
 * in two-pane mode (on tablets) or a {@link ScoreDetailActivity}
 * on handsets.
 */
public class ScoreDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Score mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScoreDetailFragment() {


    }

    int CHORD_IMAGE_WIDTH = 64;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            long itemID = getArguments().getLong(ARG_ITEM_ID);
            if(itemID == -1) {
                // TODO: implement here.
                // never comming?
                throw new RuntimeException("never comming?");
            } else {
                mItem = Database.getInstance(getActivity()).getScoreById(itemID);
            }
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float dpi = (metrics.xdpi + metrics.ydpi) / 2;
        if (dpi < 10) dpi = 10;

        float imageWidthCm = 0.85f;
        CHORD_IMAGE_WIDTH =  (int)(dpi * imageWidthCm / 2.54f) + 1;
    }

    public static void highQualityStretch( Bitmap src, Bitmap dest )
    {
        dest.eraseColor( 0 );

        Canvas canvas = new Canvas( dest );
        Paint paint = new Paint();
        paint.setFilterBitmap( true );

        Matrix m = new Matrix();
        m.postScale( (float)dest.getWidth()/src.getWidth(), (float)dest.getHeight()/src.getHeight() );
        canvas.drawBitmap( src, m, paint );
    }

    private Bitmap floatResource( int id, int w, int h )
    {
        Bitmap dest = Bitmap.createBitmap( w, h, Bitmap.Config.ARGB_8888 );
        Bitmap bmp = BitmapFactory.decodeResource(getActivity().getResources(), id);
        highQualityStretch( bmp, dest );
        return dest;
    }


    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_score_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.score_detail_title)).setText(mItem.getTitle());
            ((TextView) rootView.findViewById(R.id.score_detail_score)).setText(mItem.getEncodedTexts());

            rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    tableLayoutReady = true;
                    formatTableIfReady();
                }
            });

        }


        if(!chordsReady()) {
            startParseChord();
        }

        return rootView;
    }

    private boolean chordsReady() {
        return mItem.getChords() != null;
    }

    Handler handler = new Handler();
    ScoreParser parser;
    ScoreParser getParser() {
        if(parser == null)
           parser = new ScoreParser();
        return parser;
    }
    private void startParseChord() {
        new Thread(){
            public void run() {
                ScoreParser parser = getParser();
                List<Chord> chords = parser.parseAll(mItem.getTexts());
                final List<Integer> chordInts = toInts(chords);
                handler.post(new Runnable(){
                    @Override
                    public void run() {
                        onChordsComming(chordInts);
                    }
                });
            }
        }.start();
    }

    private List<Integer> toInts(List<Chord> chords) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        for(Chord chord: chords) {
            res.add(chord.encodeToInt());
        }
        return res;
    }

    private void onChordsComming(List<Integer> chordInts) {
        mItem.setChords(chordInts);
        formatTableIfReady();
    }

    public static class UniqueCodeWrapper implements Iterable<Chord> {
        List<Chord> chords;
        public UniqueCodeWrapper(List<Chord> chrds) {
            chords = chrds;
        }

        @Override
        public Iterator<Chord> iterator() {
            return new UniqueChordIterator(chords);
        }
    }

    public static class UniqueChordIterator implements Iterator<Chord> {
        List<Chord> chords;
        HashMap<Chord, Boolean> map;
        int pos;
        public UniqueChordIterator(List<Chord> chrds) {
            chords = chrds;
            map = new HashMap<Chord, Boolean>();
            pos =0;
        }

        @Override
        public boolean hasNext() {
            if(pos == chords.size())
                return false;
            return true;
        }

        private boolean alreadyShown(Chord chord) {
            if(map.containsKey(chord))
                return true;
            return false;
        }

        @Override
        public Chord next() {
            Chord chord = chords.get(pos);
            map.put(chord, true);
            for(pos = pos+1; pos < chords.size(); pos++) {
                Chord tmp = chords.get(pos);
                if(!alreadyShown(tmp))
                    break;
            }
            return chord;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    HashMap<Chord, Integer> chordResourceMap;
    int lookupResourceId(Chord chord) {
        if(chordResourceMap == null) {
            chordResourceMap = new HashMap<Chord, Integer>();
            chordResourceMap.put(new Chord(Chord.BASE_A, Chord.MODIFIER_MAJOR), R.drawable.chords_a_0);
            chordResourceMap.put(new Chord(Chord.BASE_A, Chord.MODIFIER_SEVENS), R.drawable.chords_a7_0);
            chordResourceMap.put(new Chord(Chord.BASE_A, Chord.MODIFIER_MINOR), R.drawable.chords_am_0);
            chordResourceMap.put(new Chord(Chord.BASE_A, Chord.MODIFIER_MINORSEVENS), R.drawable.chords_am7_0);
            chordResourceMap.put(new Chord(Chord.BASE_B, Chord.MODIFIER_MINOR), R.drawable.chords_bm_0);
            chordResourceMap.put(new Chord(Chord.BASE_B, Chord.MODIFIER_MINORSEVENS), R.drawable.chords_bm7_0);
            chordResourceMap.put(new Chord(Chord.BASE_C, Chord.MODIFIER_MAJOR), R.drawable.chords_c_0);
            chordResourceMap.put(new Chord(Chord.BASE_C, Chord.MODIFIER_MINOR), R.drawable.chords_cm_0);
            chordResourceMap.put(new Chord(Chord.BASE_Cm_ON_G, Chord.MODIFIER_MAJOR), R.drawable.chords_cm_on_g_0);
            chordResourceMap.put(new Chord(Chord.BASE_D, Chord.MODIFIER_MAJOR), R.drawable.chords_d_0);
            chordResourceMap.put(new Chord(Chord.BASE_D, Chord.MODIFIER_SEVENSUSFOUR), R.drawable.chords_d7sus4_0);
            chordResourceMap.put(new Chord(Chord.BASE_D, Chord.MODIFIER_MINORSEVENS), R.drawable.chords_dm7_0);
            chordResourceMap.put(new Chord(Chord.BASE_E, Chord.MODIFIER_MINOR), R.drawable.chords_em_0);
            chordResourceMap.put(new Chord(Chord.BASE_G, Chord.MODIFIER_MAJOR), R.drawable.chords_g_0);
        }
        if(chordResourceMap.containsKey(chord))
            return chordResourceMap.get(chord);
        return R.drawable.chords_notready;
    }

    final int CELL_MARGIN = 40;

    boolean tableLayoutReady = false;
    private void formatTableIfReady() {
        if(!tableLayoutReady || !chordsReady())
            return;
        TableLayout tl = (TableLayout)rootView.findViewById(R.id.tableChords);

        tl.removeAllViews();
        int tableWidth = tl.getWidth();

        TableRow row = new TableRow(getActivity());
        tl.addView(row, new TableLayout.LayoutParams());
        UniqueCodeWrapper wrapper = new UniqueCodeWrapper(toChords(mItem.getChords()));
        int addedWidth = 0;
        for(Chord chord: wrapper) {
            ImageButton image = new ImageButton(getActivity());
            setChordResource(image, lookupResourceId(chord));
            row.addView(image, new TableRow.LayoutParams(0));
            addedWidth += CHORD_IMAGE_WIDTH+CELL_MARGIN;
            if(addedWidth +CHORD_IMAGE_WIDTH+CELL_MARGIN > tableWidth) {
                row = new TableRow(getActivity());
                tl.addView(row, new TableLayout.LayoutParams());
                addedWidth = 0;
            }
        }


        /*
        Log.d("GScoreV", "rootWidth:" + tableWidth + ", tableWidth: " + tl.getWidth());

        TableRow row = new TableRow(getActivity());
        ImageButton image = new ImageButton(getActivity());
        image.setImageResource(R.drawable.chords_a_0);

        row.addView(image, new TableRow.LayoutParams(0));

        image = new ImageButton(getActivity());
        image.setImageResource(R.drawable.chords_a7_0);

        row.addView(image, new TableRow.LayoutParams(1));

        tl.addView(row, new TableLayout.LayoutParams());


        row = new TableRow(getActivity());
        image = new ImageButton(getActivity());
        setChordResource(image, R.drawable.chords_bm7_0);

        row.addView(image, new TableRow.LayoutParams(0));

        image = new ImageButton(getActivity());
        setChordResource(image, R.drawable.chords_bm_0);

        row.addView(image, new TableRow.LayoutParams(1));

        tl.addView(row, new TableLayout.LayoutParams());
        */

                    /*

                    List<Integer> chords = mItem.getChords();
                    if(chords != null) {
                        for(int i = 0; i < chords.size(); i++) {

                        }
                    }
                    */
    }

    private List<Chord> toChords(List<Integer> chords) {
        ArrayList<Chord> res = new ArrayList<Chord>();
        for(int chordInt : chords) {
            res.add(Chord.decodeInt(chordInt));
        }
        return res;
    }


    private void setChordResource(ImageButton image, int rid) {
        image.setImageBitmap(floatResource(rid, CHORD_IMAGE_WIDTH, CHORD_IMAGE_WIDTH));
    }
}
