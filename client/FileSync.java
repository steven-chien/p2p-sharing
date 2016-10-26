import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import org.json.*;

class FileSync
{
	Tracker tracker;
	private ArrayList<Socket> peers;

	public FileSync(Tracker tracker) {
		this.tracker = tracker;
		peers = new ArrayList<Socket>();

		JSONArray peerList = tracker.getPeers().getJSONArray("peers");
		for(int i=0; i<peerList.length(); i++) {
			JSONObject peerObj = peerList.getJSONObject(i);
			try {
				newPeer(new Socket(peerObj.getString("ip"), peerObj.getInt("port")));
			} catch(Exception e) {
				System.out.println("Unable to connect to peer, exception: "+e.toString());
			}
		}
	}

	public void newPeer(Socket socket) {
		peers.add(socket);
	}

}
