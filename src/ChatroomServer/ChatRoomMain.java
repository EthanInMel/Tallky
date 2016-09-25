package ChatroomServer;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class ChatRoomMain {
	public static void main(String[] args) {
		System.out.println("Server started");
		CmdLineArgs argsBean = new CmdLineArgs();
		CmdLineParser parser = new CmdLineParser(argsBean);
		try {			
			parser.parseArgument(args);		
			Server server = new Server(argsBean.getServerid(), argsBean.getConfig());
			server.run();
		} catch (CmdLineException e) {
			
			System.err.println(e.getMessage());
			
			parser.printUsage(System.err);
		}		
		

	}

}
