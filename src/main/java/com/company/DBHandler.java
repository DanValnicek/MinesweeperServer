package com.company;

import java.sql.*;
import java.util.Map;

public class DBHandler {
	static Connection connection;
	//	public static void execQuery(PreparedStatement query) throws SQLException {
//		ResultSetMetaData rsmd = query.getMetaData();
//		int columnsNumber = rsmd.getColumnCount();
//		while (query.next()) {
//			for (int i = 1; i <= columnsNumber; i++) {
//				if (i > 1) System.out.print(" | ");
//				System.out.print(query.getString(i));
//			}
//			System.out.println("");
//		}
//	}
	Map<String, String> queries = Map.of(
			"reg", "insert into Users (userName, password) VALUES ( ?, SHA2(CONCAT(NOW(),?,256)))"
	);
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public DBHandler() {
	}

	public void executeQuery(String type, String queryKey, String rawData) throws SQLException {
		preparedStatement = connection.prepareStatement(queries.get(queryKey));
		String[] values = rawData.split(",");
		for (int i = 0; i < values.length; i++) {
			System.out.println((i + 1) + " value:" + values[i]);
			preparedStatement.setObject(i + 1, values[i]);
		}
		if (type.equals("q")) {
			System.out.println(preparedStatement.executeQuery());
		} else if (type.equals("u")) {
			System.out.println(preparedStatement.executeUpdate());
		}
	}

	public void connect(String password) throws SQLException {
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minesweeperDatabase",
				"workServ", password);
		statement = connection.createStatement();
		System.out.println("Connected to DB");
//		execQuery(statement.executeQuery("SELECT * FROM Users "));
	}


}

