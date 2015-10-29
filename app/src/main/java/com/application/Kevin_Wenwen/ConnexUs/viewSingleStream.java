package com.application.Kevin_Wenwen.ConnexUs;
import android.app.Dialog;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;

/**
 * Created by wenwen on 10/21/15.
 */
public class viewSingleStream extends ActionBarActivity {
    private String TAG  = "Display Subscribes";
    public final static String EXTRA_MESSAGE = "MESSAGE IN";
    // to mark which image has been displayed
    private int location = 0;
    private int pre_location = 0;

    //msg from DisplayImages
    private String[] msg;
    private String email = null;
    private String locationLat = null;
    private String locationLong = null;
    private String nameofStream;

   // private SignInButton mSignInButton;
    private TextView stream_name;

    private Button upload;
    private Button streams_button;
    private Button more_pictures ;
    private int morePageCount = 0;

    Context context = this;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_singlestream);

        stream_name = (TextView) findViewById(R.id.name);
        upload = (Button)findViewById(R.id.upload);
        streams_button = (Button)findViewById(R.id.streams);
        more_pictures = (Button)findViewById(R.id.morePictures);
        more_pictures.setEnabled(false);

        Intent intent = getIntent();
        msg = intent.getStringArrayExtra(EXTRA_MESSAGE);
        email = msg[0];
        stream_name.setText(msg[1]);
        nameofStream = msg[1];
       // locationLat = msg[2];
       // locationLong = msg[3];
        //Log.d("WENWENWENWEN", locationLat);
        //Log.d("WENWENWENWENfromsearc",locationLong);

        //stream_name.setTextColor();
        stream_name.setTextSize(30);
       // Log.d("wenwen singlesteam ", "EMAIL");
      //  System.out.print(email);





        final String request_url = "http://mini3-test1.appspot.com/viewSingleStream";
        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email",email);
        params.put("stream_name",msg[1]);

        httpClient.get(request_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                final ArrayList<String> imageURLs = new ArrayList<String>();
                final ArrayList<String> captionList = new ArrayList<String>();
                String stream_author;
                //   final ArrayList<String> streamURLs = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    final JSONArray displayImages = jObject.getJSONArray("displayImages");
                    final JSONArray caption = jObject.getJSONArray("caption");
                    stream_author = jObject.getString("author");
                    //Log.i("stream_author",stream_author.toLowerCase());
                    //Log.i("email",email.split("@",2)[0]);
                    //System.out.println(stream_author.toLowerCase().getClass().getName());
                    //System.out.println(email.split("@",2)[0].getClass().getName());
                    if(email!=null){
                        if (email.split("@",2)[0].equals(stream_author.toLowerCase()) ){
                        upload.setEnabled(true);
                     //   Log.d("Upload ability","OK!!");
                     }
                        else{
                            upload.setEnabled(false);
                            //   Log.d("Upload ability", "not OK!!");
                        }
                    }
                    else{
                        upload.setEnabled(false);
                     //   Log.d("Upload ability", "not OK!!");
                    }
                    //  JSONArray streamUrlList = jObject.getJSONArray("streamUrlList");
                    Log.d("display"," length");
                    System.out.print(displayImages.length());
                    System.out.println(displayImages.length());
                    if (displayImages.length() > 16) {
                        more_pictures.setEnabled(true);
                    }
                    for (int i = 0; i < displayImages.length() && i < 16; i++) {

                        imageURLs.add(displayImages.getString(i));
                        //   streamURLs.add(streamUrlList.getString(i));
                        captionList.add(caption.getString(i));
                        //System.out.println(displayImages.getString(i));
                        location = i + 1;
                    }

                    //  imageURLs.add("http://lh3.googleusercontent.com/eG4Xx4zEiekHQneAbeViCOhCdZZL76j1p01gvCJUNZlZ7DQcnLQo5w4IFyHaG8sWffMstj795u4W9fUEikinAouQ4ZM");
                    GridView gridview = (GridView) findViewById(R.id.gridview);
                    //              if (imageURLs.size()>0){
                    gridview.setAdapter(new ImageAdapter(context, imageURLs));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            Toast.makeText(context, captionList.get(position), Toast.LENGTH_SHORT).show();

                            // Intent intent= new Intent(this,viewSingleStream.class,streamURLs.get(position), );
                            //   startActivity(intent);
                            Dialog imageDialog = new Dialog(context);
                            imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            imageDialog.setContentView(R.layout.thumbnail);
                            ImageView image = (ImageView) imageDialog.findViewById(R.id.thumbnail_IMAGEVIEW);

                            Picasso.with(context).load(imageURLs.get(position)).into(image);

                            imageDialog.show();
                        }
                    });


                    more_pictures.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    morePageCount++;
                                    more_pictures.setEnabled(false);

                                    try{
                                        pre_location = location;

                                        if (displayImages.length() - morePageCount * 16 > 16) {
                                            more_pictures.setEnabled(true);
                                        }

                                        for (int i = location, j = 0; i < displayImages.length() && j < 16; i++, j++) {

                                            location = i + 1;

                                            imageURLs.add(displayImages.getString(i));
                                            captionList.add(caption.getString(i));

                                        }
                                    }
                                    catch (JSONException j){
                                        System.out.println("JSON Error");
                                    }

                                   // imageURLs.add("http://blobstore-1107.appspot.com/view_photo/AMIfv96rR9yu2cYNdPeYB4clwpZKQ3sfVL10KGMKkkhONEuIDqm8F3BKY0pCKgL69VOGMIIGczMt3ODyBE4L4DktWTYIZcmY4jdE0QntqzeOQLdUm_WlgcN9EQQpUpocX2CH196RDGRCCfcFDDB57qrWZQLeoLgHyoe09UuX38UxpRevY_fE03E");
                                    GridView gridview = (GridView) findViewById(R.id.gridview);
                                    //              if (imageURLs.size()>0){
                                    final ArrayList<String> subimgURLs = new ArrayList<String>(imageURLs.subList(pre_location,location));
                                    final ArrayList<String> subCaptions = new ArrayList<String>(captionList.subList(pre_location,location));
                                    gridview.setAdapter(new ImageAdapter(context, subimgURLs));
                                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View v,
                                                                int position, long id) {

                                            Toast.makeText(context, subCaptions.get(position), Toast.LENGTH_SHORT).show();

                                            // Intent intent= new Intent(this,viewSingleStream.class,streamURLs.get(position), );
                                            //   startActivity(intent);
                                            Dialog imageDialog = new Dialog(context);
                                            imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            imageDialog.setContentView(R.layout.thumbnail);
                                            ImageView image = (ImageView) imageDialog.findViewById(R.id.thumbnail_IMAGEVIEW);

                                            Picasso.with(context).load(subimgURLs.get(position)).into(image);

                                            imageDialog.show();
                                        }
                                    });


                                }
                            }
                    );


                    //   }
                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e(TAG, "There was a problem in retrieving the url : " + e.toString());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_mysubscribe, menu);
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


    public  void imageUpload(View view){
        Intent intent = new Intent(this,ImageUpload.class);
        //String[] msg_out = new String[4];
        //msg_out = msg;
        /*msg[0] = email;
        msg[1] = nameofStream;
        msg[2] = locationLat;
        msg[3] = locationLong;*/
       // Log.d("WENWENWENWENtoupload", msg[2]);
      //  Log.d("WENWENWENWEN to upload", msg[3]);

        intent.putExtra(EXTRA_MESSAGE,msg);

        startActivity(intent);
    }

    public void viewAllImages(View view){
        Intent intent= new Intent(this, DisplayImages.class);
        //if(Homepage.login)
        String[] msg_out = new String[3];
        msg_out[0] = email;
      //  msg_out[1] = locationLat;
      //  msg_out[2] = locationLong;
        intent.putExtra(EXTRA_MESSAGE,msg_out);
        startActivity(intent);
    }

}
