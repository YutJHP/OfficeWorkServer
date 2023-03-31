import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.*;

public class JDBC {

	static final String DB_URL = "jdbc:mysql://localhost:3306/chatapp";
	static final String USER = "root";
	static final String PASS = "AAAAAA";

	public String login(String uName, String pass) {
				
		String response = "{ \"valid\": \"false\" }";
		ArrayList<Integer> IDs = new ArrayList<Integer>();
		ArrayList<String> uNames = new ArrayList<String>();
		ArrayList<String> passwords = new ArrayList<String>();

		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select User_ID, Username, Password from chatapp.user");

			while (rs.next()) {
				IDs.add(rs.getInt(1));
				uNames.add(rs.getString(2));
				passwords.add(rs.getString(3));
			}

			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (uNames.isEmpty()) {
			return response;
		}

		for (int i = 0; i < uNames.size(); i++) {
			if (uName.equals(uNames.get(i)) && pass.equals(passwords.get(i))) {
				return "{\"valid\":\"true\", \"userID\": " + IDs.get(i) + "}";
			}
		}

		return response;
	}

	public String createUser(String uName, String pass, String fname, String lname, String position, String email) {
		String response = "{ \"valid\": \"false\" }";

		ArrayList<Integer> IDs = new ArrayList<Integer>();
		ArrayList<String> uNames = new ArrayList<String>();

		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select User_ID, Username from chatapp.user");

			while (rs.next()) {
				IDs.add(rs.getInt(1));
				uNames.add(rs.getString(2));
				System.out.println(rs.getInt(1) +  " | " + rs.getString(2));
			}

			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("THere are this many username: " + uNames.size());
		for (int i = 0; i < uNames.size(); i++) {
			if (uName.equals(uNames.get(i))) {
				System.out.println("triggered early return");
				return response;
			}
		}
		
		System.out.println("reached part a");

		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here
			Statement stmt = con.createStatement();
			String sql = "INSERT INTO `User` (Username, Password, Fname, Lname, Email, Active_status, Position) VALUES ('" + uName + "', '" + pass + "', '" + fname + "', '"
					+ lname + "', '" + email + "', 'offline', '" + position + "' );";
			stmt.executeUpdate(sql);
			con.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

				
		return "{\"valid\":\"true\"}";
	}

	public String fetch(String query) {

		return query;
	}

