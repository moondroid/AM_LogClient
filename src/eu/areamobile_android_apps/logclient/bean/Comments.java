package eu.areamobile_android_apps.logclient.bean;

import java.util.ArrayList;
import java.util.List;

public class Comments {

	private ArrayList<SingleComment> commentList;

	public Comments(){
	
		commentList = new ArrayList<SingleComment>();
	}
	
	public List<SingleComment> getcommentList() {
		return commentList;
	}
	
	public int size(){
		return commentList.size();
	}
	
	public void add(SingleComment c){
		
		commentList.add(c);
	}
	
	public SingleComment get(int position){
		return commentList.get(position);
	}
}



