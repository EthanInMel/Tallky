package Messages;
import java.util.Map;

import ChatroomServer.Chatroom;
import ChatroomServer.MessageEncoder;
import ChatroomServer.Server;
import ChatroomServer.UserInfo;

public class ListMessage extends ClientMessageBase {

	@Override
	public void process(UserInfo user) {
		// TODO Auto-generated method stub
		Map<String, String> infos = Server.getInstance().getChatroomManager().GetAllChatrooms();
		String names[] = infos.keySet().toArray(new String[infos.size()]);	
		String message = MessageEncoder.EncodeClientMsg("roomlist", names);
		
		Chatroom chatroom = Server.getInstance().getChatroomManager().getChatRoom(user.getCurChatroom());
		if(null != chatroom)
			chatroom.AddMessage(message, user.getChannel());
	}

}
