package com.illuminati.akshayeap.piper;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jmusixmatch.MusixMatchException;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

import songExtractor.Callback;
import songExtractor.SongExtractor;

public class ProceedFragment extends android.app.Fragment {

    LinearLayout ll;
    ProgressDialog progressDialog;
    public SongExtractor songExtractor;

    private NextButton mNextButton=null;
    private String mFileName;
    ProceedFragment proceedFragment;

    public ProceedFragment() {
        // Required empty public constructor
    }


    public class NextButton extends android.support.v7.widget.AppCompatButton {

        OnClickListener clicker = new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                handleProceed();
            }
        };

        public NextButton(Context ctx) {
            super(ctx);
            setOnClickListener(clicker);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        proceedFragment=this;
        ll = new LinearLayout(this.getActivity());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setBackgroundResource(R.color.green);
        ll.setGravity(Gravity.CENTER);

        mNextButton =new NextButton(this.getActivity());
        Drawable nextIcon=getResources().getDrawable(R.drawable.nextbutton);
        mNextButton.setBackground(nextIcon);

        ll.addView(mNextButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams)mNextButton.getLayoutParams();
        lp1.gravity = Gravity.CENTER;
        mNextButton.setLayoutParams(lp1);
        return ll;
    }

    private void handleProceed() {

        progressDialog=new ProgressDialog(this.getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Wait while loading...");
        progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressDialog.show();

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                failedDialog();

            }
        };


        songExtractor=new SongExtractor(new Callback() {
            @Override
            public void oncallback(HashMap<String,String> finalval) {
                progressDialog.dismiss();
                String status=finalval.get("status");
                if(status.equals("fail")) {
                    handler.sendEmptyMessage(0);
                }
                else {
                    String genre = finalval.get("genre");
                    String lyrics = finalval.get("lyrics");
                    String title = finalval.get("title");
                    String artist = finalval.get("artist");
                    setResult(mFileName, genre, lyrics, title, artist);
                }
            }
        },mFileName);
        final Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    songExtractor.convert();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (MusixMatchException e) {
                    e.printStackTrace();
                }
                //log("Converting");
            }
        });
        thread.start();
    }

    public void preparePreview(String filename) {
        mFileName=filename;
    }

    public void setResult(String filename, String genre, String lyrics, String title, String artist) {
        MainActivity mainActivity= (MainActivity) this.getActivity();
        mainActivity.proceed(filename,genre,lyrics,title,artist);
    }

    public void failedDialog() {

        new AlertDialog.Builder(this.getActivity())
                .setTitle("Oops")
                .setMessage("Couldn't identify the song. Please try again.")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
