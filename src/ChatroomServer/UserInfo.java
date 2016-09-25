package ChatroomServer;
import java.nio.channels.SocketChannel;

public class UserInfo {
	private String identity;
	private String curChatroom;
	private SocketChannel channel;
	
	public UserInfo(String identity, SocketChannel channel) {
		super();
		this.identity = identity;
		this.channel = channel;
	}
	public String getCurChatroom() {
		return curChatroom;
	}
	public void setCurChatroom(String roomid) {
		this.curChatroom = roomid;
	}
	public String getIdentity() {
		return identity;
	}
	public SocketChannel getChannel() {
		return channel;
	}
	
	
}
