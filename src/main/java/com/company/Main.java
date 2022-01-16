package com.company;

import java.sql.SQLException;


public class Main {
	static GamesHandler gamesHandler = new GamesHandler();

	public static void main(String[] args) throws SQLException, ClassNotFoundException {

		Class.forName("com.mysql.cj.jdbc.Driver");
		System.out.println("Enter db password for workServ:");
		while (true) {
			try {
				new DBHandler().connect(new String(System.console().readPassword()));
				break;
			} catch (SQLException e) {
				System.out.println("Wrong password! Try again!");
			}
		}
		new Server(56850).run();
	}
}
