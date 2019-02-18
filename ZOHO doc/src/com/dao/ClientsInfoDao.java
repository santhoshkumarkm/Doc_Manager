package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
}
