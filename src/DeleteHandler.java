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

public class DeleteHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println("Delete aquired");
		InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
		BufferedReader br = new BufferedReader(isr);
		String query = br.lines().collect(Collectors.joining());
		
		try {
			JSONObject obj = new JSONObject(query);
			
			switch(obj.getString("requestType")){
			
			case "DELETEMESSAGE":
				break;
			}
			
			System.out.println(" Object created " + obj.getString("uName"));
		}catch (JSONException e) {
			System.out.println(e + " Something unexpected went wrong. ");
		}
		
		//parseQuery(query, parameters);

		// send response
		String response = "hit";
		exchange.sendResponseHeaders(200, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.toString().getBytes());
		os.close();

	}

	public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {
		
	}
	
}
