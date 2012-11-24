package eu.areamobile_android_apps.logclient;

import java.util.ArrayList;

import android.app.Application;

public class LogApplication extends Application {

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
