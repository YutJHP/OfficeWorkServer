import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.*;


public class JDBC {
	
	  static final String DB_URL = "Insert Database URL here";
	  static final String USER = "guest";
	  static final String PASS = "guest123";
	  
	  
	  public String login(String uName, String pass) {
		  String response = "{ \"valid\": \"false\" }";
		  
		  return response;
	  }
	  
	  public String createUser(String uName, String pass, String fname, String lname, String position, String email, String id) {
		  String response = "{ \"valid\": \"false\" }";
		  
		  return response;
	  }
	  
	  public String fetch(String query) {
		  
		return query;
	  }
	
	  public void delete(String query) {
		  
	  }
	  
	  public boolean update(String query) {
		  
		  return true;
	  }

	public String sendMessage(int uID, String text, ArrayList<Integer> recipients) {
		  String response = "{ \"valid\": \"false\" }";
		  
		  return response;
	}
	
}