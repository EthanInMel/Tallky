package ChatroomServer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import Messages.ClientMessageBase;

public class Server {
	
	private static Server single; 
	public static int TIME_OUT = 100;
	private String mainHallID;
	private ServerInfo mServerInfo;
	private List<UserInfo> curConnectedUser;
	private ChatroomManager chatroomManager;
	private ArrayList<ServerInfo> serversInfo;
	private Map<String, String> lockedIdentities;
	private Map<String, String> lockedRoomIds;
	private Map<UserInfo, List<String>> userIdWaitingList;
	private Map<String, UserInfo> waitingRoomidOwnerMap;
	private Map<String, List<String>> roomIdWaitingList;
	private InterServerMessenger interServerMessenger;

	public Server(String id, String path) {
		if(single != null)
			return;
		serversInfo = new ArrayList<ServerInfo>();
		ReadServerInfo(id, path);
		mainHallID = "MainHall-"+ mServerInfo.getServerid();
		curConnectedUser = Collections.synchronizedList(new ArrayList<UserInfo>());
		lockedIdentities = Collections.synchronizedMap(new HashMap<String, String>());
		lockedRoomIds = Collections.synchronizedMap(new HashMap<String, String>());
		userIdWaitingList = Collections.synchronizedMap(new HashMap<UserInfo, List<String>>());
		roomIdWaitingList = Collections.synchronizedMap(new HashMap<String, List<String>>());
		waitingRoomidOwnerMap = Collections.synchronizedMap(new HashMap<String, UserInfo>());
		chatroomManager = new ChatroomManager();
		interServerMessenger = new InterServerMessenger(serversInfo, id);
		single = this;
		UserInfo user = new UserInfo("", null);				
		chatroomManager.OpenChatRoom(mainHallID, user);
	}
	
	public Map<String, String> getLockedRoomIds() {
		return lockedRoomIds;
	}
	
	public InterServerMessenger getmInterServerMessenger() {
		return interServerMessenger;
	}

	public static Server getInstance() {  
        return single;  
    }  
	
	public ServerInfo getServerInfo() {
		return mServerInfo;
	}
	
	public Map<String, String> getLockedIdentity() {
		return lockedIdentities;
	}

	public Map<UserInfo, List<String>> getUserIDWaitingList() {
		return userIdWaitingList;
	}
		
	public List<UserInfo> GetCurrentUser(){
		return curConnectedUser;
	}
	
	public List<String> getRoomIdServerWaitingList(String roomid) {
		return roomIdWaitingList.get(roomid);
	}
	
	public UserInfo getRoomIdOwner(String roomid) {
		return waitingRoomidOwnerMap.get(roomid);
	}

	private void ReadServerInfo(String id, String path)
	{
		Scanner input = null;
		
		try {
			input = new Scanner(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			System.out.println(path +" not found.");
			System.exit(0);
		}
		while (input.hasNextLine())
		{
			String line = input.nextLine();
			String[] args = line.split("\t");
			InetAddress address = null;
			try {
				address = InetAddress.getByName(args[1]);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ServerInfo serverInfo = new ServerInfo(args[0], address, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			
			if(args[0].equals(id))
			{
				mServerInfo = serverInfo;
			}
			else
			{
				serversInfo.add(serverInfo);
			}
		}
	}
	
	public void run()
	{		
		interServerMessenger.start();
		ServerSocketChannel serverSocketChannel;
		Selector selector;
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.bind(new InetSocketAddress(mServerInfo.getClientPort()));
			serverSocketChannel.configureBlocking(false);

			selector = Selector.open();
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		while (true) {
			try {
				selector.select(TIME_OUT);
			} catch (IOException ex) {
				ex.printStackTrace();
				break;
			}
			Set<SelectionKey> readyKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = readyKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				iterator.remove();
				try {
					if (key.isValid() && key.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel client = server.accept();
						System.out.println("Accepted connection from " + client);
						client.configureBlocking(false);
						client.register(selector, SelectionKey.OP_READ);
					}
					else if(key.isValid() && key.isReadable())
					{
						SocketChannel client = (SocketChannel)key.channel();
						String message = HelperMethods.receiveMessage(client);
						ClientMessageBase handler = (ClientMessageBase)MessageDecoder.DecodeClientMsg(message);
						UserInfo tempInfo = new UserInfo("", client);
						if(null != handler)
							handler.process(tempInfo);
						key.cancel();						
					}

				} 
				catch (IOException ex) {
					key.cancel();
					try {
						key.channel().close();
					} 
					catch (IOException cex) {
					}
				}
			}
		}
	}
	
	
	public void AddUsertoWatinglist(UserInfo user, List<String> waitingServerid) {
		userIdWaitingList.put(user, waitingServerid);
	}
	
	public void AddRoomtoWatinglist(String roomid, UserInfo user, List<String> waitingServerid) {
		roomIdWaitingList.put(roomid, waitingServerid);
		waitingRoomidOwnerMap.put(roomid, user);
	}
	
	public void AddtoLockedIdentities(String identity, String serverid)
	{
		lockedIdentities.put(identity, serverid);
	}
	
	public void AddConnectedUser(UserInfo user) {
		userIdWaitingList.remove(user);
		curConnectedUser.add(user);
	}
	
	public UserInfo GetUserfromWaitingList(String identity) {
		UserInfo user = null;
		for(UserInfo userinfo : userIdWaitingList.keySet())
		{
			if(userinfo.getIdentity().equals(identity))
			{
				user = userinfo;
				break;
			}			
		}
		return user;			
	}
	
	public void RemoveUserFromWaitingList(UserInfo user) {
		userIdWaitingList.remove(user);
	}
	
	public void RemoveRoomidFromWaitingList(String roomid) {
		waitingRoomidOwnerMap.remove(roomid);
		roomIdWaitingList.remove(roomid);
	}
	
	public void RemoveDisconnectedUser(UserInfo user)
	{
		System.out.println(user.getIdentity() + " disconnected...");
		curConnectedUser.remove(user);
	}
	
	public void LockRoomId(String roomid, String serverid) {		
		lockedRoomIds.put(roomid, serverid);
	}
	
	
	public void AddUserToChatroom(UserInfo user, String roomid) {		
		chatroomManager.EnterChatRoom(roomid, user);
	}
	
	public ChatroomManager getChatroomManager() {
		return chatroomManager;
	}

	public void AddUserToMainHall(UserInfo user) {
		AddUserToChatroom(user, mainHallID);
	}
	
	public void TryReleaseIdentity(String identity, String serverid) {
		if(lockedIdentities.get(identity).equals(serverid))
			lockedIdentities.remove(identity);
	}
	
	public boolean TryReleaseRoomid(String roomid, String serverid) {
		if(lockedRoomIds.get(roomid).equals(serverid))
		{
			lockedIdentities.remove(roomid);
			return true;
		}
		return false;
	}
}
