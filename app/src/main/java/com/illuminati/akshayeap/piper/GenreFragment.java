package com.illuminati.akshayeap.piper;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.jmusixmatch.MusixMatchException;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;

import songExtractor.Callback;
import songExtractor.SongExtractor;


public class GenreFragment extends android.app.Fragment {

    LinearLayout ll;
    TextView preText;
    TextView genreText;
    ProgressDialog progressDialog;

    private String mFileName;

    public SongExtractor songExtractor;

    private String genre;

    public GenreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ll=new LinearLayout(this.getActivity());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setBackgroundResource(R.color.green);
        ll.setGravity(Gravity.CENTER);
        preText = new TextView(this.getActivity());
        preText.setText("Genre:");
        preText.setTextSize(20);
        preText.setTextColor(getResources().getColor(R.color.black));
        ll.addView(preText);
        genreText=new TextView(this.getActivity());
        genreText.setText(genre);
        genreText.setTextSize(40);
        genreText.setTextColor(getResources().getColor(R.color.black));
        ll.addView(genreText);
        return ll;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //convert();
    }


    public void prepare(String filename, String genre) {
        mFileName=filename; this.genre=genre;
    }

    private void log(String text) {

        Toast sent = Toast.makeText(this.getView().getContext(),text, Toast.LENGTH_SHORT);
        sent.show();

    }

}
