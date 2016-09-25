package ChatroomServer;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Messages.ClientMessageBase;

import java.util.Set;

public class Chatroom extends Thread {
	private ChatroomInfo roominfo;

	public ChatroomInfo getRoominfo() {
		return roominfo;
	}

	private Map<SocketChannel, List<String>> messageTaskList;
	private Map<SocketChannel, UserInfo> channelToUserMap;
	private Selector selector;
	private boolean exit;

	public Chatroom(ChatroomInfo roominfo) {
		super();
		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.roominfo = roominfo;
		this.channelToUserMap = Collections.synchronizedMap(new HashMap<SocketChannel, UserInfo>());
		this.messageTaskList = Collections.synchronizedMap(new HashMap<SocketChannel, List<String>>());
		exit = false;
	}
	
	
	public void exit() {
		exit = true;
	}

	public void AddUser(UserInfo user) {
		SocketChannel channel = user.getChannel();
		if (channel != null) {
			channelToUserMap.put(channel, user);
			try {
				channel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
			} catch (ClosedChannelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	public void RemoveUser(SocketChannel channel, boolean close) {
		channelToUserMap.remove(channel);
		messageTaskList.remove(channel);
		if(close)
		{
			try {
				channel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			channel.keyFor(selector).cancel();
		}
	}

	public Collection<UserInfo> GetAllUser() {
		Collection<UserInfo> users = Collections.synchronizedList(new ArrayList<UserInfo>());
		users = channelToUserMap.values();
		return users;
	}

	public void RemoveDisconnectedUser(SocketChannel channel) {
		UserInfo disConnectedUser = channelToUserMap.get(channel);
		if(disConnectedUser.getIdentity().equals(roominfo.getOwner().getIdentity()))
		{
			Server.getInstance().getChatroomManager().DeleteChatRoom(disConnectedUser.getCurChatroom());
		}
		
		RemoveUser(channel, true);
		Server.getInstance().RemoveDisconnectedUser(disConnectedUser);
	}

	public void AddMessage(String message, SocketChannel channel) {
		List<String> messages = messageTaskList.get(channel);
		if (messages != null) {
			messages.add(message);
			messageTaskList.put(channel, messages);
		} else {
			ArrayList<String> list = new ArrayList<String>();
			list.add(message);
			messageTaskList.put(channel, list);
		}
	}

	public void BroadCastMessage(UserInfo sender, String message) {
		Iterator<SocketChannel> iter = channelToUserMap.keySet().iterator();
		while (iter.hasNext()) {		
			SocketChannel client = iter.next();
			if(sender == null || sender.getChannel() != client)
				AddMessage(message, client);
		}
	}

	@Override
	public void run() {
		while (true && exit) {
			if (channelToUserMap.size() != 0) {
				try {
					Iterator<SocketChannel> iter = channelToUserMap.keySet().iterator();

					while (iter.hasNext()) {
						SocketChannel client = iter.next();
						client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				while (true) {
					try {
						selector.select(Server.TIME_OUT);
					} catch (IOException ex) {
						ex.printStackTrace();
						break;
					}
					Set<SelectionKey> readyKeys = selector.selectedKeys();
					Iterator<SelectionKey> iterator = readyKeys.iterator();
					while (iterator.hasNext()) {
						SelectionKey key = iterator.next();
						iterator.remove();
						if (key.isValid() && key.isWritable()) {
							SocketChannel client = (SocketChannel) key.channel();
							ArrayList<String> messages = (ArrayList<String>) messageTaskList.get(client);
							if (messages != null && messages.size() != 0) {
								HelperMethods.sendMessage(client, messages.get(0));								
								messages.remove(0);
								messageTaskList.put(client, messages);
							}
						}
						if (key.isValid() && key.isReadable()) {
							SocketChannel client = (SocketChannel) key.channel();
							String message = HelperMethods.receiveMessage(client);
							if (!message.isEmpty()) {
								ClientMessageBase handler = (ClientMessageBase) MessageDecoder.DecodeClientMsg(message);
								if (null != handler) {

									handler.process(channelToUserMap.get(client));
								}

							} else {
								key.cancel();
								RemoveDisconnectedUser(client);													
							}

						}
					}
				}
			}
		}
	}
}
