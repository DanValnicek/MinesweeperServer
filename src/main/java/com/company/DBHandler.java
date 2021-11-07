package com.company;

import com.mysql.cj.xdevapi.DbDoc;
import com.mysql.cj.xdevapi.JsonParser;

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
			"\"register\"", "insert into Users (userName, password) VALUES ( ?, SHA2(CONCAT(NOW(),?),256))"
	);
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public DBHandler() {
	}

	public void executeQuery(String message) throws SQLException {
		DbDoc json = JsonParser.parseDoc(message);
		System.out.println(json.get("operation"));
		System.out.println(json.get("queryType"));
		System.out.println(queries.get(json.get("operation").toString()));
		preparedStatement = connection.prepareStatement(queries.get(json.get("operation").toString()));
		int i = 0;
//		System.out.println(json.containsKey(i));
		System.out.println(json.containsKey(Integer.toString(i)));
		System.out.println(json.get(Integer.toString(i)));
		while (json.containsKey(Integer.toString(i))) {
			System.out.println(json.get(Integer.toString(i)));
			preparedStatement.setObject(i + 1, json.get(Integer.toString(i)));
			i++;
		}
		if (json.get("queryType").equals("query")) {
			System.out.println(preparedStatement.executeQuery());
		} else if (json.get("queryType").equals("update")) {
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

