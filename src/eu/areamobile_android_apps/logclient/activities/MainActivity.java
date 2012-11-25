package eu.areamobile_android_apps.logclient.activities;

import eu.areamobile_android_apps.logclient.LogApplication;
import eu.areamobile_android_apps.logclient.R;
import eu.areamobile_android_apps.logclient.adapters.CommentsAdapter;
import eu.areamobile_android_apps.logclient.async.DownloadAsync.OnDownloadCompletedListener;
import eu.areamobile_android_apps.logclient.bean.Comments;
import eu.areamobile_android_apps.logclient.bean.SingleComment;
import eu.areamobile_android_apps.logclient.database.CommentsDataSource;
import eu.areamobile_android_apps.logclient.receivers.DownloadCompletedReceiver;
import eu.areamobile_android_apps.logclient.receivers.UploadCompletedReceiver;
import eu.areamobile_android_apps.logclient.services.DownloadService;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnClickListener,
		OnDownloadCompletedListener, OnItemClickListener {

	private Button mButtonDownload, mButtonSend;
	private ListView mCommentsList;
	private CommentsAdapter mCommentsAdapter;
	private LogApplication app;
	private DownloadCompletedReceiver downloadReceiver;
	private UploadCompletedReceiver uploadReceiver;

	public static final String EXTRA_COMMENT = "eu.areamobile_android_apps.logclient.comment";

	private CommentsDataSource db;
	private SharedPreferences prefs;
	private long lastDownloadedTimestamp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mButtonDownload = (Button) findViewById(R.id.btn_download);
		mButtonDownload.setOnClickListener(this);
		mButtonSend = (Button) findViewById(R.id.btn_addcomment);
		mButtonSend.setOnClickListener(this);

		mCommentsList = (ListView) findViewById(R.id.lv_comments);

		app = (LogApplication) getApplication();
		if (savedInstanceState == null) {
			app.setItems(new Comments());
		}

		mCommentsAdapter = new CommentsAdapter(this, app.getItems());
		mCommentsList.setAdapter(mCommentsAdapter);
		mCommentsList.setOnItemClickListener(this);

		// open database
		db = new CommentsDataSource(this);
		db.open();
		// db.deleteAllComments();
		if (!db.isEmpty()) {
			updateListFromDB();
		}

		prefs = getSharedPreferences(LogApplication.LOGCLIENT_PREFERENCES,
				Context.MODE_PRIVATE);
		lastDownloadedTimestamp = prefs.getLong(LogApplication.LOGCLIENT_LASTDOWNLOAD, -1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {

		// new DownloadAsync().execute(this);

		switch (v.getId()) {

		case R.id.btn_download:
			Intent downloadIntent = new Intent(this, DownloadService.class);
			downloadIntent.putExtra(DownloadService.PARAM_IN_MSG,
					String.valueOf(lastDownloadedTimestamp));
			startService(downloadIntent);

			Toast.makeText(this, "getting new comments", Toast.LENGTH_SHORT)
					.show();
			break;

		case R.id.btn_addcomment:
			Intent sendCommentIntent = new Intent(this, SendActivity.class);
			startActivity(sendCommentIntent);
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

	public void updateList(Comments comments) {

		lastDownloadedTimestamp = System.currentTimeMillis();
		SharedPreferences.Editor editor = prefs.edit();
		if (editor != null) {
			editor.putLong(LogApplication.LOGCLIENT_LASTDOWNLOAD, lastDownloadedTimestamp);
			editor.commit();
		}

		if (comments.size() > 0) {

			app.addItems(comments);
			db.addCommentList(comments);

			mCommentsAdapter.refresh(app.getItems());
			mCommentsAdapter.notifyDataSetChanged();

			Toast.makeText(this, comments.size() + " new comment/s",
					Toast.LENGTH_SHORT).show();

		} else {

			Toast.makeText(this, "no new comments", Toast.LENGTH_SHORT).show();

		}

	}

	private void updateListFromDB() {

		Comments comments = db.getAllComments();
		app.setItems(comments);

		mCommentsAdapter.refresh(comments);
		mCommentsAdapter.notifyDataSetChanged();

		Toast.makeText(this, "displaying comments from db", Toast.LENGTH_SHORT)
				.show();
	}

	public void displayError() {

		new AlertDialog.Builder(this).setTitle("Network Error")
				.setMessage("Check connection").setNeutralButton("Close", null)
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

		

	@Override
	protected void onResume() {

		super.onResume();

		// register receivers for completing download and upload
		IntentFilter filter = new IntentFilter(
				DownloadCompletedReceiver.ACTION_RESP);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		downloadReceiver = new DownloadCompletedReceiver();
		registerReceiver(downloadReceiver, filter);

		filter = new IntentFilter(
				UploadCompletedReceiver.ACTION_RESP);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		uploadReceiver = new UploadCompletedReceiver();
		registerReceiver(uploadReceiver, filter);
	}

	@Override
	protected void onPause() {

		unregisterReceiver(downloadReceiver);
		unregisterReceiver(uploadReceiver);
		super.onPause();
	}

}
