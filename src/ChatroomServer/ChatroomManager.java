package ChatroomServer;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class ChatroomManager{
	private Map<String, Chatroom> chatroomList;
	private Map<String, String> chatroomToServerMap;
	private Chatroom mainHall;
	
	public ChatroomManager() {
		this.chatroomList = Collections.synchronizedMap(new HashMap<String, Chatroom>());
		this.chatroomToServerMap = Collections.synchronizedMap(new HashMap<String, String>());
	}
	
	public Chatroom getChatRoom(String roomid) {
		return chatroomList.get(roomid);
	}
	
	public Map<String, String> GetAllChatrooms() {		
		return chatroomToServerMap;
	}
	
	public String getChattoomServer(String roomid) {
		return chatroomToServerMap.get(roomid);
	}
	
	public Chatroom getMailHall() {
		return mainHall;
	}
	
	public void addRemoteChatroom(String roomid, String serverid) {
		chatroomToServerMap.put(roomid, serverid);
	}
	
	public void DeleteRemoteChatroom(String roomid, String serverid) {
		if(chatroomToServerMap.get(roomid).equals(serverid))
		{
			chatroomToServerMap.remove(roomid);
		}
	}
	
	public void OpenChatRoom(String roomid, UserInfo owner) {			
		ChatroomInfo info = new ChatroomInfo(roomid, owner);
		Chatroom chatroom = new Chatroom(info);
		chatroom.start();
		chatroomList.put(roomid, chatroom);
		chatroomToServerMap.put(roomid, Server.getInstance().getServerInfo().getServerid());
		if(mainHall == null)
		{
			mainHall = chatroom;
		}	
	}	
	
	public String LeaveRoom(String roomid, UserInfo user)
	{
		Chatroom formerRoom = chatroomList.get(user.getCurChatroom());
		String formerId = "";
		if(formerRoom != null)
		{
			formerRoom.RemoveUser(user.getChannel(), false);	
			formerId = user.getCurChatroom();
			String args[] = {user.getIdentity(), formerId, roomid};
			String message = MessageEncoder.EncodeClientMsg("roomchange", args);
			formerRoom.BroadCastMessage(null, message);
		}
		return formerId;
	}
	
	public void DeleteChatRoom(String roomid) {
		Chatroom chatroom = chatroomList.get(roomid);
		UserInfo users[] = chatroom.GetAllUser().toArray(new UserInfo[ chatroom.GetAllUser().size()]);
		
		for(UserInfo userInfo : users)
		{
			LeaveRoom(mainHall.getRoominfo().getChatroomId(), userInfo);
		}
		for(UserInfo userInfo : users)
		{
			EnterChatRoom(mainHall.getRoominfo().getChatroomId(), userInfo);
		}
		chatroomToServerMap.remove(roomid);
		chatroomList.remove(chatroom);
		chatroom.exit();
	}
	
	public void EnterChatRoom(String roomid, UserInfo user) {		
		String formerRoom = LeaveRoom(roomid, user);
		Chatroom room = chatroomList.get(roomid);		 
		String args[] = {user.getIdentity(), formerRoom, roomid};
		String message = MessageEncoder.EncodeClientMsg("roomchange", args);
		user.setCurChatroom(roomid);
		room.AddUser(user);
		room.BroadCastMessage(null, message);
	}

}
