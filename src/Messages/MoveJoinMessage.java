package Messages;

import java.util.Map;

import ChatroomServer.HelperMethods;
import ChatroomServer.MessageEncoder;
import ChatroomServer.Server;
import ChatroomServer.UserInfo;

public class MoveJoinMessage extends ClientMessageBase {
	private String former;
	private String identity;
	private String roomid;
	
	@Override
	public void process(UserInfo user) {
		UserInfo userInfo = new UserInfo(identity, user.getChannel());
		userInfo.setCurChatroom(former);
		Server.getInstance().AddConnectedUser(userInfo);
		if(CheckRoom(roomid))
		{
			SendServerChangeMessage(userInfo);	
			Server.getInstance().AddUserToChatroom(userInfo, roomid);					
		}
		else
		{						  
			Server.getInstance().AddUserToMainHall(userInfo);
		}
		

	}
	
	private boolean CheckRoom(String roomid)
	{
		Map<String, String> rooms = Server.getInstance().getChatroomManager().GetAllChatrooms();
		if(rooms.containsKey(roomid))
			return true;
		else
			return false;
	}
	private void SendServerChangeMessage(UserInfo user)
	{
		String args[] = {"true", Server.getInstance().getServerInfo().getServerid()};
		String message = MessageEncoder.EncodeClientMsg("serverchange", args);
		HelperMethods.sendMessage(user.getChannel(), message);
	}

}
