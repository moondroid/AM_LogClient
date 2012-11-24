package eu.areamobile_android_apps.logclient;

import com.google.gson.Gson;

import eu.areamobile.android.net.Http;
import eu.areamobile.android.net.Http.ResponseStream;
import eu.areamobile_android_apps.logclient.MainActivity.DownloadCompletedReceiver;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class UploadService extends IntentService {
    
	public static final String PARAM_IN_MSG_SENDER = "imsg_sender";
	public static final String PARAM_IN_MSG_COMMENT = "imsg_comment";
	
    public static final String PARAM_OUT_MSG = "omsg";
    
	private final static String COMMENT_SERVICE = "http://lab.areamobile.eu/AMRevolution/CommentAdderService";
	private final static String POST_SENDER = "POST_SENDER";
	private final static String POST_COMMENT = "POST_COMMENT";

	
	public UploadService() {
		super("UploadService");
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		String sender = intent.getStringExtra(PARAM_IN_MSG_SENDER);
		String comment = intent.getStringExtra(PARAM_IN_MSG_COMMENT);
		
		Log.d("UploadService", "onHandleIntent: "+sender+", "+comment);
		
		String response = "";
		try {
			
			response = Http.post(COMMENT_SERVICE).body(POST_SENDER, sender, POST_COMMENT, comment).execute().asString();

//			Log.i("after performUploadRegId", myResponseStream.asString());
			

		} catch (Exception e) {
			
			Log.e("Http.Requests", e.getMessage());
			e.printStackTrace();
		}
				
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(DownloadCompletedReceiver.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(PARAM_OUT_MSG, response);
		sendBroadcast(broadcastIntent);
	}
}
