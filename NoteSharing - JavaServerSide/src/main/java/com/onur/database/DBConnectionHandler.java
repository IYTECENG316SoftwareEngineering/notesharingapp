package com.onur.database;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
 
 
public class DBConnectionHandler {
 
    Connection con = null;
 
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");//Mysql Connection
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            con = DriverManager.getConnection("jdbc:mysql://mysql.tuneintoblack.com/notesharing", "notesharing", "noteapp007");
            //mysql database
 
        } catch (SQLException ex) {
            Logger.getLogger(DBConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }
}
