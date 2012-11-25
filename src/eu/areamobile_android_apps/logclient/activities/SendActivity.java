package eu.areamobile_android_apps.logclient.activities;


import eu.areamobile_android_apps.logclient.R;
import eu.areamobile_android_apps.logclient.R.id;
import eu.areamobile_android_apps.logclient.R.layout;
import eu.areamobile_android_apps.logclient.services.DownloadService;
import eu.areamobile_android_apps.logclient.services.UploadService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/*
 * 
 */
public class SendActivity extends Activity implements OnClickListener {

	private Button mButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_layout);
		
		mButton=(Button)findViewById(R.id.btn_send);
		mButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		
		EditText etSender = (EditText)findViewById(R.id.et_sender);
		EditText etComment = (EditText)findViewById(R.id.et_comment);
		
		Intent uploadIntent = new Intent(this, UploadService.class);
		uploadIntent.putExtra(UploadService.PARAM_IN_MSG_SENDER,
				etSender.getText().toString());
		uploadIntent.putExtra(UploadService.PARAM_IN_MSG_COMMENT,
				etComment.getText().toString());
		startService(uploadIntent);
		
		
//		Intent i = new Intent();
//		setResult(RESULT_OK, i);
		finish();
	}
	
}
