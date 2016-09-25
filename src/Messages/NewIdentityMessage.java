package Messages;
import java.io.IOException;
import java.nio.channels.SocketChannel;
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

public class NewIdentityMessage extends ClientMessageBase{
	private String identity;
	@Override
	public void process(UserInfo user) {
		SocketChannel channel = user.getChannel();
		if(HelperMethods.IsNameLegal(identity))
		{
			if(CheckAvailability())
			{
				String[] args = {Server.getInstance().getServerInfo().getServerid(), identity};
				String message = MessageEncoder.EncodeServerMsg("lockidentity", args);
				AddUsertoWaitinglist(channel);
				List<String> result = Server.getInstance().getmInterServerMessenger().BroadCastMessage(message);
				if(result.size() == 0)
				{
					UserInfo userinfo = new UserInfo(identity, channel);
					Server.getInstance().AddConnectedUser(userinfo);
					Server.getInstance().AddUserToMainHall(userinfo);
					SendApprovalnMessage(channel);
				}			
			}
			else
			{
				SendDenyCreationMessage(channel);
			}
		}
		else
		{
			SendDenyCreationMessage(channel);
		}
	}
	
	public void AddUsertoWaitinglist(SocketChannel channel) {		
		UserInfo user = new UserInfo(identity, channel);
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
		Server.getInstance().AddUsertoWatinglist(user, serverids);
	}

	private void SendApprovalnMessage(SocketChannel channel){
		String message = MessageEncoder.EncodeClientMsg("newidentity", "true");
		HelperMethods.sendMessage(channel, message);
	
	}
	
	private void SendDenyCreationMessage(SocketChannel channel){
		String message = MessageEncoder.EncodeClientMsg(type, "false");
		HelperMethods.sendMessage(channel, message);
		try {
			channel.socket().close();
			channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean CheckAvailability(){
		List<UserInfo> curUsers = Server.getInstance().GetCurrentUser();
		for(UserInfo  userInfo : curUsers)
		{
			if(userInfo.getIdentity().equals(identity))
				return false;
		}
		Map<String, String> lockedIdentities = Server.getInstance().getLockedIdentity();
		if(lockedIdentities.containsKey(identity))
			return false;		
		return true;
	}
}
