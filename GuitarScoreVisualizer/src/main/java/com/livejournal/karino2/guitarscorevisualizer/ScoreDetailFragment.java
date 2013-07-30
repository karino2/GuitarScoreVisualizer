package com.livejournal.karino2.guitarscorevisualizer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

    final int CHORD_IMAGE_WIDTH = 64;

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

                    TableLayout tl = (TableLayout)rootView.findViewById(R.id.tableChords);
                    appendChordsTableRows(tl);
                }
            });

        }



        return rootView;
    }

    private void appendChordsTableRows(TableLayout tl) {
        int tableWidth = tl.getWidth();
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
        image.setImageResource(R.drawable.chords_bm7_0);

        row.addView(image, new TableRow.LayoutParams(0));

        image = new ImageButton(getActivity());
        image.setImageResource(R.drawable.chords_bm_0);

        row.addView(image, new TableRow.LayoutParams(1));

        tl.addView(row, new TableLayout.LayoutParams());


                    /*

                    List<Integer> chords = mItem.getChords();
                    if(chords != null) {
                        for(int i = 0; i < chords.size(); i++) {

                        }
                    }
                    */
    }
}
