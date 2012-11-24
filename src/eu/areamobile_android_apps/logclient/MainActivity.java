package eu.areamobile_android_apps.logclient;

import java.util.ArrayList;

import com.google.gson.Gson;

import database.CommentsDataSource;

import eu.areamobile_android_apps.logclient.DownloadAsync.OnDownloadCompletedListener;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity implements OnClickListener,
		OnDownloadCompletedListener, OnItemClickListener {

	private Button mButton;
	private ListView mCommentsList;
	private CommentsAdapter mCommentsAdapter;
	private LogApplication app;
	private DownloadCompletedReceiver receiver;
	
	public static final String EXTRA_COMMENT = "eu.areamobile_android_apps.logclient.comment";

	private CommentsDataSource db;
	public static final String LOGCLIENT_PREFERENCES = "eu.areamobile_android_apps.logclient.PREFERENCES";
	public static final String LOGCLIENT_LASTDOWNLOAD = "eu.areamobile_android_apps.logclient.LASTDOWNLOAD";
	private SharedPreferences prefs;
	private long lastDownloadedTimestamp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mButton = (Button) findViewById(R.id.btn_download);
		mButton.setOnClickListener(this);

		mCommentsList = (ListView) findViewById(R.id.lv_comments);

		app = (LogApplication) getApplication();
		if (savedInstanceState == null) {
			app.setItems(new Comments());
		}

		mCommentsAdapter = new CommentsAdapter(this, app.getItems());
		mCommentsList.setAdapter(mCommentsAdapter);
		mCommentsList.setOnItemClickListener(this);

		// register receiver
		IntentFilter filter = new IntentFilter(
				DownloadCompletedReceiver.ACTION_RESP);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new DownloadCompletedReceiver();
		registerReceiver(receiver, filter);
		
		// open database
		db = new CommentsDataSource(this);
		db.open();
//		db.deleteAllComments();
		if (!db.isEmpty()){
			updateListFromDB();
		}
		
		prefs = getSharedPreferences(LOGCLIENT_PREFERENCES, Context.MODE_PRIVATE);
        lastDownloadedTimestamp = prefs.getLong(LOGCLIENT_LASTDOWNLOAD, -1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {

		// new DownloadAsync().execute(this);

		switch(v.getId()){
		
		case R.id.btn_download:
			Intent downloadIntent = new Intent(this, DownloadService.class);
			downloadIntent.putExtra(DownloadService.PARAM_IN_MSG, String.valueOf(lastDownloadedTimestamp));
			startService(downloadIntent);

			Toast.makeText(this, "getting new comments", Toast.LENGTH_SHORT).show();
			break;
		
		case R.id.btn_addcomment:
			
			
			break;
		}
		

	}

	/*
	 * Used for the asynctask only
	 */
	@Override
	public void onDownloadCompleted(Comments comments) {

		app.setItems(comments);
		mCommentsAdapter.refresh(comments);
		mCommentsAdapter.notifyDataSetChanged();

		Toast.makeText(this, "download finished", Toast.LENGTH_SHORT).show();

	}

	private void updateList(Comments comments) {
	
		lastDownloadedTimestamp = System.currentTimeMillis();
		SharedPreferences.Editor editor = prefs.edit();
        if(editor != null) {
        	editor.putLong(LOGCLIENT_LASTDOWNLOAD, lastDownloadedTimestamp);
        	editor.commit();
        }
		
        if (comments.size()>0){
        	
        	app.addItems(comments);
     		db.addCommentList(comments);
     		 
     		mCommentsAdapter.refresh(app.getItems());
     		mCommentsAdapter.notifyDataSetChanged();

     		Toast.makeText(this, comments.size()+" new comment/s", Toast.LENGTH_SHORT).show();        	
        	
        } else {
        	
     		Toast.makeText(this, "no new comments", Toast.LENGTH_SHORT).show();        	

        }
       
	}

	private void updateListFromDB(){
		
		Comments comments = db.getAllComments();
		app.setItems(comments);
		
		mCommentsAdapter.refresh(comments);
		mCommentsAdapter.notifyDataSetChanged();
		
		Toast.makeText(this, "displaying comments from db", Toast.LENGTH_SHORT).show();
	}
	
	private void displayError() {
	    
	    new AlertDialog.Builder(this)
	    .setTitle("Network Error")
	    .setMessage("Check connection")
	    .setNeutralButton("Close", null)
	    .show();  
	}
	
	@Override
	public void onItemClick(AdapterView<?> listView, View row, int position,
			long id) {

		SingleComment comment = app.getItem(position);

		final Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra(EXTRA_COMMENT, comment.getComment());

		startActivity(intent);

	}

	public class DownloadCompletedReceiver extends BroadcastReceiver {

		public static final String ACTION_RESP = "eu.areamobile_android_apps.logclient.DOWNLOAD_COMPLETED";

		@Override
		public void onReceive(Context context, Intent intent) {

			String response = intent
					.getStringExtra(DownloadService.PARAM_OUT_MSG);
			if (!response.equalsIgnoreCase("")) {
				Comments mCommentItems = new Gson().fromJson(response,
						Comments.class);
				updateList(mCommentItems);
			} else {
				displayError();
			}

			Log.d("DownloadCompletedReceiver", "onReceive");
		}

		

	}

	@Override
	protected void onPause() {
		
		unregisterReceiver(receiver);
		super.onPause();
	}

}
