package ChatroomServer;

import com.google.gson.Gson;

public class MessageEncoder {
	private static class Message {
		protected String type;

		public Message(String type) {
			super();
			this.type = type;
		}
	}

	private static class NewIdentityMessage extends Message {
		private String approved;

		public NewIdentityMessage(String type, String approval) {
			super(type);
			approved = approval;
			// TODO Auto-generated constructor stub
		}

	}

	private static class BroadcastMessage extends Message {
		private String identity;
		private String content;

		public BroadcastMessage(String type, String identity, String content) {
			super(type);
			this.identity = identity;
			this.content = content;
		}

	}

	private static class WhoMessage extends Message {
		private String roomid;
		private String[] identities;
		private String owner;

		public WhoMessage(String type, String roomid, String[] identities, String owner) {
			super(type);
			this.roomid = roomid;
			this.identities = identities;
			this.owner = owner;
		}

	}

	private static class ListMessage extends Message {
		private String[] rooms;

		public ListMessage(String type, String[] rooms) {
			super(type);
			this.rooms = rooms;
		}
	}

	private static class RoomChangeMessage extends Message {
		private String identity;
		private String former;
		private String roomid;

		public RoomChangeMessage(String type, String identity, String former, String roomid) {
			super(type);
			this.identity = identity;
			this.former = former;
			this.roomid = roomid;
		}
	}

	private static class RoomChangeApprovalMessage extends Message {
		private String roomid;
		private String approved;

		public RoomChangeApprovalMessage(String type, String roomid, String approved) {
			super(type);
			this.roomid = roomid;
			this.approved = approved;
		}

	}
	
	private static class RouteMessage extends Message {
		private String roomid;
		private String host;
		private String port;
		public RouteMessage(String type, String roomid, String host, String port) {
			super(type);
			this.roomid = roomid;
			this.host = host;
			this.port = port;
		}
	}
	
	private static class ServerChangeMessage extends Message {
		private String approved;
		private String serverid;
		public ServerChangeMessage(String type, String approved, String serverid) {
			super(type);
			this.approved = approved;
			this.serverid = serverid;
		}		
	}
	
	private static class DeleteRoomMessage extends Message {
		private String approved;
		private String roomid;
		public DeleteRoomMessage(String type, String roomid, String approved) {
			super(type);
			this.approved = approved;
			this.roomid = roomid;
		}
		
	}

	
	

	public static String EncodeClientMsg(String type, Object args) {
		String message = "";
		Gson gson = new Gson();
		if (!type.isEmpty()) {
			switch (type) {
			case "newidentity":
				NewIdentityMessage newIdentityMessage = new NewIdentityMessage(type, (String) args);
				message = gson.toJson(newIdentityMessage);
				break;
			case "message":
				BroadcastMessage broadcastMessage = new BroadcastMessage(type, ((String[]) args)[0],
						((String[]) args)[1]);
				message = gson.toJson(broadcastMessage);
				break;
			case "roomcontents":
				Object properties[] = (Object[]) args;
				WhoMessage whoMessage = new WhoMessage(type, ((String) properties[0]), ((String[]) properties[1]),
						((String) properties[2]));
				message = gson.toJson(whoMessage);
				break;
			case "roomlist":
				ListMessage listMessage = new ListMessage(type, (String[]) args);
				message = gson.toJson(listMessage);
				break;
			case "createroom":
				RoomChangeApprovalMessage roomChangeApprovalMessage = new RoomChangeApprovalMessage(type, ((String[]) args)[0], ((String[]) args)[1]);
				message = gson.toJson(roomChangeApprovalMessage);
				break;
			case "roomchange":
				RoomChangeMessage roomChangeMessage = new RoomChangeMessage(type, ((String[]) args)[0],
						((String[]) args)[1], ((String[]) args)[2]);
				message = gson.toJson(roomChangeMessage);
				break;
			case "route":
				RouteMessage routelMessage = new RouteMessage(type, ((String[]) args)[0], ((String[]) args)[1], ((String[]) args)[2]);
				message = gson.toJson(routelMessage);
				break;
			case "serverchange":
				ServerChangeMessage serverChangeMessage = new ServerChangeMessage(type, ((String[]) args)[0], ((String[]) args)[1]);
				message = gson.toJson(serverChangeMessage);
				break;
			case "deleteroom":
				DeleteRoomMessage deleteRoomMessage = new DeleteRoomMessage(type, ((String[]) args)[0], ((String[]) args)[1]);
				message = gson.toJson(deleteRoomMessage);
				break;
				
			}
		}
		return message + "\n";
	}

