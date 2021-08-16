package com.company;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Server Started");
        try {
            new Server("127.0.0.1", 56850);
            while (true) {
                Thread.sleep(10 * 1000);
            }
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Server closed");
    }
}
