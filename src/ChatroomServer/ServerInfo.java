package ChatroomServer;
import java.net.InetAddress;

public class ServerInfo {
	private String serverid;
	private InetAddress hostAddress;
	private int clientPort;
	private int serverPort;
	
	public ServerInfo(String serverid, InetAddress hostAddress, int clientPort, int serverPort) {
		this.serverid = serverid;
		this.hostAddress = hostAddress;
		this.clientPort = clientPort;
		this.serverPort = serverPort;
	}

	public InetAddress getHostAddress() {
		return hostAddress;
	}

	public int getClientPort() {
		return clientPort;
	}

	public int getServerPort() {
		return serverPort;
	}

	public String getServerid() {
		return serverid;
	}


}
	