	private static class LockidentityMessage extends Message {
		private String serverid;
		private String identity;

		public LockidentityMessage(String type, String serverid, String identity) {
			super(type);
			this.serverid = serverid;
			this.identity = identity;
		}
	}

	private static class LockidentityApprovalMessage extends Message {
		private String serverid;
		private String identity;
		private String locked;

		public LockidentityApprovalMessage(String type, String serverid, String identity, String locked) {
			super(type);
			this.serverid = serverid;
			this.identity = identity;
			this.locked = locked;
		}
	}

	private static class ServerActivityMessage extends Message {
		private String serverid;
		private String noticed;
		
		public ServerActivityMessage(String type, String serverid, String noticed) {
			super(type);
			this.serverid = serverid;
			this.noticed = noticed;
		}
	}

	private static class ReleaseidentityMessage extends Message {
		private String serverid;
		private String identity;

		public ReleaseidentityMessage(String type, String serverid, String identity) {
			super(type);
			this.serverid = serverid;
			this.identity = identity;
		}

	}
	
	private static class LockRoomidMessage extends Message {
		private String serverid;
		private String roomid;
		public LockRoomidMessage(String type, String serverid, String roomid) {
			super(type);
			this.serverid = serverid;
			this.roomid = roomid;
		}		
	}

	private static class LockRoomidApprovalMessage extends Message {
		private String serverid;
		private String roomid;
		private String locked;
		public LockRoomidApprovalMessage(String type, String serverid, String roomid, String locked) {
			super(type);
			this.serverid = serverid;
			this.roomid = roomid;
			this.locked = locked;
		}		
	}
	
	private static class ReleaseRoomidMessage extends Message {
		private String serverid;
		private String roomid;
		private String approved;
		public ReleaseRoomidMessage(String type, String serverid, String roomid, String approved) {
			super(type);
			this.serverid = serverid;
			this.roomid = roomid;
			this.approved = approved;
		}
	}
	
	private static class ServerDeleteRoomMessage extends Message {
		private String serverid;
		private String roomid;
		public ServerDeleteRoomMessage(String type, String serverid, String roomid) {
			super(type);
			this.serverid = serverid;
			this.roomid = roomid;
		}
	}

	public static String EncodeServerMsg(String type, Object args) {
		String message = "";
		Gson gson = new Gson();
		if (!type.isEmpty()) {
			switch (type) {
			case "lockidentity":
				String strings[] = (String[]) args;
				if (strings.length == 2) {
					LockidentityMessage lockidentityMessage = new LockidentityMessage(type, strings[0], strings[1]);
					message = gson.toJson(lockidentityMessage);
				} else {
					LockidentityApprovalMessage lockidentityApprovalMessage = new LockidentityApprovalMessage(type,
							strings[0], strings[1], strings[2]);
					message = gson.toJson(lockidentityApprovalMessage);
				}
				break;
			case "activity":
				ServerActivityMessage serverActivityMessage = new ServerActivityMessage(type, ((String[]) args)[0], ((String[]) args)[1]);
				message = gson.toJson(serverActivityMessage);
				break;
			case "releaseidentity":
				ReleaseidentityMessage releaseidentityMessage = new ReleaseidentityMessage(type, ((String[]) args)[0],
						((String[]) args)[1]);
				message = gson.toJson(releaseidentityMessage);
				break;
			case "lockroomid":
				String properties[] = (String[]) args;
				if(properties.length == 2)
				{
					LockRoomidMessage lockRoomidMessage = new LockRoomidMessage(type, ((String[]) properties)[0], ((String[]) properties)[1]);
					message = gson.toJson(lockRoomidMessage);
				}
				else {
					LockRoomidApprovalMessage lockRoomidApprovalMessage = new LockRoomidApprovalMessage(type, ((String[]) properties)[0], ((String[]) properties)[1], ((String[]) properties)[2]);
					message = gson.toJson(lockRoomidApprovalMessage);
				}				
				break;
			case "releaseroomid":
				ReleaseRoomidMessage releaseRoomidMessage = new ReleaseRoomidMessage(type, ((String[]) args)[0], ((String[]) args)[1], ((String[]) args)[2]);
				message = gson.toJson(releaseRoomidMessage);
				break;
			case "deleteroom":
				ServerDeleteRoomMessage serverDeleteRoomMessage = new ServerDeleteRoomMessage(type, ((String[]) args)[0], ((String[]) args)[1]);
				message = gson.toJson(serverDeleteRoomMessage);
				break;
			}

		}
		return message + "\n";
	}
}
