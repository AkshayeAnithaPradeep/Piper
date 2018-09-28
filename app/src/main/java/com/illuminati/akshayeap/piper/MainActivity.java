package com.illuminati.akshayeap.piper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

    public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    RecordFragment fragment=new RecordFragment();
    ChooseFragment chooseFragment=new ChooseFragment();
    PreviewFragment previewFragment=new PreviewFragment();
    ProceedFragment proceedFragment=new ProceedFragment();
    GenreFragment genreFragment =new GenreFragment();
    ApproveFragment approveFragment=new ApproveFragment();
    LyricsFragment lyricsFragment =new LyricsFragment();

    private int state=0;
    private int countprev=0;
    private int countproc=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getFragmentManager().beginTransaction().add(R.id.fragment_container1,fragment).commit();

        getFragmentManager().beginTransaction().add(R.id.fragment_container2,chooseFragment).commit();

    }

    public void sendMessage(View view) {
        Toast sent = Toast.makeText(view.getContext(), "Sent", Toast.LENGTH_SHORT);
        sent.show();
        //getSupportFragmentManager().beginTransaction().add(R.id.fragment_container2,fragment).commit();
    }

    public void enable(boolean state) {
        chooseFragment.enable(state);
        if(getFragmentManager().findFragmentByTag("PreviewFrag")!=null) previewFragment.enable(state);
    }

    public void setPreview(String filename, int source) {

        previewFragment.preparePreview(filename);
        proceedFragment.preparePreview(filename);
        if(countprev==0) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container3, previewFragment, "PreviewFrag").commit();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container4, proceedFragment, "ProceedFrag").commit();
            countprev=1;
        }
        else {
            getFragmentManager().beginTransaction().attach(previewFragment).commit();
            getFragmentManager().beginTransaction().attach(proceedFragment).commit();
        }

    }

    public void proceed(String filename, String genre, String lyrics, String title, String artist) {

        genreFragment.prepare(filename,genre);
        lyricsFragment.prepare(lyrics, title, artist);
        if(countproc==0) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container6, lyricsFragment, "LyricsFrag").commit();
            getFragmentManager().beginTransaction().replace(R.id.fragment_container5, genreFragment, "GenreFrag").commit();
            countproc=1;
        }
        else {
            getFragmentManager().beginTransaction().attach(lyricsFragment).commit();
            getFragmentManager().beginTransaction().attach(genreFragment).commit();
        }
        getFragmentManager().beginTransaction().detach(previewFragment).commit();
        getFragmentManager().beginTransaction().detach(fragment).commit();
        getFragmentManager().beginTransaction().detach(proceedFragment).commit();
        getFragmentManager().beginTransaction().detach(chooseFragment).commit();
        state=1;
    }

    @Override
    public void onBackPressed() {
        if(state==1) {
            getFragmentManager().beginTransaction().detach(lyricsFragment).commit();
            getFragmentManager().beginTransaction().detach(genreFragment).commit();
            getFragmentManager().beginTransaction().attach(fragment).commit();
            getFragmentManager().beginTransaction().attach(chooseFragment).commit();
            state=0;
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            MainActivity.super.onBackPressed();
                        }
                    }).create().show();

        }

    }
}
