package com.company;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Server Started");
        try {
            new Server("165.22.76.230", 56850);
            while (true) {
                Thread.sleep( 1000);
            }
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Server closed");
    }
}
