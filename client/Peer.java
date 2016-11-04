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
		} catch (Exception e){
			System.err.println("Exception: " + e.toString());
		}
	}

	public void setup() throws Exception {

		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		if (fileSync.publish != null) {
			System.err.println("Sending metadata to peer: " + fileSync.publish.getMetaData());
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
		} catch(Exception e){
			System.err.println("Exception in Peer run(): " + e.toString());
		}
	}

	public void handleCommand(String command, String data) throws Exception {
		System.err.println("Got command "+command);
		System.err.println("Got Data "+data);

		/* publisher should not be updated */
		if (command.equals("update")) {
			if(fileSync.publish==null) {
				boolean updated = fileSync.update(data);

				/* if the peer just received latest update push the update to others */
				if(updated) {
					for(Peer peer : fileSync.getPeers()) {
						peer.out.println("update");
						peer.out.println(data);
					}
				}
			}
		}

		if (command.equals("getblock")) {
			System.err.println("Peer requested a block");
			int blockNo = Integer.parseInt(in.readLine());
			System.err.println("Block No "+blockNo);
			if (P2PFile.files != null) {
				for(P2PFile file : P2PFile.files) {
					if (file.path.equals(data)) {
						out.println(bytesToHex(file.getBlock(blockNo)));
						System.err.println("Block was sent to client");
						return;
					}
				}
			}

			System.err.println("Invalid block, sending NOOP");
			out.println("NODATA");
		}
	}

    /*
     * Used for debugging; This method is stolen from Stack Overflow
     */
    final protected char[] hexArray = "0123456789ABCDEF".toCharArray();
    public String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
