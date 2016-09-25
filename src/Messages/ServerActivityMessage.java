package Messages;

import java.util.HashMap;
import ChatroomServer.*;

public class ServerActivityMessage extends ServerMessageBase {
	private String serverid;
	private String noticed;
	
	@Override
	public void process() {
		HashMap<String, Boolean> serverActivityMap = Server.getInstance().getmInterServerMessenger().getServerActivityMap();
		Boolean activated = serverActivityMap.get(serverid);
		if(!Boolean.parseBoolean(noticed))
		{
			String args[] = {Server.getInstance().getServerInfo().getServerid(), "true"};
			String message = MessageEncoder.EncodeServerMsg("activity", args);
			Server.getInstance().getmInterServerMessenger().AddMessage(message, serverid);
		}
		if(activated == false)
		{
			serverActivityMap.put(serverid, true);
			Server.getInstance().getChatroomManager().addRemoteChatroom("MainHall-" + serverid, serverid);
		}

	}

}
