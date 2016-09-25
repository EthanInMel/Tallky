package Messages;
import java.util.Collection;
import java.util.List;
import java.util.Map;


import ChatroomServer.MessageEncoder;
import ChatroomServer.Server;
import ChatroomServer.UserInfo;

public class LockIdentityMessage extends ServerMessageBase {
	private String serverid;
	private String identity;
	
	@Override
	public void process() {
		String message = "";
		
		if(CheckIndentity())
		{
			String[] args = {Server.getInstance().getServerInfo().getServerid(), identity, "true"};
			Server.getInstance().AddtoLockedIdentities(identity, serverid);
			message = MessageEncoder.EncodeServerMsg(type, args);
		}
		else
		{
			String[] args = {Server.getInstance().getServerInfo().getServerid(), identity, "false"};
			message = MessageEncoder.EncodeServerMsg(type, args);
		}
		
		Server.getInstance().getmInterServerMessenger().AddMessage(message, serverid);
	}
	
	private boolean CheckIndentity()
	{
		Map<String, String> lockedIdentities = Server.getInstance().getLockedIdentity();
		if(lockedIdentities.containsKey(identity))
			return false;
		Collection<UserInfo> waitingList =  Server.getInstance().getUserIDWaitingList().keySet();
		for(UserInfo userInfo : waitingList)
		{
			if(userInfo.getIdentity().equals(identity))
			{
				return false;
			}
		}
		List<UserInfo> users = Server.getInstance().GetCurrentUser();
		for(UserInfo userInfo : users)
		{
			if(userInfo.getIdentity().equals(identity))
			{
				return false;
			}
		}
		return true;
	}
}
