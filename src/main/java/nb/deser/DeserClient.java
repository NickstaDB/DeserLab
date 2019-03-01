package nb.deser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/***********************************************************
 * DeserClient class
 * 
 * Java (de)serialization demo app - client side.
 * 
 * This class is a simple client application that uses a
 * custom network protocol based on Java serialization to
 * communicate with the server implemented by DeserServer.
 * The client submits a string to the server which
 * generates and responds with an MD5 hash of that string.
 * Hash requests and responses are wrapped in a
 * HashRequest object.
 * 
 * Written by Nicky Bloor (@NickstaDB).
 **********************************************************/
public class DeserClient {
	public static final int PROTOCOL_HELLO = 0xF000BAAA;
	public static final short PROTOCOL_VERSION = 0x0101;
	
	public void run(String serverAddress, int serverPort) throws Exception {
		BufferedReader br;
		ObjectInputStream ois;
		ObjectOutputStream oos;
		InetAddress serverAddr;
		HashRequest hr;
		Socket sock;
		
		//Get the server address
		serverAddr = InetAddress.getByName(serverAddress);
		
		//Connect to the server
		System.out.println("[+] DeserClient started, connecting to " + serverAddr.getHostAddress() + ":" + serverPort);
		sock = new Socket(serverAddr, serverPort);
		sock.setSoTimeout(250);
		
		//Get the input/output streams for the socket
		oos = new ObjectOutputStream(sock.getOutputStream());
		ois = new ObjectInputStream(sock.getInputStream());
		
		//Create a BufferedReader for console input
		br = new BufferedReader(new InputStreamReader(System.in));
		
		//Receive hello packet from the server
		System.out.println("[+] Connected, reading server hello packet...");
		if(ois.readInt() != PROTOCOL_HELLO) {
			System.out.println("[-] Server did not send a valid hello, killing connection...");
			sock.close();
			return;
		}
		
		//Send a hello to the server
		System.out.println("[+] Hello received, sending hello to server...");
		oos.writeInt(PROTOCOL_HELLO);
		oos.flush();
		
		//Read the server's protocol version
		System.out.println("[+] Hello sent, reading server protocol version...");
		if(ois.readShort() > PROTOCOL_VERSION) {
			System.out.println("[-] Server protocol version is greater than client protocol version, killing connection...");
			sock.close();
			return;
		}
		
		//Send client protocol version
		System.out.println("[+] Sending supported protocol version to the server...");
		oos.writeShort(PROTOCOL_VERSION);
		oos.flush();
		
		//Read client name string and send it to the server
		System.out.println("[+] Enter a client name to send to the server: ");
		oos.writeUTF(br.readLine());
		oos.flush();
		
		//Read a string to hash, create a HashRequest, and send it to the server
		System.out.println("[+] Enter a string to hash: ");
		hr = new HashRequest(br.readLine());
		System.out.println("[+] Generating hash of \"" + hr.getData() + "\"...");
		oos.writeObject(hr);
		oos.flush();
		
		//Read back the resulting hash
		hr = (HashRequest)ois.readObject();
		System.out.println("[+] Hash generated: " + hr.getHash());
	}
}
