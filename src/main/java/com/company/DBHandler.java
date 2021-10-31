package com.company;

import java.sql.*;

public class DBHandler {
	public static void connect(String password) throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minesweeperDatabase",
				"workServ", password);
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT * FROM Users ");
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		while (resultSet.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
				if (i > 1) System.out.print(" | ");
				System.out.print(resultSet.getString(i));
			}
			System.out.println("");
		}
	}
}