	public String update(int uID) {
		String response = "{ \"valid\": \"false\" }";
		String usersJSON = "\"users\": [";
		String messagesJSON = "\"messages\": [";
		String groupsJSON = "\"groups\": [";
		
		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select Fname, Lname, Position, Email, Active_Status, User_ID from chatapp.user ORDER BY User_ID");
			
			ArrayList<String> firstName = new ArrayList<String>();
			ArrayList<String> lastName = new ArrayList<String>();
			ArrayList<String> position = new ArrayList<String>();
			ArrayList<String> email = new ArrayList<String>();
			ArrayList<String> status = new ArrayList<String>();
			ArrayList<Integer> userID = new ArrayList<Integer>();

			while (rs.next()) {
				firstName.add(rs.getString(1));
				lastName.add(rs.getString(2));
				position.add(rs.getString(3));
				email.add(rs.getString(4));
				status.add(rs.getString(5));
				userID.add(rs.getInt(6));
			}
			
			for(int i = 0; i < firstName.size(); i++) {
				usersJSON += "{\r\n"
						+ "       \"firstName\": \""+ firstName.get(i) +"\",\r\n"
						+ "       \"lastName\": \""+ lastName.get(i) +"\",\r\n"
						+ "       \"position\": \""+ position.get(i) +"\",\r\n"
						+ "       \"email\": \""+ email.get(i) +"\",\r\n"
						+ "       \"status\": \""+ status.get(i) +"\",\r\n"
						+ "       \"id\": "+ userID.get(i) +"\r\n"
						+ "}";
				if(i != firstName.size()-1) {
					usersJSON += ", \n";
				}else {
					usersJSON += "\n";
				}
			}
			
			usersJSON += "],\n";
			
			
			rs = stmt.executeQuery("SELECT messageowner.User_ID, messageowner.sender, messageowner.Receiver, message.Message_ID, message.MessageText, message.Date_Created, messageowner.Archived_Status\r\n"
					+ "FROM messageowner\r\n"
					+ "INNER JOIN message ON messageowner.Message_ID=message.Message_ID\r\n;");
			
			class MessageRow {
				int userID;
				boolean sender;
				boolean receiver;
				int messageID;
				String text;
				String dateCreated;
				String archivedStatus;
				
				public MessageRow(int userID, boolean sender, boolean receiver, int messageID, String text,
						String dateCreated, String archivedStatus) {
					super();
					this.userID = userID;
					this.sender = sender;
					this.receiver = receiver;
					this.messageID = messageID;
					this.text = text;
					this.dateCreated = dateCreated;
					this.archivedStatus = archivedStatus;
				}				
			}
			
			ArrayList<MessageRow> MessageRows = new ArrayList<>();
			while (rs.next()) {
				MessageRows.add(new MessageRow(
						rs.getInt(1),
						rs.getBoolean(2),
						rs.getBoolean(3),
						rs.getInt(4),
						rs.getString(5), 
						rs.getString(6),
						rs.getString(7)));
			}
			
			class Message {				
				int sender;
				ArrayList<Integer> recipients = new ArrayList<>();
				String archivedStatus;
				String text;
				String timeSent;
				boolean hasActiveUser = false;
				
				public JSONObject getJson() {
					JSONObject json = new JSONObject();
					json.put("text", text);
					json.put("timeSent", timeSent);
					json.put("archived", archivedStatus.equals("Archived"));
					json.put("sender", sender);
					JSONArray recipientsJson = new JSONArray();
					for (int recipient: recipients) {
						recipientsJson.put(recipient);
					}
					json.put("recipients", recipientsJson);
					return json;
				}
			}			
			HashMap<Integer, Message> messages = new HashMap<>();
			
			for (MessageRow row: MessageRows) {
				Message message;
				if (!messages.containsKey(row.messageID)) {
					message = new Message();
					messages.put(row.messageID, message);
					message.text = row.text;
					message.timeSent = row.dateCreated;
				} else {
					message = messages.get(row.messageID);
				}
				if (row.sender) {
					message.sender = row.userID;
				}
				if (row.receiver) {
					message.recipients.add(row.userID);
				}
				if (row.userID == uID) {
					message.archivedStatus = row.archivedStatus;
					message.hasActiveUser = true;
					
				}
			}
			
			JSONArray messagesJson = new JSONArray();
			for (Map.Entry<Integer, Message> entry: messages.entrySet()) {
				Message message = entry.getValue();
				if (message.hasActiveUser && !message.archivedStatus.equals("Deleted")) {
					JSONObject messageJson = message.getJson();
					messageJson.put("id", entry.getKey());
					messagesJson.put(messageJson);
				}
			}
			
			messagesJSON = " \"messages\":" + messagesJson.toString() + ", ";	
			
			rs = stmt.executeQuery("select Group_ID, User_ID from chatapp.groupmembers;");
			ArrayList<Integer> GID = new ArrayList<Integer>();
			HashMap<Integer, ArrayList<Integer>> groupMembers = new HashMap<Integer, ArrayList<Integer>>();

			while(rs.next()) {
				if(GID.contains(rs.getInt(1))) {
					ArrayList<Integer> temp = groupMembers.get(rs.getInt(1));
					temp.add(rs.getInt(2));
					groupMembers.put(rs.getInt(1), temp);
				}else {
					ArrayList<Integer> temp = new ArrayList<Integer>();
					temp.add(rs.getInt(2));
					groupMembers.put(rs.getInt(1), temp);
					GID.add(rs.getInt(1));
				}
			}
			rs = stmt.executeQuery("select Group_ID, GroupName from chatapp.group;");
			HashMap<Integer, String> gNames = new HashMap<Integer, String>();
			
			while (rs.next()) {
				gNames.put(rs.getInt(1), rs.getString(2));
			}
			

			
			for(int i = 0; i < GID.size(); i++) {
				groupsJSON += "{\r\n"
						+ "       \"id\": "+ GID.get(i) +",\r\n"
						+ "       \"name\": \""+ gNames.get(GID.get(i)) +"\",\r\n"
						+ "       \"members\":  ";
				
				groupsJSON += groupMembers.get(GID.get(i)) + " ";
				
				groupsJSON += "\n\r\n" + "}";
				if(i != GID.size()-1) {
					groupsJSON += ", \n";
				}else {
					groupsJSON += "\n";
				}
			}
			
			groupsJSON += "]\n";			

			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		

		return "{ " + usersJSON + messagesJSON + groupsJSON + "}";
	}

