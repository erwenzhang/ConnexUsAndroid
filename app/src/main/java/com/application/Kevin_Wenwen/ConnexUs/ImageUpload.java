package com.application.Kevin_Wenwen.ConnexUs;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public class ImageUpload extends ActionBarActivity implements LocationListener{
    public final static String EXTRA_MESSAGE = "MESSAGE IN";
    private static final int PICK_IMAGE = 1;
    private static final int USE_CAMERA = 2;
    private String email;
    private String stream_name;
    private String locationLat;
    private String locationLong;
    private String[] msg;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        Intent intent = getIntent();
        msg = intent.getStringArrayExtra(DisplayImages.EXTRA_MESSAGE);
        //System.out.println("MSG from a single stream");
       // System.out.println(msg[2]);
       // System.out.println(msg[3]);
        email = msg[0];
        stream_name = msg[1];
        locationLat = msg[2];
        locationLong = msg[3];

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
    public void onLocationChanged(Location location) {
        System.out.print("Location changes wenwen");
        System.out.print(location.toString());
        locationLong = String.valueOf(location.getLongitude());
        locationLat = String.valueOf(location.getLatitude());

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
        if ((requestCode == PICK_IMAGE && data != null && data.getData() != null)
            || (requestCode == USE_CAMERA && resultCode == RESULT_OK && data != null)) {
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
            final Bitmap bitmapImage = BitmapFactory.decodeFile(imageFilePath);
            imgView.setImageBitmap(bitmapImage);

            // Enable the upload button once image has been uploaded

            Button uploadButton = (Button) findViewById(R.id.upload_to_server);
            uploadButton.setClickable(true);

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
        } /*else if (requestCode == USE_CAMERA && resultCode == RESULT_OK && data != null      && data.getData() != null) {

        }*/
    }

    private void getUploadURL(final byte[] encodedImage, final String photoCaption){
        AsyncHttpClient httpClient = new AsyncHttpClient();
        String request_url="http://blobstore-1107.appspot.com/getUploadURL";
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
        System.out.println(locationLat);

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
