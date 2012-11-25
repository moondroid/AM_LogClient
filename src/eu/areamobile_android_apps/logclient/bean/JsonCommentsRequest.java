package eu.areamobile_android_apps.logclient.bean;

public class JsonCommentsRequest {
	private String id;
	private String timestamp;

	public JsonCommentsRequest(String id, String timestamp) {
		this.id = id;
		this.timestamp = timestamp;
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
