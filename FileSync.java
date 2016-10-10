import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import org.json.*;

class FileSync
{
	Tracker tracker;
	private ArrayList<Peer> peers;

	public FileSync(Tracker tracker) {
		this.tracker = tracker;
		peers = new ArrayList<Peer>();

		JSONArray peerList = tracker.getPeers().getJSONArray("peers");
		for(int i=0; i<peerList.length(); i++) {
			JSONObject peerObj = peerList.getJSONObject(i);
			System.out.println(peerObj.getString("ip")+";"+peerObj.getInt("port"));
		}
	}

	public void newPeer(Socket socket) {
		peers.add(new Peer(socket));
	}
}
