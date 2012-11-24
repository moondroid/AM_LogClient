package eu.areamobile_android_apps.logclient;

import com.google.gson.Gson;

import eu.areamobile.android.net.Http;
import eu.areamobile_android_apps.logclient.MainActivity.DownloadCompletedReceiver;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DownloadService extends IntentService {
    
	public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_OUT_MSG = "omsg";
    
	private final static String COMMENT_SERVICE = "http://lab.areamobile.eu/AMRevolution/CommentService.php";

	public DownloadService() {
		super("DownloadService");
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		String msg = intent.getStringExtra(PARAM_IN_MSG);		
		Log.d("DownloadService", "onHandleIntent: "+msg);
		
		String json = new Gson().toJson(new JsonCommentsRequest("-1", msg));

		String response = "";
		try {
			response = Http.Requests.json(COMMENT_SERVICE, json).asString();

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
