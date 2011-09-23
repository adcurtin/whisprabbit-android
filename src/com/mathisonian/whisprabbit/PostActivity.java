package com.mathisonian.whisprabbit;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PostActivity extends Activity {
	String server = "http://chan.mathisonian.com";
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
			setContentView(R.layout.post_layout);
			
			final Button button = (Button) findViewById(R.id.ButtonCreate);
	        button.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	                createPost();
	            }
	        });
	        
	        Toast.makeText(getApplicationContext(), getIntent().getStringExtra("t_id"), Toast.LENGTH_SHORT);
	 }
	 
	 void createPost() {
		 EditText editText = (EditText) findViewById(R.id.textContent);
		 String myContent = editText.getText().toString();
		 String url = server + "/php/";
		 Intent intent = getIntent();
		 String t_id = "";
		 if(intent.getBooleanExtra("isThread", false)) {
			 url += "createThread.php";
		 } else {
			 url += "createResponse.php";
			 t_id = intent.getStringExtra("t_id");
		 }
		 
		// Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(url);

		    try {
		        // Add your data
		    	
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair("c", myContent));
		        nameValuePairs.add(new BasicNameValuePair("t", t_id));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
//		        HttpResponse response = 
        		httpclient.execute(httppost);
    		    Intent resultIntent = new Intent();
    		    resultIntent.putExtra("POST_IDENTIFIER", 1);
    		    setResult(Activity.RESULT_OK, resultIntent);
    		    finish();

		    } catch (Exception e) {
		    	Toast.makeText(getApplicationContext(), "fuck", Toast.LENGTH_SHORT);
		    }
	 }
}
