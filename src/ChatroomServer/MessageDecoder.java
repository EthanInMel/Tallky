package ChatroomServer;

import java.io.IOException;
import java.io.StringReader;
import Messages.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class MessageDecoder {

	public static MessageBase DecodeClientMsg(String message) {
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new StringReader(message));
		MessageBase messageProcessor = null;
		try {
			reader.beginObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // throws IOException
		try {
			while (reader.hasNext()) {
				String s = reader.nextName();
				String property = reader.nextString();
				if (!s.isEmpty() && s.equals("type")) {
					switch (property) {
					case "newidentity":
						messageProcessor = gson.fromJson(message, NewIdentityMessage.class);
						break;
					case "lockidentity":
						messageProcessor = gson.fromJson(message, LockIdentityMessage.class);
						break;
					case "releaseidentity":
						messageProcessor = gson.fromJson(message, ReleaseidentityMessage.class);
						break;
					case "list":
						messageProcessor = gson.fromJson(message, ListMessage.class);
						break;
					case "message":
						messageProcessor = gson.fromJson(message, BroadcastMessage.class);
						break;
					case "who":
						messageProcessor = gson.fromJson(message, WhoMessage.class);
						break;
					case "createroom":
						messageProcessor = gson.fromJson(message, CreatroomMessage.class);
						break;
					case "join":
						messageProcessor = gson.fromJson(message, JoinRoomMessage.class);
						break;
					case "movejoin":
						messageProcessor = gson.fromJson(message, MoveJoinMessage.class);
						break;
					case "deleteroom":
						messageProcessor = gson.fromJson(message, DeleteRoomMessage.class);
						break;	
					case "quit":
						messageProcessor = gson.fromJson(message, QuitMessage.class);
						break;	
					}
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return messageProcessor;
	}

	public static MessageBase DecodeServerMsg(String message) {
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new StringReader(message));
		MessageBase messageProcessor = null;
		try {
			reader.beginObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // throws IOException
		try {
			while (reader.hasNext()) {
				String s = reader.nextName();
				String property = reader.nextString();
				if (!s.isEmpty() && s.equals("type")) {
					switch (property) {
					case "lockidentity":
						if(message.contains("locked"))
							messageProcessor = gson.fromJson(message, LockIdentityApprovalMessage.class);
						else
						{
							messageProcessor = gson.fromJson(message, LockIdentityMessage.class);
						}							
						break;
					case "releaseidentity":
						messageProcessor = gson.fromJson(message, ReleaseidentityMessage.class);
						break;
					case "createroom":
						messageProcessor = gson.fromJson(message, CreatroomMessage.class);
						break;
					case "activity":
						messageProcessor = gson.fromJson(message, ServerActivityMessage.class);
						break;
					case "lockroomid":
						if(message.contains("locked"))
						{
							messageProcessor = gson.fromJson(message, LockRoomApprovalMessage.class);
						}
						else {
							messageProcessor = gson.fromJson(message, LockRoomMessage.class);
						}						
						break;
					case "releaseroomid":
						messageProcessor = gson.fromJson(message, ReleaseRoomidMessage.class);
						break;
					case "deleteroom":
						messageProcessor = gson.fromJson(message, ServerDeleteRoomMessage.class);
						break;									
					}
											
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return messageProcessor;
	}
}
