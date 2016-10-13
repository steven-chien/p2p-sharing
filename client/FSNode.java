import java.io.*;
import java.util.*;
import java.lang.*;
import org.json.*;

class FSNode
{
    protected String path;
	public JSONObject toJSON(JSONArray folders, JSONArray files) {
        System.err.println("WARNING! Calling toJSON in FSNode");
        return null;
    }
    public JSONObject toJSON() {
        System.err.println("WARNING! Calling toJSON in FSNode");
        return null;
    }
}
