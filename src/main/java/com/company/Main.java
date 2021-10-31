package com.company;

import java.io.IOException;
import java.sql.SQLException;


public class Main {


	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		System.out.println("Enter db password for workServ:");
		DBHandler.connect(new String(System.console().readPassword()));
		new Server(56850).run();
	}
}
