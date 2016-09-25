package Messages;
import ChatroomServer.Server;

public class ReleaseRoomidMessage extends ServerMessageBase {
	private String serverid;
	private String roomid;
	private String approved;
	@Override
	public void process() {
		// TODO Auto-generated method stub
		if(Boolean.parseBoolean(approved))
		{
			boolean result = Server.getInstance().TryReleaseRoomid(roomid, serverid);
			if(result)
			{
				Server.getInstance().getChatroomManager().addRemoteChatroom(roomid, serverid);
			}
		}
	}

}
