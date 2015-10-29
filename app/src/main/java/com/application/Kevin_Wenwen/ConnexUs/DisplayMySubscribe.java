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
import android.widget.Toast;

import android.graphics.Bitmap;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wenwen on 10/21/15.
 */
public class DisplayMySubscribe extends ActionBarActivity {
    private String TAG  = "Display Subscribes";
    private String email;
    Context context = this;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_mysubscribe);

        Intent intent = getIntent();
        email = intent.getStringExtra(DisplayImages.EXTRA_MESSAGE);


        final String request_url = "http://mini3-test1.appspot.com/mySubscribe";
        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", email);

        httpClient.post(request_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                final ArrayList<String> imageURLs = new ArrayList<String>();
                final ArrayList<String> captionList = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray displayImages = jObject.getJSONArray("displayImages");
                    JSONArray caption = jObject.getJSONArray("caption");
                    Log.d("wenwen! ", "json successful");
                    for (int i = 0; i < displayImages.length(); i++) {

                        imageURLs.add(displayImages.getString(i));
                        captionList.add(caption.getString(i));
                        System.out.println(displayImages.getString(i));
                    }
                    //  imageURLs.add("http://lh3.googleusercontent.com/eG4Xx4zEiekHQneAbeViCOhCdZZL76j1p01gvCJUNZlZ7DQcnLQo5w4IFyHaG8sWffMstj795u4W9fUEikinAouQ4ZM");
                    // imageURLs.add("http://blobstore-1107.appspot.com/view_photo/AMIfv96rR9yu2cYNdPeYB4clwpZKQ3sfVL10KGMKkkhONEuIDqm8F3BKY0pCKgL69VOGMIIGczMt3ODyBE4L4DktWTYIZcmY4jdE0QntqzeOQLdUm_WlgcN9EQQpUpocX2CH196RDGRCCfcFDDB57qrWZQLeoLgHyoe09UuX38UxpRevY_fE03E");
                    //  imageURLs.add("http://blobstore-1107.appspot.com/view_photo/AMIfv94eNMqZTxyEIrPnZrPK-vJ2aAUXOL5DEhXc-7wbTQayahyPBGklLn1CyABgyN0LIMARtj33k4Ib-QkQHAnQiaLUbnqApoydfpZ5Vt_tIVd5Gex5v4pb8_8AAJWD114mUpBD5Ck8x3pybwphYBsX66cSnaOFYPMMBArD4AmICVbbro6jC2o");
                    // imageURLs.add("http://aptmini3.appspot.com/view_photo/AMIfv97fab0PppGNVymma0HtTZA1oSd2RN7wH8XWeZqPnLCVQTV0q1ucMQJGMVnU7fPIuyHhbbTzf8USHu0hYSo20tJN8KeEfMaNCLwCa5X5rMFMwORUPRFIWur9h38oppodVsr1-Ifublk37Zc4B8b6fiM_-Nd_1w");
                    //imageURLs.add("http://blobstore-1107.appspot.com/view_photo/AMIfv94yxPpaChAyE2FcZfMdvwl7bgvTMkcX-YMQAJVoOUXjg5apF1GzX6K57v1NgVTgx7lM2VEg2tlF4NRgBSnkHVRBbNFslzJGJ_4HviqIDFTdY0y4l_Be9VJf1faJHlBr0Spar-us-vbHtVoeARcKLbQBX5a--ehkp2-HgVKyBktGVMS4mKw");
                    //        imageURLs.add("http://lh3.googleusercontent.com/eG4Xx4zEiekHQneAbeViCOhCdZZL76j1p01gvCJUNZlZ7DQcnLQo5w4IFyHaG8sWffMstj795u4W9fUEikinAouQ4ZM");

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

}
