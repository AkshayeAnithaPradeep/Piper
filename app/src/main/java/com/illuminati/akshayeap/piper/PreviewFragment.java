package com.illuminati.akshayeap.piper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

public class PreviewFragment extends android.app.Fragment {


    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;
    private boolean mStartPlaying;
    private TextView playPreview=null;

    private PlayButton mPlayButton = null;
    private MediaPlayer mPlayer = null;

    LinearLayout ll;

    private int count=0;

    public PreviewFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void startPlaying() {
        mPlayer = new MediaPlayer();
        /*mFileName=Environment.getExternalStorageDirectory() +
                File.separator + "Piper"+File.separator+"audiorecordtest.3gp";*/
        if(mFileName.startsWith("content")) {
            try {
                mPlayer.setDataSource(this.getContext(),Uri.parse(mFileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                mPlayer.setDataSource(mFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            mPlayer.prepare();
            mPlayer.start();
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handleStopMedia();
                }
            }, mPlayer.getDuration());
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    public void handleStopMedia() {
        if(!(mPlayButton.getState())) {
            stopPlaying();
            mPlayButton.setmPlayButton();
        }
    }


    public class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    //setText("Stop playing");
                    Drawable stopIcon=getResources().getDrawable(R.drawable.stopbutton);
                    setBackground(stopIcon);
                } else {
                    //setText("Preview");
                    Drawable previewIcon=getResources().getDrawable(R.drawable.playbutton);
                    setBackground(previewIcon);
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setOnClickListener(clicker);
        }

        public void setmPlayButton() {
            mStartPlaying = !mStartPlaying;
        }

        public boolean getState() {
            return mStartPlaying;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        ll = new LinearLayout(this.getActivity());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setBackgroundResource(R.color.green);
        ll.setGravity(Gravity.CENTER);
        /*playPreview=new TextView(this.getActivity());
        playPreview.setText("Play Preview");
        playPreview.setPadding(10,10,10,10);
        playPreview.setTextSize(20);
        ll.addView(playPreview);*/
        mPlayButton=new PlayButton(this.getActivity());
        Drawable previewIcon=getResources().getDrawable(R.drawable.playbutton);
        mPlayButton.setBackground(previewIcon);
        ll.addView(mPlayButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams)mPlayButton.getLayoutParams();
        lp1.gravity = Gravity.CENTER;
        mPlayButton.setLayoutParams(lp1);

        return ll;
    }

    public void preparePreview(String filename) {
        mFileName=filename;
    }

    public void enable(boolean state) {
        mPlayButton.setEnabled(state);
    }
}
