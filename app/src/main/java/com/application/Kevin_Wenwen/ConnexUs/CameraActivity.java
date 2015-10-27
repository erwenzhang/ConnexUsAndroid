package com.application.Kevin_Wenwen.ConnexUs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.hardware.Camera;
import android.widget.FrameLayout;
import android.widget.Button;
import android.view.View;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.net.Uri;
import android.os.Environment;
import android.view.KeyEvent;

public class CameraActivity extends Activity {
<<<<<<< HEAD
=======
    public final static String EXTRA_MESSAGE = "MESSAGE IN";

>>>>>>> master
    private static final String TAG = "CameraActivity";
    private Camera mCamera;
    private CameraPreview mPreview;
    private String imageFile;
    Context context = this;
    private int takePictureClicked = 0;
    private List<byte[]> photoDataList = new ArrayList<byte[]>();
<<<<<<< HEAD
    private String savedPhotoAbsolutePath;
=======
    private String savedPhotoAbsolutePath = "yes";
>>>>>>> master

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        final Button use_this = (Button)findViewById(R.id.use_this);
        use_this.setEnabled(false);
        // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        takePictureClicked++;
                        System.out.println("Take a Picture clicked " + takePictureClicked + " times!!!");

                        if ((takePictureClicked % 2) == 1) {
                            mCamera.takePicture(null, null, mPicture);
                            use_this.setEnabled(true);
                        } else {
                            mCamera.startPreview();
                            use_this.setEnabled(false);
                        }
                    }
                }
        );

        // use_this.setOnClickListener(
        //         new View.OnClickListener() {
        //             @Override
        //             public void onClick(View v) {

        //             }
        //         }
        // );

        Button button_streams = (Button)findViewById(R.id.button_streams);
        button_streams.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // return to stream

                    }
                }
        );
    }
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private PictureCallback mPicture = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // new SaveImageTask().execute(data);
            photoDataList.add(data);
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };


    @Override
    public void onBackPressed() {
        // do something on back.
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.stopPreview();
            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();
            mCamera = null;
        }
        super.onBackPressed();
    }

    public void useThisPhoto(View view){
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mPreview.getHolder().removeCallback(mPreview);
            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();
            mCamera = null;
        }

        byte[] data = photoDataList.get(photoDataList.size() - 1);
        new SaveImageTask().execute(data);


<<<<<<< HEAD
        Intent returnIntent = new Intent();
        String streamName = getIntent().getStringExtra("stream_name");
        returnIntent.putExtra("stream_name", streamName);
        returnIntent.putExtra("image_path", savedPhotoAbsolutePath);
        setResult(RESULT_OK, returnIntent);
        finish();
=======
>>>>>>> master
    }

    public void returnStreams(View view){
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mPreview.getHolder().removeCallback(mPreview);
            // Important: Call release() to release the camera for use by other
            // applications. Applications should release the camera immediately
            // during onPause() and re-open() it during onResume()).
            mCamera.release();
            mCamera = null;
        }
        Intent returnIntent = new Intent();
//        String streamName = getIntent().getStringExtra("streamName");
//        String streamID = getIntent().getStringExtra("streamID");
//        returnIntent.putExtra("streamName",streamName);
//        returnIntent.putExtra("streamID",streamID);
//        returnIntent.putExtra("imageFile",imageFile);
//        setResult(RESULT_OK,returnIntent);
//        finish();

    }

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/ConnexUs");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

<<<<<<< HEAD
                savedPhotoAbsolutePath = sdCard.getAbsolutePath() + "/ConnexUs" + fileName;

=======
                savedPhotoAbsolutePath = sdCard.getAbsolutePath() + "/ConnexUs/" + fileName;

                Log.d("TAGTAGTAG1", savedPhotoAbsolutePath);
>>>>>>> master
                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
<<<<<<< HEAD
=======
                Log.d("TAGTAGTAG", "1");
                Intent returnIntent = new Intent();

                Log.d("TAGTAGTAG", "2");
                String[] msg = getIntent().getStringArrayExtra(EXTRA_MESSAGE);
                String streamName = msg[1];
                String[] msg_out = new String[2];
                Log.d("TAGTAGTAG", "3");
                Log.d("TAGTAGTAG0", savedPhotoAbsolutePath);
                msg_out[0] = streamName;
                msg_out[1] = savedPhotoAbsolutePath;
                Log.d("TAGTAGTAG", savedPhotoAbsolutePath);
                //  returnIntent.putExtra("stream_name", streamName);
                returnIntent.putExtra(EXTRA_MESSAGE,msg_out);

                Log.d("TAGTAGTAG", savedPhotoAbsolutePath);
                Log.d("TAGTAGTAG", "5");
                setResult(RESULT_OK, returnIntent);
                finish();
>>>>>>> master
            }
            return null;
        }

    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }
}
