import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.json.*;

public class PatchHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println("Patch aquired");
		InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String query = br.lines().collect(Collectors.joining());
		String response = "";
		
		try {
			JSONObject obj = new JSONObject(query);
			
			switch(obj.getString("requestType")){
			case "UPDATESTATUS":
				response = updateStatus(obj);
				break;
			case "ARCHIVEMESSAGE":
				response = archiveMessage(obj);
				break;
			}
		}catch (JSONException e) {
			System.out.println(e + " Something unexpected went wrong. ");
		}

		// send response
		exchange.sendResponseHeaders(200, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.toString().getBytes());
		os.close();


	}
	

	private String updateStatus(JSONObject obj) {
		JDBC jdbc = new JDBC();
		
		int uID = obj.getInt("UserID");
		String status = obj.getString("status");
		
		return jdbc.updateStatus(uID, status);
	}

	private String archiveMessage(JSONObject obj) {
		JDBC jdbc = new JDBC();
		
		int uID = obj.getInt("UserID");
		int mID = obj.getInt("messageID");
		
		return jdbc.archiveMessage(uID, mID);
	}
}
