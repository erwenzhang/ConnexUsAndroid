package com.application.Kevin_Wenwen.ConnexUs;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class ImageUpload extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    private static final String TAG = "ConnexUs-Upload";
    //final int RQS_GooglePlayServices = 1;
    private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;

    private static final int RC_SIGN_IN = 0;

    private static final String SAVED_PROGRESS = "sign_in_progress";
    private int mSignInProgress;

    // Used to store the PendingIntent most recently returned by Google Play
    // services until the user clicks 'sign in'.
    private PendingIntent mSignInIntent;


    public final static String EXTRA_MESSAGE = "MESSAGE IN";
    private static final int PICK_IMAGE = 1;
    private static final int USE_CAMERA = 2;
    private String email;
    private String stream_name;
    private String locationLat = "0";
    private String locationLong = "0";
    private Location mLastLocation;
    private  LocationRequest mLocationRequest;
    private String[] msg;
    Context context = this;
    private Button uploadButton;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        uploadButton = (Button) findViewById(R.id.upload_to_server);
        uploadButton.setEnabled(false);

        Intent intent = getIntent();
        msg = intent.getStringArrayExtra(DisplayImages.EXTRA_MESSAGE);
        //System.out.println("MSG from a single stream");
       // System.out.println(msg[2]);
       // System.out.println(msg[3]);
        email = msg[0];
        stream_name = msg[1];
        //locationLat = msg[2];
       // locationLong = msg[3];


        // Choose image from library
        TextView uploadTo = (TextView) findViewById(R.id.uploadTo);
        uploadTo.setText("Stream: "+stream_name);
        uploadTo.setTextSize(30);

        Button chooseFromLibraryButton = (Button) findViewById(R.id.choose_from_library);
        chooseFromLibraryButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // To do this, go to AndroidManifest.xml to add permission
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // Start the Intent
                        startActivityForResult(galleryIntent, PICK_IMAGE);
                    }
                }
        );
        mGoogleApiClient = buildGoogleApiClient();
        mLocationRequest = createLocationRequest();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void onLocationChanged(Location location) {
        System.out.print("Location changes wenwen!!");
        System.out.print(location.toString());
        locationLong = String.valueOf(location.getLongitude());
        locationLat = String.valueOf(location.getLatitude());

    }

    protected void onPause() {
        super.onPause();
        // stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    private GoogleApiClient buildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and
        // connection failed callbacks should be returned, which Google APIs our
        // app uses and which OAuth 2.0 scopes our app requests.
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }


    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }



    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        // We call connect() to attempt to re-establish the connection or get a
        // ConnectionResult that we can attempt to resolve.
        mGoogleApiClient.connect();
    }

    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        // int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        //       Log.i("WENWEN0", "IS Available "
        //             + GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()));
        Log.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
        // GooglePlayServicesUtil.getErrorDialog(resultCode,this,RQS_GooglePlayServices ).show();

        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            Log.d("WENWEN", "HERE");
            // An API requested for GoogleApiClient is not available. The device's current
            // configuration might not be supported with the requested API or a required component
            // may not be installed, such as the Android Wear application. You may need to use a
            // second GoogleApiClient to manage the application's optional APIs.
        } else if (mSignInProgress != STATE_IN_PROGRESS) {
            Log.d("WENWEN", "HERE1");
            // We do not have an intent in progress so we should store the latest
            // error resolution intent for use when the sign in button is clicked.
            mSignInIntent = result.getResolution();
          //  mSignInError = result.getErrorCode();

            if (mSignInProgress == STATE_SIGN_IN) {
                // STATE_SIGN_IN indicates the user already clicked the sign in button
                // so we should continue processing errors until the user is signed in
                // or they click cancel.
              //  resolveSignInError();
                System.out.print(STATE_SIGN_IN);
                Log.d("WENWEN1", "111");
                System.out.print(mSignInProgress);

            }
        }
        Log.d("WENWEN", "HERE3");
        // In this sample we consider the user signed out whenever they do not have
        // a connection to Google Play services.
       // onSignedOut();
    }

    public void onConnected(Bundle connectionHint) {


        startLocationUpdates();


    /*    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            locationLat = String.valueOf(mLastLocation.getLatitude());
            locationLong = String.valueOf(mLastLocation.getLongitude());
        }*/
    }

    public void useCamera(View view){
        Intent cameraIntent = new Intent(context, CameraActivity.class);
        //String[] msg_out = new String[4];
        //msg_out = msg;
        /*msg[0] = email;
        msg[1] = nameofStream;
        msg[2] = locationLat;
        msg[3] = locationLong;*/

        cameraIntent.putExtra(EXTRA_MESSAGE, msg);
        startActivityForResult(cameraIntent, USE_CAMERA);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uploadButton.setEnabled(true);
        if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();

            // User had pick an image.

            String[] filePathColumn = {MediaStore.Images.ImageColumns.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            // Link to the image

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imageFilePath = cursor.getString(columnIndex);
            cursor.close();

            // Bitmap imaged created and show thumbnail

            ImageView imgView = (ImageView) findViewById(R.id.thumbnail);

                    // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFilePath, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 1024;
            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (width_tmp > REQUIRED_SIZE || height_tmp > REQUIRED_SIZE) {
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            final Bitmap bitmapImage = BitmapFactory.decodeFile(imageFilePath, o2);
            imgView.setImageBitmap(bitmapImage);

            // Enable the upload button once image has been uploaded

            // Button uploadButton = (Button) findViewById(R.id.upload_to_server);
            // uploadButton.setClickable(true);

            uploadButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // Get photo caption

                            EditText text = (EditText) findViewById(R.id.upload_message);
                            String photoCaption = text.getText().toString();

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                            byte[] b = baos.toByteArray();
                            byte[] encodedImage = Base64.encode(b, Base64.DEFAULT);
                            String encodedImageStr = encodedImage.toString();

                            getUploadURL(b, photoCaption);
                        }
                    }
            );
        } else if (requestCode == USE_CAMERA && resultCode == RESULT_OK && data != null      /*&& data.getData() != null*/) {

            String imageFilePath;
            String[] msg_from_camera = data.getStringArrayExtra(EXTRA_MESSAGE);
            imageFilePath = msg_from_camera[1];
            Log.d("TAGTAGTAG upload",msg_from_camera[0]);
            Log.d("TAGTAGTAG upload",msg_from_camera[1]);

            // Bitmap imaged created and show thumbnail

            ImageView imgView = (ImageView) findViewById(R.id.thumbnail);
            final Bitmap bitmapImage = BitmapFactory.decodeFile(imageFilePath);
            imgView.setImageBitmap(bitmapImage);

            // Enable the upload button once image has been uploaded


            // uploadButton.setClickable(true);

            uploadButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // Get photo caption

                            EditText text = (EditText) findViewById(R.id.upload_message);
                            String photoCaption = text.getText().toString();

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                            byte[] b = baos.toByteArray();
                            byte[] encodedImage = Base64.encode(b, Base64.DEFAULT);
                            String encodedImageStr = encodedImage.toString();

                            getUploadURL(b, photoCaption);
                        }
                    }
            );
        }
        else {
            Log.d("WENWENresulte ","back");
            Intent intent = new Intent(context,DisplayImages.class);
            intent.putExtra(EXTRA_MESSAGE,msg);
            startActivity(intent);
        }

    }

    private void getUploadURL(final byte[] encodedImage, final String photoCaption){
        AsyncHttpClient httpClient = new AsyncHttpClient();
        String request_url="http://mini3-test1.appspot.com/getUploadURL";
        //System.out.println(request_url);
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            String upload_url;

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                try {
                    JSONObject jObject = new JSONObject(new String(response));

                    upload_url = jObject.getString("upload_url");
                    postToServer(encodedImage, photoCaption, upload_url);

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Get_serving_url", "There was a problem in retrieving the url : " + e.toString());
            }
        });
    }

    private void postToServer(byte[] encodedImage,String photoCaption, String upload_url){

        RequestParams params = new RequestParams();
        params.put("file",new ByteArrayInputStream(encodedImage));
        params.put("photoCaption",photoCaption);
        params.put("email",email);
        params.put("stream_name",stream_name);
        params.put("locationLat",locationLat);
        params.put("locationLong",locationLong);
        Log.d("LocationLatLo", "lO");
        System.out.println(locationLat);
        System.out.println(locationLong);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(upload_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Log.w("async", "success!!!!");
                Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, viewSingleStream.class);
                intent.putExtra(EXTRA_MESSAGE,msg);
                startActivity(intent);


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
            }
        });
    }


}
