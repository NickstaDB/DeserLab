package nb.deser;

import java.io.Serializable;

/***********************************************************
 * Hash request object - the client constructs this with a
 * string to hash and then sends it to the server which
 * generates the hash and returns the result.
 * 
 * Written by Nicky Bloor (@NickstaDB).
 **********************************************************/
public class HashRequest implements Serializable {
	private String dataToHash;
	private String theHash;
	
	public HashRequest(String hashMe) {
		dataToHash = hashMe;
		theHash = "";
	}
	
	public void setData(String data) {
		dataToHash = data;
	}
	
	public void setHash(String hash) {
		theHash = hash;
	}
	
	public String getData() {
		return dataToHash;
	}
	
	public String getHash() {
		return theHash;
	}
}
