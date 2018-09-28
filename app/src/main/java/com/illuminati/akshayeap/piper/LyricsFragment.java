package com.illuminati.akshayeap.piper;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LyricsFragment extends android.app.Fragment {


    LinearLayout ll;
    TextView lyricText;
    TextView titleView;
    TextView artistView;
    private String lyrics;
    private String title;
    private String artist;

    public LyricsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ll=new LinearLayout(this.getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundResource(R.color.paleTurquoise);
        ll.setGravity(Gravity.CENTER);
        ll.setPadding(10,10,10,10);
        LinearLayout ll1=new LinearLayout(this.getActivity());
        ll1.setOrientation(LinearLayout.HORIZONTAL);
        ll1.setBackgroundResource(R.color.paleTurquoise);
        ll1.setGravity(Gravity.CENTER);
        ll.addView(ll1);
        titleView=new TextView(this.getActivity());
        titleView.setText(title);
        titleView.setTextColor(getResources().getColor(R.color.black));
        titleView.setTextSize(30);
        ll1.addView(titleView);
        LinearLayout ll2=new LinearLayout(this.getActivity());
        ll2.setOrientation(LinearLayout.HORIZONTAL);
        ll2.setBackgroundResource(R.color.paleTurquoise);
        ll2.setGravity(Gravity.CENTER);
        ll.addView(ll2);
        artistView=new TextView(this.getActivity());
        artistView.setText(artist);
        artistView.setTextColor(getResources().getColor(R.color.black));
        artistView.setTextSize(20);
        ll2.addView(artistView);
        lyricText=new TextView(this.getActivity());
        lyricText.setText(lyrics);
        lyricText.setTextColor(getResources().getColor(R.color.black));
        //lyricText.setMaxLines(10);
        lyricText.setVerticalScrollBarEnabled(true);
        lyricText.setMovementMethod(new ScrollingMovementMethod());
        ll.addView(lyricText);
        return ll;
    }

    public void prepare(String lyrics, String title, String artist){
        if(lyrics==""||lyrics.length()==0) lyrics="Sorry, lyrics not available";
        this.lyrics=lyrics;
        this.title=title;
        this.artist=artist;
    }

}
