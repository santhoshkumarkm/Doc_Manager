package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

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

	static public long insertFile(String location) {
		PreparedStatement stmt = null;
		Statement stmt2 = null;
		long primaryKeyValue = 0;
		ResultSet generatedKeys = null, rs = null;
		try {
			stmt = con.prepareStatement("insert ignore into files_info values(null,?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, location);
			stmt.executeUpdate();
			generatedKeys = stmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				primaryKeyValue = generatedKeys.getLong(1);
			} else {
				stmt2 = con.createStatement();
				String qq = "select id from files_info where filelocation='" + location + "'";
				rs = stmt2.executeQuery(qq);
				if (rs.next()) {
					primaryKeyValue = rs.getLong(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (generatedKeys != null)
					generatedKeys.close();
			} catch (Exception e) {
			}
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
			try {
				if (stmt2 != null)
					stmt2.close();
			} catch (Exception e) {
			}

		}
		return primaryKeyValue;
	}

	public static long deleteFile(String location) {
		Statement stmt = null;
		long primaryKeyValue = 0;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate("delete from files_info where filelocation = '" + location + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
		}
		return primaryKeyValue;
	}

	public static LinkedHashSet<String> getSharedUserNamesForAnUser(String userName) {
		Statement stmt = null;
		ResultSet rs = null;
		LinkedHashSet<String> set = new LinkedHashSet<String>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(
					"select f.filelocation from files_info f inner join shared_users_info u on f.id = u.file_id inner join clients_info c on u.user_id = c.id where c.name ='"
							+ userName + "'");
			while (rs.next()) {
				String fileLocation = rs.getString(1);
				set.add(fileLocation.substring(0, fileLocation.indexOf('/')));
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
					"select f.filelocation, u.privilege from files_info f inner join shared_users_info u on f.id = u.file_id inner join clients_info c on u.user_id = c.id where c.name ='"
							+ user + "' and f.filelocation like '" + sharedUser + "%'");
			while (rs.next()) {
				map.put(rs.getString(1), rs.getString(2));
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

	public static LinkedHashMap<String, String> getSharedFilesForALocation(String location, String privilege) {
		Statement stmt = null;
		ResultSet rs = null;
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("select filelocation from files_info where filelocation like '" + location + "/%'");
			while (rs.next()) {
				String result = rs.getString(1);
				if (result.substring(location.length(), result.length()).indexOf('/') == result
						.substring(location.length(), result.length()).lastIndexOf('/')) {
					map.put(result, privilege);
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
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
		}
		return map;
	}

	public static String checkAccess(String location, String rootUser, String sharedUser) {
		LinkedHashMap<String, String> fileListMap = getSharedFilesForAnUser(sharedUser, rootUser);
		for(Map.Entry<String, String> entry : fileListMap.entrySet()) {
			if(location.contains(entry.getKey())) {
				return entry.getValue();
			}
		}
		return "denied";
	}
}