	public String sendMessage(int uID, String text, ArrayList<Integer> recipients) {
		
		int newMessageID = 0;
		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here
			Statement stmt = con.createStatement();
			
			java.time.LocalDateTime date = java.time.LocalDateTime.now();
			String dateTime = date.toString();
			dateTime.replace("T", " ");

			String sql = "INSERT INTO `Message` (MessageText, Date_Created) VALUES ('" + text + "', '" + dateTime  + "' );";
			stmt.executeUpdate(sql);
			
			ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
			rs.next();
			newMessageID = rs.getInt(1);

			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

				
		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here

			Statement stmt = con.createStatement();

			String sql = "";
			
			if(recipients.contains(uID)) {
				sql = "INSERT INTO `messageOwner` (Archived_Status, Sender, Receiver, User_ID, Message_ID) VALUES ('Inboxed', true, true, " + uID + ", " + newMessageID + ");";
			}else {
				sql = "INSERT INTO `messageOwner` (Archived_Status, Sender, Receiver, User_ID, Message_ID) VALUES ('Inboxed', true, false, " + uID + ", " + newMessageID + ");";
			}
			stmt.executeUpdate(sql);
			
			for (int recipient: recipients) {
				if (recipient != uID) {
					sql = "INSERT INTO `messageOwner` (Archived_Status, Sender, Receiver, User_ID, Message_ID) VALUES ('Inboxed', false, true, " + recipient + ", " + newMessageID + ");";
					stmt.executeUpdate(sql);
				}				
			}
			con.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "{\"valid\":\"true\"}";
	}

	public String createGroup(int uID, String name, ArrayList<Integer> member) {
		String response = "{ \"valid\": \"false\" }";

		ArrayList<String> gNames = new ArrayList<String>();
		ArrayList<Integer> gID = new ArrayList<Integer>();
		
		int newGroupID = 0;

		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select GroupName from chatapp.group");

			while (rs.next()) {
				if (name.equals(rs.getString(1))) return response;
			}			

			String sql = "INSERT INTO `group` (GroupName) VALUES ('" + name + "');";
			stmt.executeUpdate(sql);
			
			rs = stmt.executeQuery("SELECT LAST_INSERT_ID()");
			rs.next();
			newGroupID = rs.getInt(1);			

			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		

		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here

			Statement stmt = con.createStatement();

			for (int n = 0; n < member.size(); n++) {				

				String sql = "INSERT INTO `groupMembers` (User_ID, Group_ID) VALUES (" + member.get(n) + ", "
						+ newGroupID + ");";
				stmt.executeUpdate(sql);
			}
			con.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "{\"valid\":\"true\"}";
	}

	public String updateStatus(int uID, String status) {
		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here
			Statement stmt = con.createStatement();
			String sql = "UPDATE chatapp.user SET Active_Status = '" + status + "' WHERE User_ID = " + uID + ";";
			stmt.executeUpdate(sql);
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return "{ \"valid\": \"false\" }";
	}

	public String archiveMessage(int uID, int mID) {
		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here
			Statement stmt = con.createStatement();
			String sql = "UPDATE chatapp.messageowner SET Archived_Status = 'Archived' WHERE User_ID = " + uID
					+ " AND Message_ID = " + mID + ";";
			stmt.executeUpdate(sql);
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "{ \"valid\": \"false\" }";
	}

	public String delete(int uID, int mID) {
		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here
			Statement stmt = con.createStatement();
			String sql = "UPDATE chatapp.messageowner SET Archived_Status = 'Deleted' WHERE User_ID = " + uID
					+ " AND Message_ID = " + mID + ";";
			stmt.executeUpdate(sql);
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "{ \"valid\": \"false\" }";
	}
}