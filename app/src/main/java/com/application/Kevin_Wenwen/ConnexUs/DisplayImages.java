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
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.view.WindowManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;




public class DisplayImages extends ActionBarActivity {
    Context context = this;
    private String TAG  = "Display Images";
    private String email;
    private String[] msg;
    public final static String EXTRA_MESSAGE = "MESSAGE IN";
    public final static String EXTRA_MESSAGE1 = "com.displayimages.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images);

        //Bundle bundle=new Bundle();
      //  bundle.putString("email",email);

      //  FragmentManager fragmentManager = getFragmentManager();
     //   FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
     //   DisplayMySubscribe mysubscribe = new DisplayMySubscribe();
      //  mysubscribe.setArguments(bundle);
      //  fragmentTransaction.replace(android.R.id.content,mysubscribe);
      //  fragmentTransaction.commit();


        Button my_subscribe  = (Button) findViewById(R.id.my_subscribe);

        Intent intent = getIntent();
        intent.getClass();
       // String source = intent.getStringExtra("From");
        msg = intent.getStringArrayExtra(EXTRA_MESSAGE);
        email = msg[0];

        if(email != null){
            my_subscribe.setEnabled(true);
            Log.d("wenwen passing msg", email);
        }

        else {
            my_subscribe.setEnabled(false);
          //  Log.d("wenwen passing msg", " failed");
        }
      //  final String request_url = "http://aptandroiddemo.appspot.com/viewAllPhotos";
      final String request_url = "http://blobstore-1107.appspot.com/viewAllPhotos";
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                final ArrayList<String> coverURLs = new ArrayList<String>();
                final ArrayList<String> streams = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    JSONArray displayCovers = jObject.getJSONArray("displayCovers");
                    JSONArray streamList = jObject.getJSONArray("streamList");
                   // Log.d("wenwen TAG", "json successful");
                    for(int i=0;i<displayCovers.length();i++) {

                        coverURLs.add(displayCovers.getString(i));
                        streams.add(streamList.getString(i));
                        System.out.println(displayCovers.getString(i));
                    }
                    GridView gridview = (GridView) findViewById(R.id.gridview);
                    gridview.setAdapter(new ImageAdapter(context,coverURLs));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                         //    Toast.makeText(context, streamURLs.get(position), Toast.LENGTH_SHORT).show();

                            Intent intent= new Intent(context,viewSingleStream.class);
                            String[] msg_out = new String[4];
                            msg_out[0] = email;
                            msg_out[1] = streams.get(position);
                            msg_out[2] = msg[1];
                            msg_out[3] = msg[2];
                            Log.d("WENWENWENWEN to single", msg_out[2]);
                            Log.d("WENWENWENWEN to single", msg_out[3]);

                           // intent.putExtra(EXTRA_MESSAGE,)
                            intent.putExtra( EXTRA_MESSAGE,msg_out);
                            startActivity(intent);

                            //Dialog imageDialog = new Dialog(context);
                         //   imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                           // imageDialog.setContentView(R.layout.thumbnail);
                            //ImageView image = (ImageView) imageDialog.findViewById(R.id.thumbnail_IMAGEVIEW);

                           // Picasso.with(context).load(coverURLs.get(position)).into(image);

                         //   imageDialog.show();
                        }
                    });
                }
                catch(JSONException j){
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
        getMenuInflater().inflate(R.menu.display_images, menu);
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

    public void mySubscribeStreams(View v ){
        Intent intent= new Intent(this, DisplayMySubscribe.class);
        intent.putExtra(EXTRA_MESSAGE,email);
        startActivity(intent);
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
        Log.d("WENWENWENWEN to search", msg_out[2]);
        Log.d("WENWENWENWEN to search", msg_out[3]);
        intent.putExtra(EXTRA_MESSAGE,msg_out);
        startActivity(intent);
    }


}
