import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;

class Peer implements Runnable
{
	FileSync fileSync;
	Socket socket;

	public Peer(String ip, int port, FileSync fileSync) throws Exception {
		this.fileSync = fileSync;
		System.out.println("Connecting to host "+ip+":"+port);
		socket = new Socket(ip, port);
		setup();
	}

	public Peer(Socket socket) {
		this.socket = socket;
		setup();
	}

	public void setup() {
		(new Thread(this)).start();
		// (new Thread(new Listener(1337, fileSync))).start();
	}

	public void run() {
		try {
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in =	new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String data;
			String command;
			while ((command = in.readLine()) != null) {
				data = in.readLine();
				handleCommand(command, data);
			}
		} catch(Exception e){}
	}

	public void handleCommand(String command, String data) {
		System.err.println("Got command "+command);
	}
}
