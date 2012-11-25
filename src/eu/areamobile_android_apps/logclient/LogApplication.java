package eu.areamobile_android_apps.logclient;

import eu.areamobile_android_apps.logclient.bean.Comments;
import eu.areamobile_android_apps.logclient.bean.SingleComment;

import android.app.Application;

public class LogApplication extends Application {

	public static final String LOGCLIENT_PREFERENCES = "eu.areamobile_android_apps.logclient.PREFERENCES";
	public static final String LOGCLIENT_LASTDOWNLOAD = "eu.areamobile_android_apps.logclient.LASTDOWNLOAD";

	private Comments mComments;
	
	
	public Comments getItems() {
		return mComments;
	}

	public SingleComment getItem(int position) {
		return mComments.get(position);
	}
	
	public void setItems(Comments items) {
		this.mComments = items;
	}
	
	public void addItems(Comments items) {
		for (int i=0; i<items.size(); i++){
			this.mComments.add(items.get(i));
		}
		
	}
	
}
