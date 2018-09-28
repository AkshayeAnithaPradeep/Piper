package com.illuminati.akshayeap.piper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.NoiseSuppressor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Process;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import lame.SimpleLame;

public class RecordFragment extends android.app.Fragment {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int REQUEST_FOLDER_CREATION_PERMISSION = 201;
    private static final int SOURCE_REC=0;

    private static String mFileName = null;
    private boolean mRecording=false;

    private TextView startRecording=null;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    LinearLayout ll;
    //MainActivity mainActivity= (MainActivity) this.getActivity();

    public static final int MSG_REC_STARTED = 0;
    public static final int MSG_REC_STOPPED = 1;
    public static final int MSG_ERROR_GET_MIN_BUFFERSIZE = 2;
    public static final int MSG_ERROR_CREATE_FILE = 3;
    public static final int MSG_ERROR_REC_START = 4;
    public static final int MSG_ERROR_AUDIO_RECORD = 5;
    public static final int MSG_ERROR_AUDIO_ENCODE = 6;
    public static final int MSG_ERROR_WRITE_FILE = 7;
    public static final int MSG_ERROR_CLOSE_FILE = 8;
    private int mSampleRate=22050;
    private boolean mIsRecording = false;
    private Handler mHandler;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToCreateFolderAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case REQUEST_FOLDER_CREATION_PERMISSION:
                permissionToCreateFolderAccepted=grantResults[1]== PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted )
        {
            log("Permission Denied!");
        }

        if (!permissionToCreateFolderAccepted )
        {
            log("Permission Denied!");
        }

    }

    private void startRecording() {


        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Piper");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            mFileName=Environment.getExternalStorageDirectory() +
                    File.separator + "Piper"+File.separator+"audiorecordtest.mp3";

            new Thread() {
                @Override
                public void run() {
                    Process
                            .setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                    final int minBufferSize = AudioRecord.getMinBufferSize(
                            mSampleRate, AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
                    if (minBufferSize < 0) {
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(MSG_ERROR_GET_MIN_BUFFERSIZE);
                        }
                        return;
                    }
                    AudioRecord audioRecord = new AudioRecord(
                            MediaRecorder.AudioSource.MIC, mSampleRate,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2);

                    int audioSessionId=audioRecord.getAudioSessionId();
                    if (NoiseSuppressor.create(audioSessionId) == null) {
                        Log.v("NoiseOff","NoiseSuppressor failed :(");
                        //log("NoiseSuppressor failed :(");
                    } else {
                        Log.v("NoiseOn","NoiseSuppressor ON");
                        //log("NoiseSuppressor ON");
                    }
                    if(AutomaticGainControl.create(audioSessionId)==null) {
                        Log.v("AGCOff","AutomaticGainControl failed :(");
                    }else {
                        Log.v("AGCOn","AutomaticGainControl ON");
                    }
                    if (AcousticEchoCanceler.create(audioSessionId) == null) {
                        Log.v("AECOff","AcousticEchoCanceler failed :(");
                    } else {
                        Log.i("AECOn","AcousticEchoCanceler ON");
                    }

                    // PCM buffer size (5sec)
                    short[] buffer = new short[mSampleRate * (16 / 8) * 1 * 10]; // SampleRate[Hz] * 16bit * Mono * 10sec
                    byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];

                    FileOutputStream output = null;
                    try {
                        output = new FileOutputStream(new File(mFileName));
                    } catch (FileNotFoundException e) {
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(MSG_ERROR_CREATE_FILE);
                        }
                        return;
                    }

                    // Lame init
                    SimpleLame.init(mSampleRate, 1, mSampleRate, 32, 1);

                    mIsRecording = true;
                    try {
                        try {
                            audioRecord.startRecording();
                        } catch (IllegalStateException e) {
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_ERROR_REC_START);
                            }
                            return;
                        }

                        try {
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(MSG_REC_STARTED);
                            }

                            int readSize = 0;
                            while (mIsRecording) {
                                readSize = audioRecord.read(buffer, 0, minBufferSize);
                                if (readSize < 0) {

                                    if (mHandler != null) {
                                        mHandler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
                                    }
                                    break;
                                }

                                else if (readSize == 0) {
                                }

                                else {
                                    int encResult = SimpleLame.encode(buffer,
                                            buffer, readSize, mp3buffer);
                                    if (encResult < 0) {

                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(MSG_ERROR_AUDIO_ENCODE);
                                        }
                                        break;
                                    }
                                    if (encResult != 0) {
                                        try {
                                            output.write(mp3buffer, 0, encResult);
                                        } catch (IOException e) {

                                            if (mHandler != null) {
                                                mHandler.sendEmptyMessage(MSG_ERROR_WRITE_FILE);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }

                            int flushResult = SimpleLame.flush(mp3buffer);
                            if (flushResult < 0) {

                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(MSG_ERROR_AUDIO_ENCODE);
                                }
                            }
                            if (flushResult != 0) {
                                try {
                                    output.write(mp3buffer, 0, flushResult);
                                } catch (IOException e) {

                                    if (mHandler != null) {
                                        mHandler.sendEmptyMessage(MSG_ERROR_WRITE_FILE);
                                    }
                                }
                            }

                            try {
                                output.close();
                            } catch (IOException e) {

                                if (mHandler != null) {
                                    mHandler.sendEmptyMessage(MSG_ERROR_CLOSE_FILE);
                                }
                            }
                        } finally {
                            audioRecord.stop();
                            audioRecord.release();
                        }
                    } finally {
                        SimpleLame.close();
                        mIsRecording = false;
                    }


                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(MSG_REC_STOPPED);
                    }
                }
            }.start();





            handleLoading(true);

            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsRecording=false;
                    handleLoading(false);
                    setPreview(mFileName,SOURCE_REC);
                }
            },10000);
        } else {
           log("Failed to create folder!");
        }


    }

    private void handleLoading(boolean state) {
        LoadingFragment loadingFragment=new LoadingFragment();
        if(state) {
            mRecordButton.setEnabled(false);
            MainActivity mainActivity= (MainActivity) this.getActivity();
            mainActivity.enable(false);
            this.getActivity().getFragmentManager().beginTransaction()
                    .add(R.id.loading_text_fragment_container,loadingFragment).commit();
        }
        else {
            mRecordButton.setEnabled(true);
            MainActivity mainActivity= (MainActivity) this.getActivity();
            mainActivity.enable(true);
            this.getActivity().getFragmentManager().beginTransaction()
                    .remove(getFragmentManager().findFragmentById(R.id.loading_text_fragment_container)).commit();
            log("Removing");
        }
    }

    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                startRecording();
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            //setText("Start recording");
            setOnClickListener(clicker);
        }
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ActivityCompat.requestPermissions(this.getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        System.loadLibrary("mp3lame");
        ll = new LinearLayout(this.getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundResource(R.color.paleTurquoise);
        ll.setGravity(Gravity.CENTER);
        /*startRecording=new TextView(this.getActivity());
        startRecording.setText("Start Recording");
        startRecording.setPadding(10,10,10,10);
        startRecording.setTextSize(20);
        ll.addView(startRecording);*/
        mRecordButton = new RecordButton(this.getActivity());
        Drawable icon=getResources().getDrawable(R.drawable.recordbutton);
        mRecordButton.setBackground(icon);
        ll.addView(mRecordButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mRecordButton.getLayoutParams();
        lp.gravity = Gravity.CENTER;
        mRecordButton.setLayoutParams(lp);
        return ll;
    }

    private void log(String text) {

        Toast sent = Toast.makeText(this.getView().getContext(),text, Toast.LENGTH_SHORT);
        //sent.show();

    }

    public void setPreview(String filename, int source) {

        MainActivity mainActivity= (MainActivity) this.getActivity();
        mainActivity.setPreview(filename,source);
    }
}
