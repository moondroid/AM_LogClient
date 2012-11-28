package eu.areamobile_android_apps.logclient.activities;

import eu.areamobile_android_apps.logclient.R;
import eu.areamobile_android_apps.logclient.R.id;
import eu.areamobile_android_apps.logclient.R.layout;
import eu.areamobile_android_apps.logclient.database.CommentsProvider;
import eu.areamobile_android_apps.logclient.database.MySQLiteHelper;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

public class DetailActivity extends Activity {

	private Uri todoUri;
	private TextView mComment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_layout);

		mComment = (TextView) findViewById(R.id.tv_comment);

		Bundle extras = getIntent().getExtras();

		// Check from the saved Instance
		todoUri = (savedInstanceState == null) ? null
				: (Uri) savedInstanceState
						.getParcelable(CommentsProvider.CONTENT_ITEM_TYPE);

		// Or passed from the other activity
		if (extras != null) {
			todoUri = extras.getParcelable(CommentsProvider.CONTENT_ITEM_TYPE);

		}

		fillData(todoUri);

	}

	/*
	 * Fill the comment details
	 */
	private void fillData(Uri uri) {
		String[] projection = { MySQLiteHelper.COLUMN_ID,
				MySQLiteHelper.COLUMN_TIMESTAMP, MySQLiteHelper.COLUMN_SENDER,
				MySQLiteHelper.COLUMN_COMMENT };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();

			mComment.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_COMMENT)));
			
			cursor.close();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(CommentsProvider.CONTENT_ITEM_TYPE, todoUri);
	}

}
