package com.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginDao {
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

	public static boolean checkClient(String name, String password) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("select * from clients_info");
			while (rs.next()) {
				if (rs.getString(2).equals(name) && rs.getString(3).equals(password)) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
		}
		return false;
	}

	public static void addClient(String name, String password) {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement("insert into clients_info values(null,?,?)");
			stmt.setString(1, name);
			stmt.setString(2, password);
			stmt.execute();
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
