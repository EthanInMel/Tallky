package Messages;

import ChatroomServer.Server;

public class ReleaseidentityMessage extends ServerMessageBase {
	private String identity;
	private String serverid;
	@Override
	public void process() {
		// TODO Auto-generated method stub
		Server.getInstance().TryReleaseIdentity(identity, serverid);
	}

}
