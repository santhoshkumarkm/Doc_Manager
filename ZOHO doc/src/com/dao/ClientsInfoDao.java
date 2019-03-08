package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.json.simple.JSONArray;

public class ClientsInfoDao {
	static Connection con;

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/online_file_storage", "root", null);
		} catch (ClassNotFoundException | SQLException e1) {
			System.out.println("Catch occured");
			e1.printStackTrace();
		}
	}

	static public void insertFile(String location) {
		PreparedStatement stmt = null;
		int firstIndex = location.indexOf('/');
		int lastIndex = location.lastIndexOf('/');
		String ownerName = location.substring(0, firstIndex);
		String fileLocation = (firstIndex == lastIndex) ? "" : location.substring(firstIndex + 1, lastIndex);
		String fileName = location.substring(lastIndex + 1);
		try {
			stmt = con.prepareStatement("insert ignore into files_info values(null,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, ownerName);
			stmt.setString(2, fileLocation);
			stmt.setString(3, fileName);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
		}
	}

	public static void deleteFile(String location) {
		Statement stmt = null, stmt2 = null;
		int firstIndex = location.indexOf('/');
		int lastIndex = location.lastIndexOf('/');
		String ownerName = location.substring(0, firstIndex);
		String fileLocation = (firstIndex == lastIndex) ? "" : location.substring(firstIndex + 1, lastIndex);
		String fileName = location.substring(lastIndex + 1);
		try {
			stmt = con.createStatement();
			stmt.executeUpdate("delete from files_info where ownername = '" + ownerName + "' and filelocation = '"
					+ fileLocation + "' and filename = '" + fileName + "'");
			stmt2 = con.createStatement();
			stmt2.executeUpdate("delete from files_info where ownername = '" + ownerName + "' and filelocation like '%"
					+ fileName + "%'");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
			try {
				if (stmt2 != null)
					stmt2.close();
			} catch (Exception e) {
			}
		}
	}

	public static LinkedHashSet<String> getSharedUserNamesForAnUser(String userName) {
		Statement stmt = null;
		ResultSet rs = null;
		LinkedHashSet<String> set = new LinkedHashSet<String>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(
					"select f.ownername from files_info f inner join shared_users_info u on f.id = u.file_id inner join clients_info c on u.user_id = c.id where c.name ='"
							+ userName + "'");
			while (rs.next()) {
				String owner = rs.getString(1);
				set.add(owner);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
		}
		return set;
	}

	public static LinkedHashMap<String, String> getSharedFilesForAnUser(String sharedUser, String user) {
		Statement stmt = null;
		ResultSet rs = null;
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(
					"select f.ownername, f.filelocation, f.filename, u.privilege from files_info f inner join shared_users_info u on f.id = u.file_id inner join clients_info c on u.user_id = c.id where c.name ='"
							+ user + "' and f.ownername = '" + sharedUser + "'");
			while (rs.next()) {
				String result = null;
				if (rs.getString(2).equals("")) {
					result = rs.getString(1) + "/" + rs.getString(3);
				} else {
					result = rs.getString(1) + "/" + rs.getString(2) + "/" + rs.getString(3);
				}
				map.put(result, rs.getString(4));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
		}
		return map;
	}

	public static String checkAccess(String location, String rootUser, String sharedUser) {
		LinkedHashMap<String, String> fileListMap = getSharedFilesForAnUser(sharedUser, rootUser);
		for (Map.Entry<String, String> entry : fileListMap.entrySet()) {
			if (location.contains(entry.getKey())) {
				return entry.getValue();
			}
		}
		return "denied";
	}

	public static JSONArray allUserList(String rootUser, String location) {
		Statement stmt = null;
		ResultSet rs = null;
		JSONArray userListArray = new JSONArray();
		long fileId = 0;
		try {
			fileId = getFileId(location);
			stmt = con.createStatement();
			rs = stmt.executeQuery(
					"select name from clients_info where id NOT IN (select user_id from shared_users_info where file_id = '"
							+ fileId + "')");
			while (rs.next()) {
				if (rs.getString(1).equals(rootUser)) {
					continue;
				}
				userListArray.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
		}
		return userListArray;
	}

	public static Map<String, String> getSharedFilesForALocation(String location, String user) {
		int firstIndex = location.indexOf('/');
		int lastIndex = location.lastIndexOf('/');
		String ownerName = location.substring(0, firstIndex);
		String fileLocation = location.substring(firstIndex + 1);
		String fileName = location.substring(lastIndex + 1);
		Statement stmt = null, stmt2 = null;
		ResultSet rs = null, rs2 = null;
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			stmt2 = con.createStatement();
			rs2 = stmt2.executeQuery(
					"select privilege from shared_users_info s, files_info f where f.filelocation = substring('"
							+ fileLocation + "',1,char_length(filelocation)) and f.filename = '" + fileName
							+ "' and f.id = s.file_id;");
			String privilege = "";
			while (rs2.next()) {
				privilege = rs2.getString(1);
			}
			stmt = con.createStatement();
			rs = stmt.executeQuery("select filename from files_info where ownername = '" + ownerName
					+ "' and filelocation = '" + fileLocation + "'");
			while (rs.next()) {
				String file = ownerName + "/" + fileLocation + "/" + rs.getString(1);
				map.put(file, privilege);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt2 != null)
					stmt2.close();
			} catch (Exception e) {
			}
			try {
				if (rs2 != null)
					rs2.close();
			} catch (Exception e) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
		}
		return map;
	}

	public static Map<String, String> getRootUserFiles(String user) {
		Statement stmt = null;
		ResultSet rs = null;
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			stmt = con.createStatement();
			if (user.indexOf('/') == -1) {
				rs = stmt.executeQuery("select ownername, filelocation, filename from files_info where ownerName = '"
						+ user + "' and filelocation = ''");
			} else {
				int firstIndex = user.indexOf('/');
				String ownerName = user.substring(0, firstIndex);
				String fileLocation = user.substring(firstIndex + 1);
				rs = stmt.executeQuery("select ownername, filelocation, filename from files_info where ownerName = '"
						+ ownerName + "' and fileLocation = '" + fileLocation + "'");
			}
			while (rs.next()) {
				String result = null;
				if (rs.getString(2).equals("")) {
					result = rs.getString(1) + "/" + rs.getString(3);
				} else {
					result = rs.getString(1) + "/" + rs.getString(2) + "/" + rs.getString(3);
				}
				map.put(result, "default");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
		}
		return map;
	}

	public static long getFileId(String location) {
		int firstIndex = location.indexOf('/');
		int lastIndex = location.lastIndexOf('/');
		String ownerName = location.substring(0, firstIndex);
		String fileLocation = (firstIndex == lastIndex) ? "" : location.substring(firstIndex + 1, lastIndex);
		String fileName = location.substring(lastIndex + 1);
		Statement stmt1 = null;
		ResultSet rs1 = null;
		long fileId = 0;
		try {
			stmt1 = con.createStatement();
			rs1 = stmt1.executeQuery("select id from files_info where ownername = '" + ownerName
					+ "' and filelocation = '" + fileLocation + "' and filename = '" + fileName + "'");
			while (rs1.next()) {
				fileId = rs1.getLong(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt1 != null)
					stmt1.close();
			} catch (Exception e) {
			}
			try {
				if (rs1 != null)
					rs1.close();
			} catch (Exception e) {
			}
		}
		return fileId;
	}

	public static void shareFile(String user, String location, String privilege) {
		PreparedStatement prepStmt = null;
		Statement stmt2 = null;
		ResultSet rs2 = null;
		long userId = 0, fileId = 0;
		try {
			fileId = getFileId(location);
			stmt2 = con.createStatement();
			rs2 = stmt2.executeQuery("select id from clients_info where name = '" + user + "'");
			while (rs2.next()) {
				userId = rs2.getLong(1);
			}
			prepStmt = con.prepareStatement("insert ignore into shared_users_info values(?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			prepStmt.setLong(1, fileId);
			prepStmt.setLong(2, userId);
			prepStmt.setString(3, privilege);
			prepStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt2 != null)
					stmt2.close();
			} catch (Exception e) {
			}
			try {
				if (prepStmt != null)
					prepStmt.close();
			} catch (Exception e) {
			}
			try {
				if (rs2 != null)
					rs2.close();
			} catch (Exception e) {
			}
		}
	}

	public static LinkedHashMap<String, String> sharedUsersForAFile(String location) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		Statement stmt = null;
		ResultSet rs = null;
		long fileId = 0;
		try {
			fileId = getFileId(location);
			stmt = con.createStatement();
			rs = stmt.executeQuery(
					"select c.name, s.privilege from clients_info c inner join shared_users_info s where s.file_id = '"
							+ fileId + "' and c.id = s.user_id");
			while (rs.next()) {
				map.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
		}
		if (map.size() == 0) {
			map.put("notshared", "true");
		}
		return map;
	}

	public static void changePrivilege(String sharedUser, String location) {
		Statement stmt = null, stmt1 = null;
		ResultSet rs = null;
		long fileId = 0, userId = 0;
		try {
			fileId = getFileId(location);
			userId = getUserId(sharedUser);
			stmt = con.createStatement();
			rs = stmt.executeQuery("select privilege from shared_users_info where file_id = '" + fileId
					+ "' and user_id = '" + userId + "'");
			while (rs.next()) {
				stmt1 = con.createStatement();
				if (rs.getString(1).equals("read")) {
					stmt1.executeUpdate("update shared_users_info set privilege = 'write' where file_id = '" + fileId
							+ "' and user_id ='" + userId + "'");
				} else {
					stmt1.executeUpdate("update shared_users_info set privilege = 'read' where file_id = '" + fileId
							+ "' and user_id ='" + userId + "'");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
			try {
				if (stmt1 != null)
					stmt1.close();
			} catch (Exception e) {
			}
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
		}
	}

	private static long getUserId(String user) {
		long userId = 0;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			stmt1 = con.createStatement();
			rs1 = stmt1.executeQuery("select id from clients_info where name = '" + user + "'");
			while (rs1.next()) {
				userId = rs1.getLong(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt1 != null)
					stmt1.close();
			} catch (Exception e) {
			}
			try {
				if (rs1 != null)
					rs1.close();
			} catch (Exception e) {
			}
		}
		return userId;
	}

	public static void removeShare(String sharedUser, String location) {
		Statement stmt = null;
		long fileId = 0, userId = 0;
		try {
			fileId = getFileId(location);
			userId = getUserId(sharedUser);
			stmt = con.createStatement();
			stmt.executeUpdate(
					"delete from shared_users_info where file_id = '" + fileId + "' and user_id = '" + userId + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
		}
	}
}
