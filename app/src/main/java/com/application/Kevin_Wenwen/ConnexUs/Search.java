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
import android.widget.EditText;
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
public class Search extends ActionBarActivity {
    private String TAG  = "Start Search";
    public final static String EXTRA_MESSAGE = "MESSAGE IN";
    private String search_item;
    private String email = null;
    private String[] msg;
    Context context = this;
    private int location = 0;
    private int pre_location = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final Button more_results = (Button)findViewById(R.id.moreResults);
        EditText search_bar = (EditText)findViewById(R.id.search_bar);
        search_bar.setVisibility(View.VISIBLE);


        Intent intent = getIntent();
        msg = intent.getStringArrayExtra(DisplayImages.EXTRA_MESSAGE);
        email = msg[0];
        search_item = msg[1];



        final String request_url = "http://blobstore-1107.appspot.com/search";
        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("search_item", search_item);

        httpClient.get(request_url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                final ArrayList<String> coverURLs = new ArrayList<String>();
                final ArrayList<String> streams = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    final JSONArray displayCovers = jObject.getJSONArray("displayCovers");
                    final JSONArray streamList = jObject.getJSONArray("streamList");
                    Log.d("wenwen! ", "searching json successful");
                    for (int i = 0; i < displayCovers.length() && i < 8; i++) {

                        coverURLs.add(displayCovers.getString(i));
                        streams.add(streamList.getString(i));
                        location = i + 1;
                        //  System.out.println(displayImages.getString(i));
                    }
                    GridView gridview = (GridView) findViewById(R.id.gridview);
                    gridview.setAdapter(new ImageAdapter(context, coverURLs));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            Toast.makeText(context, streams.get(position), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(context, viewSingleStream.class);
                            String[] msg_out = new String[4];
                            msg_out[0] = email;
                            msg_out[1] = streams.get(position);
                            msg_out[2] = msg[2];
                            msg_out[3] = msg[3];
                            intent.putExtra(EXTRA_MESSAGE,msg_out);
                            startActivity(intent);

                        }
                    });


                    more_results.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        pre_location = location;

                                        for (int i = location, j = 0; i < displayCovers.length() && j < 8; i++, j++) {

                                            location = i + 1;

                                            coverURLs.add(displayCovers.getString(i));
                                            streams.add(streamList.getString(i));

                                        }
                                    } catch (JSONException j) {
                                        System.out.println("JSON Error");
                                    }

                                    // imageURLs.add("http://blobstore-1107.appspot.com/view_photo/AMIfv96rR9yu2cYNdPeYB4clwpZKQ3sfVL10KGMKkkhONEuIDqm8F3BKY0pCKgL69VOGMIIGczMt3ODyBE4L4DktWTYIZcmY4jdE0QntqzeOQLdUm_WlgcN9EQQpUpocX2CH196RDGRCCfcFDDB57qrWZQLeoLgHyoe09UuX38UxpRevY_fE03E");
                                    GridView gridview = (GridView) findViewById(R.id.gridview);
                                    //              if (imageURLs.size()>0){
                                    final ArrayList<String> subcoverURLs = new ArrayList<String>(coverURLs.subList(pre_location, location));
                                    final ArrayList<String> subStreams = new ArrayList<String>(streams.subList(pre_location, location));
                                    gridview.setAdapter(new ImageAdapter(context, subcoverURLs));
                                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View v,
                                                                int position, long id) {

                                            Toast.makeText(context, subStreams.get(position), Toast.LENGTH_SHORT).show();


                                            Intent intent = new Intent(context, viewSingleStream.class);
                                            String[] msg_out = new String[4];
                                            msg_out[0] = email;
                                            msg_out[1] = streams.get(position);
                                            msg_out[2] = msg[1];
                                            msg_out[3] = msg[2];
                                            intent.putExtra(EXTRA_MESSAGE,msg_out);
                                            startActivity(intent);


                                        }
                                    });


                                }
                            }
                    );


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

    public void startSearch(View v){
        Intent intent = new Intent(this,Search.class);
        EditText text = (EditText) findViewById(R.id.search_bar);
        String search_item = text.getText().toString();
        String[] msg_out = new String[4];
        msg_out[0] = email;
        msg_out[1] = search_item;
        msg_out[2] = msg[1];
        msg_out[3] = msg[2];
        intent.putExtra(EXTRA_MESSAGE,msg_out);
        startActivity(intent);


    }

}
