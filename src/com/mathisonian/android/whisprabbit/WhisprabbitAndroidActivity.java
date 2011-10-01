package com.mathisonian.android.whisprabbit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
//import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class WhisprabbitAndroidActivity extends Activity {

	String server = "http://www.whisprabbit.com";
	ArrayList<String> buttonList;
	ArrayList<TextPost> threadList;
	static final String TAG = "MyActivity";
	static ProgressDialog dialog = null;
	ListView lv;
	ImageTextAdapter adapter;
	static final int POST_RESULTS = 0;
	static final int SEARCH_RESULTS = 1;
	static String searchTerm = "";
//	private Runnable viewThreads;
//	private ProgressDialog m_ProgressDialog = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.thread_layout);
			// buttonList = new ArrayList<String>();

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

			threadList = new ArrayList<TextPost>();
		} catch (Exception e) {
			Log.v(TAG, getStackTrace(e));
		}
		adapter = new ImageTextAdapter(this, R.layout.list_item_image,
				threadList);
		updateList();
		lv.setAdapter(adapter);

//		viewThreads = new Runnable() {
//			@Override
//			public void run() {
//				updateList();
//			}
//		};
//		Thread thread = new Thread(null, viewThreads, "WhisprabbitBackground");
//		thread.start();
//		m_ProgressDialog = ProgressDialog.show(WhisprabbitAndroidActivity.this,
//				"Please wait...", "Retrieving data ...", true);

		// try {
		// WhisprabbitActivity.dialog.dismiss();
		// } catch(Exception e) {
		// Log.v(TAG, "What the fuck?!");
		// }
	}

	void updateList() {
//		threadList = new ArrayList<TextPost>();
		 adapter.clear();

		try {
			 dialog = ProgressDialog.show(WhisprabbitAndroidActivity.this, "","Loading. Please wait...", true);
			String urlString = server + "/php/getThreads.php?s=new&n=12";
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
				TextPost thread = new TextPost(jo.getString("t_id"),
						jo.getString("content").replace("\n", " ").trim(), ""/*getFilename(jo.getString("t_id"))*/);
//				threadList.add(thread);
				 adapter.add(thread);
			}
		} catch (Exception e) {
			Log.v(TAG, getStackTrace(e));
		}
		//
		// for (TextPost thread : threadList) {
		// adapter.add(thread);
		// }
//		runOnUiThread(returnRes);			
		
		 adapter.notifyDataSetChanged();
		 WhisprabbitAndroidActivity.dialog.dismiss();
	}
	
	String getFilename(String id) throws Exception {
		String urlString = server + "/php/getAttach.php?id=" + id;
		URL url = new URL(urlString);
		URLConnection urlConnection = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				urlConnection.getInputStream()));

		return in.readLine();
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
		TextPost thread = threadList.get(i);
		Intent intent = new Intent(this, SingleThreadActivity.class);
		intent.putExtra("t_id", thread.getId());
		// Toast.makeText(getApplicationContext(), thread.getT_id(),
		// Toast.LENGTH_SHORT).show();
		startActivity(intent);
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
				Toast.makeText(getApplicationContext(), searchTerm, Toast.LENGTH_SHORT).show();
				updateList();
			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!searchTerm.equals("")) {
				searchTerm = "";
				updateList();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private class ImageTextAdapter extends ArrayAdapter<TextPost> {

		private ArrayList<TextPost> items;

		public ImageTextAdapter(Context context, int textViewResourceId,
				ArrayList<TextPost> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);
			}
			TextPost o = items.get(position);
			if (o != null) {
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				if (tt != null) {
					tt.setText("Name: " + o.getId());
				}
				if (bt != null) {
					bt.setText("Status: " + o.getContent());
				}
				
				ImageView iv = (ImageView) v.findViewById(R.id.listimage);
				iv.setImageBitmap(ImageLoader.getBitmap(server + "/images/thumbs/rabbit.jpg"));
//				iv.setImageBitmap(ImageLoader.getBitmap(server + "/uploads/mobile/" + o.getFilename()));
				
			}
			return v;
		}
	}

//	private Runnable returnRes = new Runnable() {
//
//		@Override
//		public void run() {
//			if (threadList != null && threadList.size() > 0) {
//				adapter.notifyDataSetChanged();
//				for (int i = 0; i < threadList.size(); i++)
//					adapter.add(threadList.get(i));
//			}
//			m_ProgressDialog.dismiss();
//			adapter.notifyDataSetChanged();
//		}
//	};
	
	// Menu Stuff
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.thread_bottom_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.thread_refresh:
	        updateList();
	        return true;
	    case R.id.thread_sort:
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}