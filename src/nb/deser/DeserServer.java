package nb.deser;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;

/***********************************************************
 * DeserServer class
 * 
 * Java (de)serialization demo app - server side.
 * 
 * This class is a simple single-threaded server
 * application that implements a custom network protocol
 * based on the Java serialization format. The server
 * simply allows connecting clients to generate MD5 hashes
 * of given strings. The hashing request and resulting
 * hash are wrapped up in a 'HashRequest' object.
 * 
 * Written by Nicky Bloor (@NickstaDB).
 **********************************************************/
public class DeserServer {
	public static final int PROTOCOL_HELLO = 0xF000BAAA;
	public static final short PROTOCOL_VERSION = 0x0101;
	
	public void run(String listenAddress, int listenPort) throws Exception {
		ObjectInputStream ois;
		ObjectOutputStream oos = null;
		InetAddress listenAddr;
		ServerSocket serverSock;
		Socket clientSock = null;
		String clientName;
		HashRequest request;
		
		//Get the listen address
		listenAddr = InetAddress.getByName(listenAddress);
		
		//Create the server socket
		serverSock = new ServerSocket(listenPort, 0, listenAddr);
		serverSock.setSoTimeout(250);
		System.out.println("[+] DeserServer started, listening on " + serverSock.getInetAddress().getHostAddress() + ":" + serverSock.getLocalPort());
		
		//Main server loop
		while(true) {
			try {
				//Attempt to accept an incoming connection
				clientSock = serverSock.accept();
				System.out.println("[+] Connection accepted from " + clientSock.getInetAddress().getHostAddress() + ":" + clientSock.getPort());
				
				//Get the input/output streams for the socket
				oos = new ObjectOutputStream(clientSock.getOutputStream());
				ois = new ObjectInputStream(clientSock.getInputStream());
				
				//Send a hello to the client
				System.out.println("[+] Sending hello...");
				oos.writeInt(PROTOCOL_HELLO);
				oos.flush();
				System.out.println("[+] Hello sent, waiting for hello from client...");
				
				//Read a hello back
				if(ois.readInt() != PROTOCOL_HELLO) {
					System.out.println("[-] Client did not send a hello back, killing connection...");
					clientSock.close();
					continue;
				}
				System.out.println("[+] Hello received from client...");
				
				//Send the protocol version to the client
				System.out.println("[+] Sending protocol version...");
				oos.writeShort(PROTOCOL_VERSION);
				oos.flush();
				System.out.println("[+] Version sent, waiting for version from client...");
				
				//Read the client version
				if(ois.readShort() > PROTOCOL_VERSION) {
					System.out.println("[-] Client protocol version is greater than server protocol version, killing connection...");
					clientSock.close();
					continue;
				}
				
				//Read the client name
				System.out.println("[+] Client version is compatible, reading client name...");
				clientName = ois.readUTF();
				System.out.println("[+] Client name received: " + clientName);
				
				//Read a HashRequest object
				request = (HashRequest)ois.readObject();
				System.out.println("[+] Hash request received, hashing: " + request.getData());
				
				//Generate a hash
				request.setHash(generateHash(request.getData()));
				System.out.println("[+] Hash generated: " + request.getHash());
				
				//Return the hash and end the session
				oos.writeObject(request);
				oos.flush();
				System.out.println("[+] Done, terminating connection.");
				clientSock.close();
			} catch(Exception e) {
				//If a connection is open, send the exception object back
				try {
					if(clientSock != null && clientSock.isConnected() == true) {
						oos.writeObject(e);
					}
				} catch(Exception ee) {
					//Swallow exceptions up...
				}
				
				//Swallow exceptions up...
			}
		}
	}
	
	private String generateHash(String data) throws Exception {
		MessageDigest md5;
		
		//Generate and return the MD5 hash of the data
		md5 = MessageDigest.getInstance("MD5");
		md5.update(data.getBytes());
		return String.format("%032x", new BigInteger(1, md5.digest()));
	}
}
