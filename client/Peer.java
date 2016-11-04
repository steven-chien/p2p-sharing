import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;

class Peer implements Runnable
{
	FileSync fileSync;
	Socket socket;
	PrintWriter out;
	BufferedReader in;

	public Peer(String ip, int port, FileSync fileSync) throws Exception {
		this.fileSync = fileSync;
		System.out.println("Connecting to host "+ip+":"+port);
		socket = new Socket(ip, port);
		setup();
	}

	public Peer(Socket socket, FileSync fileSync) {
		this.socket = socket;
		this.fileSync = fileSync;
		try {
			setup();
		} catch (Exception e){}
	}

	public void setup() throws Exception {

		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		if (fileSync != null) {
			System.err.println("Sending metadata to peer");
			out.println("update");
			out.println(fileSync.publish.getMetaData());
			out.flush();
		} else {
			System.err.println("Dont have any metadata, will not sent to peer");
		}

		(new Thread(this)).start();
		// (new Thread(new Listener(1337, fileSync))).start();
	}

	public void run() {
		try {

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
