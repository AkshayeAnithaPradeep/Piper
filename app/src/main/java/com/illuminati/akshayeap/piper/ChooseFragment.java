package com.illuminati.akshayeap.piper;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseFragment extends android.app.Fragment {

    private static final int REQ_CODE_PICK_SOUNDFILE=500;

    private static final int SOURCE_STORAGE=1;

    private static String mFileName = null;

    private boolean mStartPlaying;
    private TextView chooseSong=null;


    private ChooseButton mChooseButton=null;

    LinearLayout ll;
    private int count=0;

    public ChooseFragment() {
        // Required empty public constructor
    }


    class ChooseButton extends android.support.v7.widget.AppCompatButton {

        OnClickListener clicker = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/mpeg");
                startActivityForResult(Intent.createChooser(intent, "Media Picking"), REQ_CODE_PICK_SOUNDFILE);
            }
        };

        public ChooseButton(Context ctx) {
            super(ctx);
            setOnClickListener(clicker);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ll = new LinearLayout(this.getActivity());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundResource(R.color.yellow);
        ll.setGravity(Gravity.CENTER);

        /*chooseSong=new TextView(this.getActivity());
        chooseSong.setText("Or choose a song from your collection");
        chooseSong.setPadding(10,10,10,10);
        chooseSong.setTextSize(20);
        ll.addView(chooseSong);*/
        mChooseButton= new ChooseButton(this.getActivity());
        //mChooseButton.setText("Choose Song");
        Drawable icon=getResources().getDrawable(R.drawable.audiobutton);
        mChooseButton.setBackground(icon);
        ll.addView(mChooseButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams)mChooseButton.getLayoutParams();
        lp1.gravity = Gravity.CENTER;
        mChooseButton.setLayoutParams(lp1);
        return ll;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_PICK_SOUNDFILE && resultCode == Activity.RESULT_OK){
            if ((data != null) && (data.getData() != null)){
                String audiofile;
                Uri audioFileUri = data.getData();
                audiofile=audioFileUri.toString();
                System.out.println(audiofile);
                if(audioFileUri.toString().contains("content")) {
                    audiofile=getRealPathFromURI(this.getActivity(),audioFileUri);
                }
                log(audiofile);
                setPreview(audiofile,SOURCE_STORAGE);
            }
        }
    }

    private void log(String text) {

        Toast sent = Toast.makeText(this.getView().getContext(),text, Toast.LENGTH_SHORT);
        //sent.show();

    }

    public void enable(boolean state) {
        mChooseButton.setEnabled(state);
    }

    public void setPreview(String filename, int source) {

        MainActivity mainActivity= (MainActivity) this.getActivity();
        mainActivity.setPreview(filename,source);
    }

    public static String getRealPathFromURI(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
