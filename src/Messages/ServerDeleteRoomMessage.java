package Messages;

import ChatroomServer.Server;

public class ServerDeleteRoomMessage extends ServerMessageBase {
	private String serverid;
	private String roomid;
	
	@Override
	public void process() {
		Server.getInstance().getChatroomManager().DeleteRemoteChatroom(roomid,serverid);

	}

}
