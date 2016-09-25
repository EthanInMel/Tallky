package Messages;

import java.util.Map;

import ChatroomServer.MessageEncoder;
import ChatroomServer.Server;
import ChatroomServer.UserInfo;

public class JoinRoomMessage extends ClientMessageBase {
	private String roomid;
	@Override
	public void process(UserInfo user) {
		if(CheckAccessibility(user))
		{
			Map<String, String> rooms = Server.getInstance().getChatroomManager().GetAllChatrooms();
			if(rooms.get(roomid).equals(Server.getInstance().getServerInfo().getServerid()))
			{
				Server.getInstance().AddUserToChatroom(user, roomid);
			}
			else
			{
				Server.getInstance().getChatroomManager().LeaveRoom(roomid, user);
				Server.getInstance().getmInterServerMessenger().RouteToOtherServer(rooms.get(roomid), user, roomid);
				
				Server.getInstance().RemoveDisconnectedUser(user);
			}
		}
		else
		{
			SendDenyMessage(user);
		}
		
	}
	
	private boolean CheckAccessibility(UserInfo user) {
		if(null != user.getCurChatroom() && !Server.getInstance().getChatroomManager().getChatRoom(user.getCurChatroom()).getRoominfo().getOwner().getIdentity().equals(user.getIdentity()))
		{
			 Map<String, String> rooms = Server.getInstance().getChatroomManager().GetAllChatrooms();
			 if(rooms.containsKey(roomid))
			 {
				 return true;
			 }
			 else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	private void SendDenyMessage(UserInfo user) {
		String args[] = {user.getIdentity(), user.getCurChatroom(), user.getCurChatroom()};
		String message = MessageEncoder.EncodeClientMsg("roomchange", args);
		Server.getInstance().getChatroomManager().getChatRoom(user.getCurChatroom()).AddMessage(message, user.getChannel());
	}

}
