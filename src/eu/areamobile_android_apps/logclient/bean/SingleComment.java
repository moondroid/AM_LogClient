package eu.areamobile_android_apps.logclient.bean;

public class SingleComment {

	private String id;
    private String timestamp;
    private String sender;
    private String comment;
    
    
	public String getId() {
		return id;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public String getSender() {
		return sender;
	}
	public String getComment() {
		return comment;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
    
    
}
