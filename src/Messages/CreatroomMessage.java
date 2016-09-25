package Messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ChatroomServer.HelperMethods;
import ChatroomServer.MessageEncoder;
import ChatroomServer.Server;
import ChatroomServer.UserInfo;

public class CreatroomMessage extends ClientMessageBase{
	private String roomid;
	@Override
	public void process(UserInfo user) {
		if(HelperMethods.IsNameLegal(roomid))
		{
			if(!user.getCurChatroom().isEmpty() && Server.getInstance().getChatroomManager().getChatRoom(user.getCurChatroom()).getRoominfo().getOwner().getIdentity().equals(user.getIdentity()))
			{				
				SendApprovalnMessage(user, false);
			}
			else
			{
				if(CheckLockedId())
				{
					List<String> sended = SendLockidMessages();
					if(sended.size() != 0)
						AddRoomtoWaitinglist(user);
					else{
						SendApprovalnMessage(user, true);						
						Server.getInstance().getChatroomManager().OpenChatRoom(roomid, user);
						Server.getInstance().AddUserToChatroom(user, roomid);		
					}
					
				}
				else
				{
					SendApprovalnMessage(user, false);
				}
			}
		}
		else
		{
			SendApprovalnMessage(user, false);
		}
		
	}
	
	private boolean CheckLockedId() {	
		Map<String, String> lockedIds = Server.getInstance().getLockedRoomIds();
		if(lockedIds.containsKey(roomid))
		{
			return false;
		}
		Map<String, String> chatrooms = Server.getInstance().getChatroomManager().GetAllChatrooms();
		if(chatrooms.keySet().contains(roomid))
		{
			return false;
		}	
		return true;
	}
	
	private void AddRoomtoWaitinglist(UserInfo user) {		
		HashMap<String, Boolean> activatedServer = Server.getInstance().getmInterServerMessenger().getServerActivityMap();
		List<String> serverids = new ArrayList<String>();
		Iterator<Entry<String, Boolean>> iter = activatedServer.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Map.Entry<String, Boolean> entry = (Map.Entry<String, Boolean>) iter.next();
			Boolean isOnline = entry.getValue();
			if(isOnline)
			{
				serverids.add(entry.getKey());
			}					
		}
		Server.getInstance().AddRoomtoWatinglist(roomid, user, serverids);
	}
	
	private void SendApprovalnMessage(UserInfo user, Boolean approval){
		String args[] = {roomid, approval.toString()};
		String message = MessageEncoder.EncodeClientMsg(type, args);
		Server.getInstance().getChatroomManager().getChatRoom(user.getCurChatroom()).AddMessage(message, user.getChannel());
	}
	
	private List<String> SendLockidMessages() {
		String args[] = {Server.getInstance().getServerInfo().getServerid(), roomid};
		String message = MessageEncoder.EncodeServerMsg("lockroomid", args);
		List<String> sended = Server.getInstance().getmInterServerMessenger().BroadCastMessage(message);
		return sended;
	}

}
