package eu.areamobile_android_apps.logclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

public class DetailActivity extends Activity {

	private TextView mComment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_layout);

		Intent i = getIntent();
		String comment = i.getStringExtra(MainActivity.EXTRA_COMMENT);

		mComment = (TextView) findViewById(R.id.tv_comment);
		mComment.setText(comment);

	}

}
