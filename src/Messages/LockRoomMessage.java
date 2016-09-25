package Messages;

import java.util.Map;


import ChatroomServer.MessageEncoder;
import ChatroomServer.Server;

public class LockRoomMessage extends ServerMessageBase {
	private String serverid;
	private String roomid;
	@Override
	public void process() {
		Map<String, String> rooms = Server.getInstance().getChatroomManager().GetAllChatrooms();
		String message = "";
		for(String chatroom : rooms.keySet())
		{
			if(chatroom.equals(roomid))
			{
				String args[] = {Server.getInstance().getServerInfo().getServerid(), roomid, "false"};
				message = MessageEncoder.EncodeServerMsg(type, args);				
			}
		}
		Map<String, String> lockedRooms = Server.getInstance().getLockedRoomIds();
		for(String id : lockedRooms.keySet())
		{
			if(id.equals(roomid))
			{
				String args[] = {Server.getInstance().getServerInfo().getServerid(), roomid, "false"};
				message = MessageEncoder.EncodeServerMsg(type, args);
			}
		}
		if(message.isEmpty())
		{
			Server.getInstance().LockRoomId(roomid, serverid);
			String args[] = {Server.getInstance().getServerInfo().getServerid(), roomid, "true"};
			message = MessageEncoder.EncodeServerMsg(type, args);
		}
		Server.getInstance().getmInterServerMessenger().AddMessage(message, serverid);
		
		
	}
	
	

}


