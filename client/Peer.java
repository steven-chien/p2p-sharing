import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;

class Peer
{
	FileSync fileSync;
	Socket socket;

	public Peer(String ip, int port, FileSync fileSync) throws Exception {
		this.fileSync = fileSync;
		System.out.println("Connecting to host "+ip+":"+port);
		socket = new Socket(ip, port);
	}

	public Peer(Socket socket) {
		this.socket = socket;
	}
}
