package Messages;

import ChatroomServer.MessageEncoder;
import ChatroomServer.Server;
import ChatroomServer.UserInfo;

public class QuitMessage extends ClientMessageBase {

	@Override
	public void process(UserInfo user) {		
		SendDisconnectMsg(user);				
//		Server.getInstance().getChatroomManager().getChatRoom(user.getCurChatroom()).RemoveDisconnectedUser(user.getChannel());
	}
	
//	private void SendApprovalMessage(UserInfo user, Boolean approval) {
//		String args[] = {user.getCurChatroom(), approval.toString()};
//		String message = MessageEncoder.EncodeClientMsg("deleteroom", args);
//		Server.getInstance().getChatroomManager().getChatRoom(user.getCurChatroom()).AddMessage(message, user.getChannel());
//	}
	
	private void SendDisconnectMsg(UserInfo user){
		String args[] = {user.getIdentity(), user.getCurChatroom(), ""};
		String message = MessageEncoder.EncodeClientMsg("roomchange", args);
		Server.getInstance().getChatroomManager().getChatRoom(user.getCurChatroom()).AddMessage(message, user.getChannel());
	}
	
//	private void SendDeleteRoomMsg(UserInfo user){
//		String args[] = {Server.getInstance().getServerInfo().getServerid(), user.getCurChatroom()};
//		String message = MessageEncoder.EncodeServerMsg("deleteroom", args);
//		Server.getInstance().getmInterServerMessenger().BroadCastMessage(message);	
//	}
}
