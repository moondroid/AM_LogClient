package eu.areamobile_android_apps.logclient.async;

import com.google.gson.Gson;

import eu.areamobile.android.net.Http;
import eu.areamobile_android_apps.logclient.bean.Comments;
import eu.areamobile_android_apps.logclient.bean.JsonCommentsRequest;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadAsync extends AsyncTask<Context, Integer, String> {

	private final static String COMMENT_SERVICE = "http://lab.areamobile.eu/AMRevolution/CommentService.php";

	public static interface OnDownloadCompletedListener {
		public void onDownloadCompleted(Comments comments);
	}

	private OnDownloadCompletedListener mListener;

	@Override
	protected String doInBackground(Context... c) {
		// Moved to a background thread.

		try {
			mListener = (OnDownloadCompletedListener) c[0];
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"containing activity must implement OnClearListener");
		}

		String json = new Gson().toJson(new JsonCommentsRequest("-1", "-1"));

		// Return the value to be passed to onPostExecute
		String response = "";

		try {
			response = Http.Requests.json(COMMENT_SERVICE, json).asString();

		} catch (Exception e) {
		}
		return response;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		// Synchronized to UI thread.
		// Update progress bar, Notification, or other UI elements

	}

	@Override
	protected void onPostExecute(String result) {
		// Synchronized to UI thread.
		// Report results via UI update, Dialog, or notifications

		// Log.d("comments", result);

		Comments mCommentItems = new Gson().fromJson(result, Comments.class);

		for (int i = 0; i < mCommentItems.getcommentList().size(); i++) {

			Log.d("sender", mCommentItems.getcommentList().get(i).getSender());
			Log.d("comment", mCommentItems.getcommentList().get(i).getComment());
		}

		if (mListener != null) {
			mListener.onDownloadCompleted(mCommentItems);
		}
	}
}
