import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;

class Peer
{
	FileSync fileSync;
	Socket socket;
	
	public Peer(String ip, int port, FileSync fileSync) {
		this.fileSync = fileSync;
	}

	public Peer(Socket socket) {
		this.socket = socket;
	}
}
