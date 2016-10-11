import java.io.*;
import java.util.*;

public class Sharing
{
	public static void main(String[] args) {

		try {
			P2PFile file = new P2PFile("./test");
		}
		catch(Exception ex) {
			System.err.println("Exception: "+ex.toString());
			System.exit(1);
		}

		Tracker tracker = new Tracker("caprioli.se", 1337, "01234567890123456789012345678912", "12134567810323436789012345678914", "01234567890123456789012345678912");
		FileSync fileSync = new FileSync(tracker);
		(new Thread(new Listener(1337, fileSync))).start();

		System.out.println(tracker.getPeers().toString());
	}
}
