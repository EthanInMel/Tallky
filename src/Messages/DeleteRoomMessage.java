package Messages;

import ChatroomServer.MessageEncoder;
import ChatroomServer.Server;
import ChatroomServer.UserInfo;

public class DeleteRoomMessage extends ClientMessageBase {
	private String roomid;
	@Override
	public void process(UserInfo user) {
		if(Server.getInstance().getChatroomManager().getChatRoom(user.getCurChatroom()).getRoominfo().getOwner().getIdentity().equals(user.getIdentity()))
		{
			SendDeleteRoomMsg();
			Server.getInstance().getChatroomManager().DeleteChatRoom(roomid);
			SendApprovalMessage(user, true);
		}
		else
		{
			SendApprovalMessage(user, false);
		}

	}
	
	private void SendApprovalMessage(UserInfo user, Boolean approval) {
		String args[] = {roomid, approval.toString()};
		String message = MessageEncoder.EncodeClientMsg("deleteroom", args);
		Server.getInstance().getChatroomManager().getChatRoom(user.getCurChatroom()).AddMessage(message, user.getChannel());
	}
	
	private void SendDeleteRoomMsg(){
		String args[] = {Server.getInstance().getServerInfo().getServerid(), roomid};
		String message = MessageEncoder.EncodeServerMsg("deleteroom", args);
		Server.getInstance().getmInterServerMessenger().BroadCastMessage(message);	
	}
	

}
