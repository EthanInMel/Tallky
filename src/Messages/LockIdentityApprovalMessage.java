package Messages;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;

import ChatroomServer.HelperMethods;
import ChatroomServer.MessageEncoder;
import ChatroomServer.Server;
import ChatroomServer.UserInfo;

public class LockIdentityApprovalMessage extends ServerMessageBase {
	private String serverid;
	private String identity;
	private String locked;
	
	@Override
	public void process() {
		UserInfo user = Server.getInstance().GetUserfromWaitingList(identity);
		Map<UserInfo, List<String>> waitingList = Server.getInstance().getUserIDWaitingList();
		List<String> list = waitingList.get(user);
		if(Boolean.parseBoolean(locked))
		{								
			list.remove(serverid);
			if(list.size() == 0)		{
				Server.getInstance().AddConnectedUser(user);   // user will be removed from waitinglist when add to cur
				Server.getInstance().AddUserToMainHall(user);
				SendApprovalMessage(user.getChannel());
				SendReleaseIdentityMsg();
			}
			else
			{
				Server.getInstance().AddUsertoWatinglist(user, list);
			}
		}
		else
		{
			Server.getInstance().RemoveUserFromWaitingList(user);
			SendDenyCreationMessage(user.getChannel());
			SendReleaseIdentityMsg();
		}
	}
	
	private void SendReleaseIdentityMsg(){
		String args[] = {Server.getInstance().getServerInfo().getServerid(), identity};
		String message = MessageEncoder.EncodeServerMsg("releaseidentity", args);
		Server.getInstance().getmInterServerMessenger().BroadCastMessage(message);
	
	}

	private void SendApprovalMessage(SocketChannel channel){
		String message = MessageEncoder.EncodeClientMsg("newidentity", "true");
		HelperMethods.sendMessage(channel, message);
	
	}
	
	private void SendDenyCreationMessage(SocketChannel channel){
		String message = MessageEncoder.EncodeClientMsg("newidentity", "false");
		HelperMethods.sendMessage(channel, message);
		try {
			channel.socket().close();
			channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
