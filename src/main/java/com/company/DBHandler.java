package com.company;

import com.mysql.cj.xdevapi.SqlStatement;

import java.sql.*;

public class DBHandler {
		static Connection connection;
		static Statement statement;

	public static Statement getStatement() {
		return statement;
	}

	public static void connect(String password) throws SQLException {
		statement = connection.createStatement();
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minesweeperDatabase",
				"workServ", password);
		execQuery(statement.executeQuery("SELECT * FROM Users "));
	}

	public static void execQuery(ResultSet query) throws SQLException {
		ResultSetMetaData rsmd = query.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		while (query.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
				if (i > 1) System.out.print(" | ");
				System.out.print(query.getString(i));
			}
			System.out.println("");
		}
	}
}

