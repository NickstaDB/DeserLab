package nb.deser;

/***********************************************************
 * DeserLab class
 * 
 * Main class of a Java (de)serialization demo app.
 * 
 * This class checks command line arguments and launches
 * either the deserialization client or server.
 * 
 * Written by Nicky Bloor (@NickstaDB).
 **********************************************************/
public class DeserLab {
	public static void main(String[] args) throws Exception {
		DeserServer ds;
		DeserClient dc;
		int port;
		
		//Check command line and start the client or server accordingly
		if(args.length != 3) {
			//Invalid args
			printUsage(null);
		} else {
			//Parse the port number (third parameter)
			try {
				port = Integer.parseInt(args[2]);
				if(port < 0 || port > 65535 || (args[0].toLowerCase().equals("-client") && port == 0)) {
					throw new IllegalArgumentException("Invalid port number specified.");
				}
			} catch(Exception nfe) {
				printUsage("Error: a valid integer between 0 and 65535 must be supplied as the <port> parameter.");
				return;
			}
			
			//Check the execution mode (first parameter)
			if(args[0].toLowerCase().equals("-server")) {
				//Start server mode
				ds = new DeserServer();
				ds.run(args[1], port);
			} else if(args[0].toLowerCase().equals("-client")) {
				//Start client mode
				dc = new DeserClient();
				dc.run(args[1], port);
			} else {
				printUsage("Error: first parameter must be -client or -server.");
			}
		}
	}
	
	private static void printUsage(String err) {
		System.out.println("DeserLab");
		System.out.println("Simple TCP client and server with a custom network protocol based on Java's");
		System.out.println("deserialization format.");
		System.out.println("");
		if(err != null && err.equals("") == false) {
			System.out.println(err);
			System.out.println("");
		}
		System.out.println("Usage:");
		System.out.println("    DeserLab.jar -server <listen-address> <port>");
		System.out.println("    DeserLab.jar -client <server-address> <port>");
		System.out.println("");
	}
}
