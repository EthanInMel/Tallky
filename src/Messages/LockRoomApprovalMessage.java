package Messages;

import java.util.List;

import ChatroomServer.MessageEncoder;
import ChatroomServer.Server;
import ChatroomServer.UserInfo;

public class LockRoomApprovalMessage extends ServerMessageBase {
	private String serverid;
	private String roomid;
	private String locked;
	
	@Override
	public void process() {
		List<String> waitingServers = Server.getInstance().getRoomIdServerWaitingList(roomid);
		UserInfo owner = Server.getInstance().getRoomIdOwner(roomid);
		if(Boolean.parseBoolean(locked))
		{			
			waitingServers.remove(serverid);
			if(waitingServers.size() == 0)
			{
				SendApprovalnMessage(owner, true);						
				SendReleaseRoomidMsg(true);
				Server.getInstance().getChatroomManager().OpenChatRoom(roomid, owner);
				Server.getInstance().AddUserToChatroom(owner, roomid);		
			}
			else
			{
				Server.getInstance().AddRoomtoWatinglist(roomid, owner, waitingServers);
			}
		}
		else
		{
			Server.getInstance().RemoveRoomidFromWaitingList(roomid);
			SendApprovalnMessage(owner, false);
			SendReleaseRoomidMsg(false);
		}
	}
	
	private void SendReleaseRoomidMsg(Boolean release) {
		String args[] = {Server.getInstance().getServerInfo().getServerid(), roomid, release.toString()};
		String message = MessageEncoder.EncodeServerMsg("releaseroomid", args);
		Server.getInstance().getmInterServerMessenger().BroadCastMessage(message);
	}
	
	private void SendApprovalnMessage(UserInfo user, Boolean approval){
		String args[] = {roomid, approval.toString()};
		String message = MessageEncoder.EncodeClientMsg("createroom", args);
		Server.getInstance().getChatroomManager().getChatRoom(user.getCurChatroom()).AddMessage(message, user.getChannel());	
	}
	

}
