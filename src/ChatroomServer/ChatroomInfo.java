package ChatroomServer;

public class ChatroomInfo {
	private String chatroomId;
	private UserInfo owner;
	
	public ChatroomInfo(String chatroomId, UserInfo owner) {
		super();
		this.chatroomId = chatroomId;
		this.owner = owner;
	}

	public String getChatroomId() {
		return chatroomId;
	}

	public UserInfo getOwner() {
		return owner;
	}
	
}
