package com.mathisonian.whisprabbit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class WhisprabbitActivity extends Activity {

	String server = "http://chan.mathisonian.com";
	ArrayList<String> buttonList;
	ArrayList<Thread> threadList;
	static final String TAG = "MyActivity";
	static ProgressDialog dialog = null;
	ListView lv;
	ArrayAdapter<String> adapter;
	static final int POST_RESULTS = 0;
	static final int SEARCH_RESULTS = 1;
	static String searchTerm = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thread_layout);
		buttonList = new ArrayList<String>();

		lv = (ListView) this.findViewById(R.id.threadList);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				getResponses(position);
			}
		});

		final Button button = (Button) findViewById(R.id.ButtonPostThread);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				makePost();
			}
		});
		
		final Button buttonSearch = (Button) findViewById(R.id.ButtonSearch);
		buttonSearch.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				search();
			}
		});

		adapter = new ArrayAdapter<String>(this, R.layout.list_item, buttonList);
		updateList();
		lv.setAdapter(adapter);

		// try {
		// WhisprabbitActivity.dialog.dismiss();
		// } catch(Exception e) {
		// Log.v(TAG, "What the fuck?!");
		// }
	}

	void updateList() {

		threadList = new ArrayList<Thread>();
		adapter.clear();

		try {
			// dialog = ProgressDialog.show(WhisprabbitActivity.this, "",
			// "Loading. Please wait...", true);
			String urlString = server + "/php/getThreads.php?s=new&n=20";
			urlString += "&q=" + searchTerm;
			URL url = new URL(urlString);
			URLConnection urlConnection = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));

			String json = "";
			String line;
			while ((line = in.readLine()) != null) {
				json += line;
			}

			JSONArray ja = new JSONArray(json);
			int length = ja.length();

			for (int i = 0; i < length; i++) {
				JSONObject jo = ja.getJSONObject(i);
				Thread thread = new Thread(jo.getString("t_id"),
						jo.getString("content"));
				threadList.add(thread);
			}
		} catch (Exception e) {
			Log.v(TAG, getStackTrace(e));
		}

		for (Thread thread : threadList) {
			adapter.add(thread.getContent());
		}
		adapter.notifyDataSetChanged();
	}

	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	void getResponses(int i) {
		// dialog = ProgressDialog.show(WhisprabbitActivity.this, "",
		// "Loading. Please wait...", true);
		Thread thread = threadList.get(i);
		Intent intent = new Intent(this, SingleThreadActivity.class);
		intent.putExtra("t_id", thread.getT_id());
		// Toast.makeText(getApplicationContext(), thread.getT_id(),
		// Toast.LENGTH_SHORT).show();
		startActivity(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        if(!searchTerm.equals("")) {
	        	searchTerm = "";
	        	updateList();
	        	return true;
	        }
	    }
	    return super.onKeyDown(keyCode, event);
	}

	void makePost() {
		Intent intent = new Intent(this, PostActivity.class);
		intent.putExtra("isThread", true);
		startActivityForResult(intent, POST_RESULTS);
	}
	
	void search() {
		Intent intent = new Intent(this, SearchActivity.class);
		startActivityForResult(intent, SEARCH_RESULTS);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (POST_RESULTS):
			if (resultCode == Activity.RESULT_OK) {
				updateList();
			}
			break;
		case (SEARCH_RESULTS):
			if (resultCode == Activity.RESULT_OK) {
				searchTerm = data.getStringExtra("SEARCH_TERM");
				updateList();
			}
			break;
		}
	}
}