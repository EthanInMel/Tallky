package ChatroomServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
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
import java.util.Set;

import Messages.ServerMessageBase;

public class InterServerMessenger extends Thread {
	private ArrayList<ServerInfo> serversInfo;
	private Map<SocketChannel, List<String>> messageTaskList;
	private HashMap<String, SocketChannel> idToChannelrMap;
	private HashMap<String, ServerInfo> serversInfoMap;
	private HashMap<String, Boolean> serverActivityMap;
	private Selector selector;
	public HashMap<String, Boolean> getServerActivityMap() {
		return serverActivityMap;
	}

	public InterServerMessenger(ArrayList<ServerInfo> serversInfo, String serverid) {
		super();
		this.serversInfo = serversInfo;
		this.serversInfoMap = new HashMap<String, ServerInfo>();
		this.serverActivityMap = new HashMap<String, Boolean>();
		this.messageTaskList = Collections.synchronizedMap(new HashMap<SocketChannel, List<String>>());
		this.idToChannelrMap = new HashMap<String, SocketChannel>();
		try {
			selector = Selector.open();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String args[] = {serverid, "false"};
		String message = MessageEncoder.EncodeServerMsg("activity", args);
		for (ServerInfo serverInfo : serversInfo) 
		{
			serversInfoMap.put(serverInfo.getServerid(), serverInfo);			
			try {
				serverActivityMap.put(serverInfo.getServerid(), false);	
				SocketAddress address = new InetSocketAddress(serverInfo.getHostAddress(), serverInfo.getServerPort());
				SocketChannel channel =  SocketChannel.open(address);
				channel.configureBlocking(false);
				AddMessage(message, channel);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();				
			}
				
		}		
		
	}
	
	public void RouteToOtherServer(String serverid, UserInfo user, String roomid) {
		ServerInfo serverInfo = serversInfoMap.get(serverid);
		String host = serverInfo.getHostAddress().toString().replaceAll("/", "");
		String args[] = {roomid, host, String.valueOf(serverInfo.getClientPort())};
		String message = MessageEncoder.EncodeClientMsg("route", args);
		HelperMethods.sendMessage(user.getChannel(), message);
	}

	public boolean AddMessage(String message, String serverid) {
		SocketChannel channel = idToChannelrMap.get(serverid);
		if (null != channel && channel.isOpen()) {
			AddMessage(message, channel);
			return true;
		} else {
			ServerInfo serverInfo = serversInfoMap.get(serverid);
			try {
				SocketAddress address = new InetSocketAddress(serverInfo.getHostAddress(), serverInfo.getServerPort());
				channel = SocketChannel.open(address);
				channel.configureBlocking(false);
				AddMessage(message, channel);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	}

	private void AddMessage(String message, SocketChannel channel) {
		List<String> messages = messageTaskList.get(channel);
		if (messages != null) {
			messages.add(message);
			messageTaskList.put(channel, messages);
		} else {
			ArrayList<String> list = new ArrayList<String>();
			list.add(message);
			messageTaskList.put(channel, list);
		}
		try {
			channel.register(selector, SelectionKey.OP_WRITE);
		} catch (ClosedChannelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println(message);
		}
	}

	public List<String> BroadCastMessage(String message) {
		List<String> result = new ArrayList<String>();
		for (ServerInfo serverInfo : serversInfo) {
			SocketChannel channel = idToChannelrMap.get(serverInfo.getServerid());
			if(null == channel || !channel.isOpen())
			{
				if(serverActivityMap.get(serverInfo.getServerid()) != false)
				{
					SocketAddress address = new InetSocketAddress(serverInfo.getHostAddress(), serverInfo.getServerPort());
					try {
						channel = SocketChannel.open(address);
						channel.configureBlocking(false);
						idToChannelrMap.put(serverInfo.getServerid(), channel);
						AddMessage(message, channel);
						result.add(serverInfo.getServerid());
					} catch (IOException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
//						System.err.println(message);
//						System.err.println(address);
					}	
				}											
			}
			else
			{
				AddMessage(message, channel);
				result.add(serverInfo.getServerid());
			}
		}
		return result;
	}

	@Override
	public void run() {
		ServerSocketChannel serverSocketChannel;
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.bind(new InetSocketAddress(Server.getInstance().getServerInfo().getServerPort()));
			serverSocketChannel.configureBlocking(false);
			
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
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
				try {
					if (key.isValid() && key.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel) key.channel();
						SocketChannel client = server.accept();
						System.out.println("Accepted connection from " + client);
						client.configureBlocking(false);
						client.register(selector, SelectionKey.OP_READ);
					} else if (key.isValid() && key.isReadable()) {
						SocketChannel client = (SocketChannel) key.channel();
						String message = HelperMethods.receiveMessage(client);
						if(!message.isEmpty())
						{
							ServerMessageBase handler = (ServerMessageBase) MessageDecoder.DecodeServerMsg(message);
							if (null != handler)
								handler.process();
						}
						client.close();
					}
					else if (key.isValid() && key.isWritable()) {
						SocketChannel client = (SocketChannel) key.channel();
						ArrayList<String> messages = (ArrayList<String>) messageTaskList.get(client);
						if (messages != null && messages.size() != 0) {
							HelperMethods.sendMessage(client, messages.get(0));
							messages.remove(0);
							messageTaskList.put(client, messages);
						}
						else
						{
							client.close();
						}
					}

				} catch (IOException ex) {
					try {
						key.channel().close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					key.cancel(); 
				}
			}
		}
	}
}
