import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;

import com.sun.net.httpserver.HttpServer;

public class Server {
    public static void main(String[] args) throws IOException {
    	
    	int port = 9000;
    	HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    	System.out.println("server started at " + port);
    	server.createContext("/", new RootHandler());
    	server.createContext("/echoGet", new GetHandler());
    	server.createContext("/echoPost", new PostHandler());
       	server.createContext("/echoDelete", new DeleteHandler());
    	server.createContext("/echoPatch", new PatchHandler());
    	server.setExecutor(null);
    	server.start();
    	
    }
}