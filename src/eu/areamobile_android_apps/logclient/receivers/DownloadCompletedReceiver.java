package eu.areamobile_android_apps.logclient.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import eu.areamobile_android_apps.logclient.activities.MainActivity;
import eu.areamobile_android_apps.logclient.bean.Comments;
import eu.areamobile_android_apps.logclient.services.DownloadService;

public class DownloadCompletedReceiver extends BroadcastReceiver {

	public static final String ACTION_RESP = "eu.areamobile_android_apps.logclient.DOWNLOAD_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {

		MainActivity a = (MainActivity)context;
		
		String response = intent
				.getStringExtra(DownloadService.PARAM_OUT_MSG);
		if (!response.equalsIgnoreCase("")) {
			Comments mCommentItems = new Gson().fromJson(response,
					Comments.class);
			a.updateList(mCommentItems);
			
			
		} else {
			a.displayError();
		}

		Log.d("DownloadCompletedReceiverNew", "onReceive");
	}

}