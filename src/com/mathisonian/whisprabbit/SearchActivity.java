package com.mathisonian.whisprabbit;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class SearchActivity extends Activity {
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
		setContentView(R.layout.search_layout);
		
		final Button button = (Button) findViewById(R.id.SearchButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search();
            }
        });
		
    }
    
    void search() {
    	Intent resultIntent = new Intent();
    	EditText editText = (EditText) findViewById(R.id.SearchText);
	    resultIntent.putExtra("SEARCH_TERM", editText.getText().toString());
	    setResult(Activity.RESULT_OK, resultIntent);
	    finish();
    }
}
