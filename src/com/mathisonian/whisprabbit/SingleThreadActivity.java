package com.mathisonian.whisprabbit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SingleThreadActivity extends Activity {
	String server = "http://chan.mathisonian.com";
	ArrayList<String> buttonList;
	ArrayList<Response> responseList;
	static final String TAG = "MyActivity";
	static ProgressDialog dialog = null;
	static final int POST_RESULTS = 0;
	ListView lv;
	ArrayAdapter<String> adapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.response_layout);
	    
	    buttonList = new ArrayList<String>();
	    responseList = new ArrayList<Response>();

		lv = (ListView) this.findViewById(R.id.responseList);
		
//		lv.setOnItemClickListener(new OnItemClickListener() {
//        	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                getResponses(position);
//            }
//		});
		
		final Button button = (Button) findViewById(R.id.ButtonPostResponse);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makePost();
            }
        });
	    
		adapter = new ArrayAdapter<String>(this, R.layout.list_item, buttonList);
		updateList();
		lv.setAdapter(adapter);
    }
    
    void updateList() {
    	responseList = new ArrayList<Response>();
	    adapter.clear();
	    
    	try {
	    	Intent intent = getIntent();
	    	String urlString = server + "/php/getResponses.php?n=20&t=" + intent.getStringExtra("t_id");
	    	if(intent.hasExtra("query")) {
	    		urlString += intent.getStringExtra("query");
	    	}
			URL url = new URL(urlString);
			URLConnection urlConnection = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			 
			String json = "";
			String line;
			while ((line = in.readLine()) != null) {
			  json += line;
			}
			
//			Toast.makeText(getApplicationContext(), json, Toast.LENGTH_SHORT).show();
			
			JSONArray ja = new JSONArray(json);
			JSONObject jo = ja.getJSONObject(0);
			
			Response response = new Response(jo.getString("t_id"), jo.getString("content"));
			responseList.add(response);
			
			ja = ja.getJSONArray(1);
			int length = ja.length();
			for (int i = 0; i < length; i++) {
			  jo = ja.getJSONObject(i);
			  response = new Response(jo.getString("r_id"), jo.getString("content"));
			  responseList.add(response);
			}
		} catch(Exception e) {
//			Log.v(TAG, getStackTrace(e));
		}
		
		for(Response response : responseList) {
			adapter.add(response.getContent());
		}
		adapter.notifyDataSetChanged();
    }
    
    void makePost() {
		Intent intent = new Intent(this, PostActivity.class);
		intent.putExtra("isThread", false);
		intent.putExtra("t_id", getIntent().getStringExtra("t_id"));
		startActivityForResult(intent, POST_RESULTS);
	}
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 
	  switch(requestCode) { 
	    case (POST_RESULTS) : { 
	      if (resultCode == Activity.RESULT_OK) { 
	    	  updateList();
	      }
	    } 
	  } 
	}
}
