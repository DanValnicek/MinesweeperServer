package com.company;

import java.sql.SQLException;


public class Main {


	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		System.out.println("Enter db password for workServ:");
		DBHandler.connect(new String(System.console().readPassword()));
		new Server(56850).run();
	}
}
