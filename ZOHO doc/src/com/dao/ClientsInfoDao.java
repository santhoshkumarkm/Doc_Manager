package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

	public static Map<String, String> getSharedFilesForALocation(String location) {
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
				map.put(result, "owner");
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
//		System.out.println("MAP: " + map);
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

	public static JSONObject shareFile(String user, String location, String privilege) {
		PreparedStatement prepStmt = null;
		Statement stmt2 = null;
		ResultSet rs2 = null;
		long userId = 0, fileId = 0;
		JSONObject jsonObject = new JSONObject();
		try {
			fileId = getFileId(location);
			stmt2 = con.createStatement();
			rs2 = stmt2.executeQuery("select id from clients_info where name = '" + user + "'");
			while (rs2.next()) {
				userId = rs2.getLong(1);
			}
			if (privilege.equals("read")) {
				String privilegeCheck = checkLocation(fileId, user);
				if (privilegeCheck != null
						&& privilegeCheck.substring(0, privilegeCheck.indexOf('+')).equals("write")) {
					jsonObject.put("success", "false");
					return jsonObject;
				}
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
		jsonObject.put("success", "true");
		return jsonObject;
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

	public static JSONArray search(LinkedHashMap<Integer, ArrayList<Integer>> wordDetailMap, String user) {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for (Map.Entry<Integer, ArrayList<Integer>> entry : wordDetailMap.entrySet()) {
//			System.out.println("file id: " + entry.getKey() + " user: " + user);
			String fileLocation = checkLocation(entry.getKey(), user);
			if (fileLocation != null) {
				jsonObject.put(fileLocation.substring(fileLocation.indexOf('+') + 1), entry.getValue().size());
			}
		}

		if (jsonObject.size() == 0) {
			return jsonArray;
		}
		Set<Entry<String, Integer>> set = jsonObject.entrySet();
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		for (Map.Entry<String, Integer> aa : list) {
			jsonObject = new JSONObject();
			jsonObject.put(aa.getKey(), aa.getValue());
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}

	public static String checkLocation(long fileId, String user) {
		Statement stmt = null, stmt2 = null, stmt1 = null;
		ResultSet rs = null, rs2 = null, rs1 = null;
		String ownerName = null, fileLocation = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("select ownername, filelocation, filename from files_info where id = '" + fileId
					+ "' and ownername = '" + user + "'");
			while (rs.next()) {
				if (rs.getString(2).equals("")) {
					return "owner" + "+" + rs.getString(1) + "/" + rs.getString(3);
				} else {
					return "owner" + "+" + rs.getString(1) + "/" + rs.getString(2) + "/" + rs.getString(3);
				}
			}

			stmt1 = con.createStatement();
			rs1 = stmt1.executeQuery(
					"select ownername, filelocation, filename from files_info where id = '" + fileId + "'");
			while (rs1.next()) {
				ownerName = rs1.getString(1);
				if (rs1.getString(2).equals("")) {
					fileLocation = rs1.getString(3);
				} else {
					fileLocation = rs1.getString(2) + "/" + rs1.getString(3);
				}
			}

			stmt2 = con.createStatement();
			rs2 = stmt2.executeQuery("select privilege from shared_users_info s, files_info f where s.user_id = '"
					+ getUserId(user) + "'  and f.id = s.file_id and concat(f.filelocation,f.filename) = substring('"
					+ fileLocation + "',1,char_length(concat(f.filelocation,f.filename)))");
			while (rs2.next()) {
				if (rs2.getString(1) != null) {
					return rs2.getString(1) + "+" + ownerName + "/" + fileLocation;
				}
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
				if (stmt1 != null)
					stmt1.close();
			} catch (Exception e) {
			}
			try {
				if (rs1 != null)
					rs1.close();
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
		return null;
	}

	public static LinkedList<String> getFilesInDirectory(String location) {
		Statement stmt = null, stmt2 = null;
		ResultSet rs = null;
		int firstIndex = location.indexOf('/');
		int lastIndex = location.lastIndexOf('/');
		String ownerName = location.substring(0, firstIndex);
		String fileLocation = location.substring(lastIndex+1);
		LinkedList<String> list = new LinkedList<String>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(
					"select CONCAT(ownername, '/', filelocation, '/', filename) from files_info where ownername = '"
							+ ownerName + "' and filelocation like '%" + fileLocation + "%' and filename like '%.txt'");
			while (rs.next()) {
				list.add(rs.getString(1));
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
				if (stmt2 != null)
					stmt2.close();
			} catch (Exception e) {
			}
		}
		System.out.println(list);
		return list;
	}
}
