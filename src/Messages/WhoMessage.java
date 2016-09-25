package Messages;

import java.util.ArrayList;
import java.util.Collection;

import ChatroomServer.Chatroom;
import ChatroomServer.MessageEncoder;
import ChatroomServer.Server;
import ChatroomServer.UserInfo;
public class WhoMessage extends ClientMessageBase {

	@Override
	public void process(UserInfo user) {
		Chatroom chatroom = Server.getInstance().getChatroomManager().getChatRoom(user.getCurChatroom());
		if(null != chatroom)
		{
			Collection<UserInfo> users = chatroom.GetAllUser();
			ArrayList<String> names =  new ArrayList<String>();
			for(UserInfo userinfo : users)
			{
				names.add(userinfo.getIdentity());
			}
			String[] array = (String[]) names.toArray(new String[names.size()]);
			Object args[] = {chatroom.getRoominfo().getChatroomId(), array, chatroom.getRoominfo().getOwner().getIdentity()};
			String message = MessageEncoder.EncodeClientMsg("roomcontents", args);
			if(null != chatroom)
				chatroom.AddMessage(message, user.getChannel());
		}

	}

}
