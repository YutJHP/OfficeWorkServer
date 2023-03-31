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

import org.json.*;

public class JDBC {

	static final String DB_URL = "jdbc:mysql://localhost:3306/chatapp";
	static final String USER = "root";
	static final String PASS = "1234";

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

	public String createUser(String uName, String pass, String fname, String lname, String position, String email,
			int id) {
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
			}

			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < uNames.size(); i++) {
			if (uName.equals(uNames.get(i)) || id == IDs.get(i)) {
				return response;
			}
		}

		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here
			Statement stmt = con.createStatement();
			String sql = "INSERT INTO `User` VALUES (" + id + ", '" + uName + "', '" + pass + "', '" + fname + "', '"
					+ lname + "', '" + email + "', 'Online', '" + position + "' );";
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
			
			
			rs = stmt.executeQuery("SELECT messageowner.User_ID, messageowner.sender, messageowner.Receiver, message.Message_ID, message.MessageText, message.Date_Created\r\n"
					+ "FROM messageowner\r\n"
					+ "INNER JOIN message ON messageowner.Message_ID=message.Message_ID\r\n"
					+ "Where messageowner.sender = true;");
			ArrayList<Integer> sender = new ArrayList<Integer>();
			ArrayList<Integer> messageID = new ArrayList<Integer>();
			HashMap<Integer, ArrayList<String>> messageContents = new HashMap<Integer, ArrayList<String>>();
			
			while(rs.next()) {
				sender.add(rs.getInt(1));
				messageID.add(rs.getInt(4));
				messageContents.put(rs.getInt(4), new ArrayList<String>());
				ArrayList<String> temp = messageContents.get(rs.getInt(4));
				temp.add(rs.getString(5));
				temp.add(rs.getString(6));
				messageContents.put(rs.getInt(4), temp);
			}
			
			
			rs = stmt.executeQuery("SELECT messageowner.User_ID, message.Message_ID\r\n"
					+ "FROM messageowner\r\n"
					+ "INNER JOIN message ON messageowner.Message_ID=message.Message_ID\r\n"
					+ "Where messageowner.sender = false;");
			
			HashMap<Integer, ArrayList<Integer>> recipients = new HashMap<Integer, ArrayList<Integer>>();
			ArrayList<Integer> receivers = new ArrayList<Integer>();
			
			while (rs.next()) {
				receivers.add(rs.getInt(1));
				if(recipients.get(rs.getInt(1))==null) {
					recipients.put(rs.getInt(1), new ArrayList<Integer>());	
					ArrayList<Integer> temp = recipients.get(rs.getInt(1));
					temp.add(rs.getInt(2));
					recipients.put(rs.getInt(1), temp);	
				}else {
					ArrayList<Integer> temp = recipients.get(rs.getInt(1));
					temp.add(rs.getInt(2));
					recipients.put(rs.getInt(1), temp);					
				}
			}
			
			
			for(int i = 0; i < sender.size(); i++) {
				messagesJSON += "{\r\n"
						+ "       \"sender\": "+ sender.get(i) +",\r\n"
						+ "       \"recipients\": [ ";

				
				for(int m = 0; m < receivers.size(); m++) {
					// TODO
					messagesJSON += receivers.get(m) + " ";
					if(m != receivers.size()-1) {
						messagesJSON += ", ";
					}
				}
				
				messagesJSON += "\n],\r\n"
						+ "       \"id\": \""+ messageID.get(i) +"\",\r\n"
						+ "       \"text\": \""+ messageContents.get(messageID.get(i)).get(0) +"\",\r\n"
						+ "       \"timeSent\": \""+ messageContents.get(messageID.get(i)).get(1) +"\"\r\n"
						+ "}";
				if(i != messageID.size()-1) {
					messagesJSON += ", \n";
				}else {
					messagesJSON += "\n";
				}
			}
			
			messagesJSON += "],\n";
			
			
			
			rs = stmt.executeQuery("select Group_ID, User_ID from chatapp.groupmembers;");
			ArrayList<Integer> GID = new ArrayList<Integer>();
			HashMap<Integer, ArrayList<Integer>> groupMembers = new HashMap<Integer, ArrayList<Integer>>();

			while(rs.next()) {
				if(GID.contains(rs.getInt(1))) {
					ArrayList<Integer> temp = groupMembers.get(rs.getInt(1));
					temp.add(rs.getInt(2));
					groupMembers.put(rs.getInt(1), temp);
				}else {
					groupMembers.put(rs.getInt(1), new ArrayList<Integer>());
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
						+ "       \"members\": [ ";
				
				groupsJSON += groupMembers.get(GID.get(i)) + " ";
				System.out.println(groupMembers);

				
				groupsJSON += "\n]\r\n" + "}";
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
		ArrayList<Integer> mID = new ArrayList<Integer>();
		int newMessageID = 1;

		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select Message_ID from chatapp.Message ORDER BY Message_ID");

			while (rs.next()) {
				mID.add(rs.getInt(1));
			}

			for (int i = 0; i < mID.size(); i++) {
				if (mID.get(i) == newMessageID) {
					newMessageID++;
				}
			}
			
			java.time.LocalDateTime date = java.time.LocalDateTime.now();
			String dateTime = date.toString();
			dateTime.replace("T", " ");

			String sql = "INSERT INTO `Message` VALUES (" + newMessageID + ", '" + text + "', '" + dateTime  + "' );";
			stmt.executeUpdate(sql);

			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		int newMessageOwnerID = 1;
		ArrayList<Integer> MOID = new ArrayList<Integer>();
		
		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select User_Message_ID from chatapp.messageOwner ORDER BY User_Message_ID");

			while (rs.next()) {
				MOID.add(rs.getInt(1));
			}
			
			String sql = "";
			
			if(recipients.contains(uID)) {
				sql = "INSERT INTO `messageOwner` VALUES (" + newMessageOwnerID + ", 'Inboxed', true, true, " + uID + ", " + newMessageID + ");";
			}else {
				sql = "INSERT INTO `messageOwner` VALUES (" + newMessageOwnerID + ", 'Inboxed', true, false, " + uID + ", " + newMessageID + ");";
			}
			stmt.executeUpdate(sql);
			
			for (int n = 0; n < recipients.size(); n++) {

				for (int i = 0; i < MOID.size(); i++) {
					if (MOID.get(i) == newMessageOwnerID) {
						newMessageOwnerID++;
					}
				}
					if(recipients.get(n)==uID) {
						
					}else {
						sql = "INSERT INTO `messageOwner` VALUES (" + newMessageOwnerID + ", 'Inboxed', false, true, " + recipients.get(n) + ", "
								+ newMessageID + ");";
					}
				
				MOID.add(newMessageOwnerID);

				stmt.executeUpdate(sql);
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
		int newGroupID = 1;

		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select Group_ID, GroupName from chatapp.group ORDER BY Group_ID");

			while (rs.next()) {
				gID.add(rs.getInt(1));
				gNames.add(rs.getString(2));
			}

			for (int i = 0; i < gNames.size(); i++) {
				if (name.equals(gNames.get(i))) {
					return response;
				}
			}

			for (int i = 0; i < gID.size(); i++) {
				if (gID.get(i) == newGroupID) {
					newGroupID++;
				}
			}

			String sql = "INSERT INTO `group` VALUES (" + newGroupID + ", '" + name + "' );";
			stmt.executeUpdate(sql);

			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		int newGroupMemebersID = 1;
		ArrayList<Integer> GMID = new ArrayList<Integer>();

		try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
			// use con here

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select groupMember_ID from chatapp.groupMembers ORDER BY groupMember_ID");

			while (rs.next()) {
				GMID.add(rs.getInt(1));
			}

			for (int n = 0; n < member.size(); n++) {

				for (int i = 0; i < GMID.size(); i++) {
					if (GMID.get(i) == newGroupMemebersID) {
						newGroupMemebersID++;
					}
				}

				String sql = "INSERT INTO `groupMembers` VALUES (" + newGroupMemebersID + ", " + member.get(n) + ", "
						+ newGroupID + ");";
				GMID.add(newGroupMemebersID);

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