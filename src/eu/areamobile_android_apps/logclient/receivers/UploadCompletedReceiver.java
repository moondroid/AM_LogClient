package eu.areamobile_android_apps.logclient.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import eu.areamobile_android_apps.logclient.LogApplication;
import eu.areamobile_android_apps.logclient.activities.MainActivity;
import eu.areamobile_android_apps.logclient.services.DownloadService;

public class UploadCompletedReceiver extends BroadcastReceiver {

	public static final String ACTION_RESP = "eu.areamobile_android_apps.logclient.UPLOAD_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {

		MainActivity a = (MainActivity)context;
		
		String response = intent
				.getStringExtra(DownloadService.PARAM_OUT_MSG);
		if (!response.equalsIgnoreCase("")) {

			Toast.makeText(context, "new comment added", Toast.LENGTH_SHORT)
					.show();
			
			SharedPreferences prefs = context.getSharedPreferences(LogApplication.LOGCLIENT_PREFERENCES,
					Context.MODE_PRIVATE);
			long lastDownloadedTimestamp = prefs.getLong(LogApplication.LOGCLIENT_LASTDOWNLOAD, -1);
			
			Intent downloadIntent = new Intent(context, DownloadService.class);
			downloadIntent.putExtra(DownloadService.PARAM_IN_MSG,
					String.valueOf(lastDownloadedTimestamp));
			context.startService(downloadIntent);

		} else {
			a.displayError();
		}

		Log.d("UploadCompletedReceiver", "onReceive");
	}

}
