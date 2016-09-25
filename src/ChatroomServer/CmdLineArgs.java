package ChatroomServer;
//Remember to add the args4j jar to your project's build path 
import org.kohsuke.args4j.Option;

//This class is where the arguments read from the command line will be stored
//Declare one field for each argument and use the @Option annotation to link the field
//to the argument name, args4J will parse the arguments and based on the name,  
//it will automatically update the field with the parsed argument value
public class CmdLineArgs {

	@Option(required = true, name = "-n", usage = "Hostname")
	private String serverid;
	
	@Option(required = true, name = "-l", usage = "Hostname")
	private String servers_conf;
	

	public String getServerid() {
		return serverid;
	}

	public String getConfig() {
		return servers_conf;
	}
	
}
