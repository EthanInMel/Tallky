package Messages;

import ChatroomServer.UserInfo;

public abstract class ClientMessageBase extends MessageBase{

	public abstract void process(UserInfo user);
}

