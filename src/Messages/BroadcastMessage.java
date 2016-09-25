
package Messages;

import ChatroomServer.MessageEncoder;
import ChatroomServer.Server;
import ChatroomServer.UserInfo;

public class BroadcastMessage extends ClientMessageBase {
	private String content;
	
	@Override
	public void process(UserInfo user) {
		if(!content.isEmpty())
		{
			String[] args = {user.getIdentity(), content};
			String message = MessageEncoder.EncodeClientMsg(type, args);
			Server.getInstance().getChatroomManager().getChatRoom(user.getCurChatroom()).BroadCastMessage(user, message);
		}

	}

}
