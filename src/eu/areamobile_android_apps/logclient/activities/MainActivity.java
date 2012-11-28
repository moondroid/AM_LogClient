package eu.areamobile_android_apps.logclient.activities;

import eu.areamobile_android_apps.logclient.LogApplication;
import eu.areamobile_android_apps.logclient.R;
import eu.areamobile_android_apps.logclient.bean.Comments;
import eu.areamobile_android_apps.logclient.database.CommentsProvider;
import eu.areamobile_android_apps.logclient.database.MySQLiteHelper;
import eu.areamobile_android_apps.logclient.receivers.DownloadCompletedReceiver;
import eu.areamobile_android_apps.logclient.receivers.UploadCompletedReceiver;
import eu.areamobile_android_apps.logclient.services.DownloadService;
import eu.areamobile_android_apps.logclient.utils.TimeStampConverter;

import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends FragmentActivity implements OnClickListener,
		OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

	private Button mButtonDownload, mButtonSend;
	private ListView mCommentsList;
	private DownloadCompletedReceiver downloadReceiver;
	private UploadCompletedReceiver uploadReceiver;

	public static final String EXTRA_COMMENT = "eu.areamobile_android_apps.logclient.comment";

	private SharedPreferences prefs;
	private long lastDownloadedTimestamp;

	private SimpleCursorAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mButtonDownload = (Button) findViewById(R.id.btn_download);
		mButtonDownload.setOnClickListener(this);
		mButtonSend = (Button) findViewById(R.id.btn_addcomment);
		mButtonSend.setOnClickListener(this);

		mCommentsList = (ListView) findViewById(R.id.lv_comments);

		// connect the ListView with the content provider
		updateListFromProvider();
		
		mCommentsList.setOnItemClickListener(this);
		
		prefs = getSharedPreferences(LogApplication.LOGCLIENT_PREFERENCES,
				Context.MODE_PRIVATE);
		lastDownloadedTimestamp = prefs.getLong(
				LogApplication.LOGCLIENT_LASTDOWNLOAD, -1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {

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

	public void updateList(Comments comments) {

		lastDownloadedTimestamp = System.currentTimeMillis();
		SharedPreferences.Editor editor = prefs.edit();
		if (editor != null) {
			editor.putLong(LogApplication.LOGCLIENT_LASTDOWNLOAD,
					lastDownloadedTimestamp);
			editor.commit();
		}

		if (comments.size() > 0) {

			addCommentList(comments);

			Toast.makeText(this, comments.size() + " new comment/s",
					Toast.LENGTH_SHORT).show();

		} else {

			Toast.makeText(this, "no new comments", Toast.LENGTH_SHORT).show();

		}

	}

	
	private void  addCommentList(Comments comments) {
		for(int i=0; i<comments.size(); i++){			
			
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.COLUMN_ID, Integer.parseInt(comments.get(i).getId()));
			values.put(MySQLiteHelper.COLUMN_TIMESTAMP, comments.get(i).getTimestamp());
			values.put(MySQLiteHelper.COLUMN_SENDER, comments.get(i).getSender());
			values.put(MySQLiteHelper.COLUMN_COMMENT, comments.get(i).getComment());
			
			Uri commentUri = getContentResolver().insert(CommentsProvider.CONTENT_URI, values);
			
		}
		
	}
	
	/*
	 * Use content provider to update the comment list
	 */
	private void updateListFromProvider() {

		// Fields from the database (projection)
		// Must include the _id column for the adapter to work
		String[] from = new String[] { MySQLiteHelper.COLUMN_ID,
				MySQLiteHelper.COLUMN_TIMESTAMP,
				MySQLiteHelper.COLUMN_TIMESTAMP, MySQLiteHelper.COLUMN_SENDER,
				MySQLiteHelper.COLUMN_COMMENT };
		// Fields on the UI to which we map
		int[] to = new int[] { R.id.row_id, R.id.row_timestamp_date,
				R.id.row_timestamp_hour, R.id.row_sender, R.id.row_comment };

		getSupportLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(this, R.layout.row_layout, null,
				from, to, 0);

		adapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {

				TextView textView;
				String createDate;
				int id = view.getId();

				switch (id) {

				case R.id.row_timestamp_date:					
					createDate = TimeStampConverter.toDate(cursor
							.getString(columnIndex));
					textView = (TextView) view;
					textView.setText(createDate);
					return true;

				case R.id.row_timestamp_hour:
					createDate = TimeStampConverter.toHour(cursor
							.getString(columnIndex));
					textView = (TextView) view;
					textView.setText(createDate);
					return true;

				}

				return false;
			}

		});

		mCommentsList.setAdapter(adapter);

	}

	@Override
	public void onItemClick(AdapterView<?> listView, View row, int position,
			long id) {

		Intent i = new Intent(this, DetailActivity.class);
	    Uri todoUri = Uri.parse(CommentsProvider.CONTENT_URI + "/" + id);
	    i.putExtra(CommentsProvider.CONTENT_ITEM_TYPE, todoUri);
		
		startActivity(i);

	}
	
	// LOADER CALLBACKS

	// Creates a new loader after the initLoader () call
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { MySQLiteHelper.COLUMN_ID,
				MySQLiteHelper.COLUMN_TIMESTAMP, MySQLiteHelper.COLUMN_SENDER,
				MySQLiteHelper.COLUMN_COMMENT };
		CursorLoader cursorLoader = new CursorLoader(this,
				CommentsProvider.CONTENT_URI, projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		adapter.swapCursor(null);
	}

	public void displayError() {

		new AlertDialog.Builder(this).setTitle("Network Error")
				.setMessage("Check connection").setNeutralButton("Close", null)
				.show();
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

		filter = new IntentFilter(UploadCompletedReceiver.ACTION_RESP);
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
