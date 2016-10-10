import java.io.*;
import java.util.*;
import java.net.*;
import java.util.*;

class Listener implements Runnable
{
	ServerSocket incomingSock;
	int port;
	FileSync fileSync;

	public void run() {
		try {
			incomingSock = new ServerSocket(port);
			while(true) {
				Socket connectionSocket = incomingSock.accept();
				System.err.println("New incoming conn: "+connectionSocket.getRemoteSocketAddress().toString());
				fileSync.newPeer(connectionSocket);
			}
		}
		catch(Exception ex) {
			System.err.println("Exception caught: "+ex.toString());
			System.exit(1);
		}
	}

	public Listener(int port, FileSync fileSync) {
		this.port = port;
		this.fileSync = fileSync;
	}
}
