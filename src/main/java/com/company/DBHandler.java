package com.company;

import org.json.simple.JSONObject;

import java.sql.*;
import java.util.List;
import java.util.Map;

import static com.company.MessageTypes.*;

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
	Map<String, String> updateQueries = Map.of("uRegister", "insert into minesweeperDatabase.Users (userName, password) VALUES ( ?, SHA2(CONCAT(NOW(),?),256))");
	Map<String, String> queries = Map.of(
//			"qLogin","select IF(password = SHA2(CONCAT(registered, ?), 256),JSON_ARRAY('true'),JSON_ARRAY('false'))from minesweeperDatabase.Users where userName = ?"
			"qLogin", "select IF(password = SHA2(CONCAT(registered, ?), 256),'success','fail')from minesweeperDatabase.Users where userName = ?"
	);

	public DBHandler() {
	}

	public JSONObject executeQuery(List<String> args, String operation) throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		if (operation.charAt(0) == 'u') {
			preparedStatement = connection.prepareStatement(updateQueries.get(operation));
		} else if (operation.charAt(0) == 'q') {
			preparedStatement = connection.prepareStatement(queries.get(operation));
		}
		if (preparedStatement == null) {
			return JsonGenerator.createCallback(e, "Operation " + operation + " is invalid!");
		}
		for (int i = 0; i < args.size(); i++) {
//			System.out.println(i + 1);
//			System.out.println(args.get(i));
			preparedStatement.setObject(i + 1, args.get(i));
		}
		if (operation.charAt(0) == 'u') {
			System.out.println(preparedStatement);
			preparedStatement.executeUpdate();
			return JsonGenerator.createCallback(i, operation + "-ok");
		} else {
			resultSet = preparedStatement.executeQuery();
//			JSONObject jsonObject = new JSONObject();
			StringBuilder out = new StringBuilder();
			System.out.println(resultSet.getMetaData());
			System.out.println(resultSet.getMetaData().getColumnCount());
			try {
				for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
					resultSet.next();
					System.out.println(resultSet.getString(i));
//				jsonObject.put(resultSet.getMetaData().getColumnName(i),resultSet.getString(i));
//				System.out.println(jsonObject);
					out.append(resultSet.getString(i));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return JsonGenerator.createCallback(q, operation + "-" + out);
		}
	}

	public void connect(String password) throws SQLException {
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minesweeperDatabase", "workServ", password);
		System.out.println("Connected to DB");
//		execQuery(statement.executeQuery("SELECT * FROM Users "));
	}


}

