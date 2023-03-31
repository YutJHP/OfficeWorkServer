import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.json.*;

public class PostHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println("Post aquired");
		InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String query = br.lines().collect(Collectors.joining());

		String response = "";

		try {
			JSONObject obj = new JSONObject(query);

			switch (obj.getString("requestType")) {
			case "LOGIN":
				response = login(obj);
				break;
			case "NEWUSER":
				response = createUser(obj);
				break;
			case "CREATEGROUP":
				response = createGroup(obj);
				break;
			case "SENDMESSAGE":
				response = sendMessage(obj);
				break;
			case "UPDATE":
				response = update(obj);
				break;
			}
		} catch (JSONException e) {
			System.out.println(" Something unexpected went wrong. " + e);
		}

		// send response
		exchange.sendResponseHeaders(200, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.toString().getBytes());
		os.close();
	}

	private String sendMessage(JSONObject obj) {
		JDBC jdbc = new JDBC();
		
		int uID = obj.getInt("userID");
		String text = obj.getString("text");
		JSONArray temp = obj.getJSONArray("recipients");
		ArrayList<Integer> recipients = new ArrayList<Integer>();
		for(int i = 0; i < temp.length(); i++) {
			recipients.add(temp.getInt(i));			
		}
		return jdbc.sendMessage(uID, text, recipients);
	}

	private String createGroup(JSONObject obj) {
		JDBC jdbc = new JDBC();
		
		int uID = obj.getInt("userID");
		String name = obj.getString("name");
		JSONArray temp = obj.getJSONArray("members");
		ArrayList<Integer> members = new ArrayList<Integer>();
		for(int i = 0; i < temp.length(); i++) {
			members.add(temp.getInt(i));			
		}
		return jdbc.createGroup(uID, name, members);
	}

	private String createUser(JSONObject obj) {
		JDBC jdbc = new JDBC();
		
		String uName = obj.getString("username");
		String pass = obj.getString("password");
		String fname = obj.getJSONObject("userData").getString("firstName");
		String lname = obj.getJSONObject("userData").getString("lastName");
		String position = obj.getJSONObject("userData").getString("position");
		String email = obj.getJSONObject("userData").getString("email");
		int id = obj.getJSONObject("userData").getInt("id");
		
		return jdbc.createUser(uName, pass, fname, lname, position, email, id);
	}

	private String login(JSONObject obj) {
		JDBC jdbc = new JDBC();
		
		String uName = obj.getString("username");
		String pass = obj.getString("password");
		
		return jdbc.login(uName, pass);
	}
	
	private String update(JSONObject obj) {
		JDBC jdbc = new JDBC();
		
		int uID = obj.getInt("userID");
		
		return jdbc.update(uID);
	}
}
