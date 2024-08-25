/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pc
 */
public class DB {
    private static final String username="sa";
    private static final String password="123";
    private static final String server="localhost";
    private static final String database="TransportPaketa";
    private static final int port= 1433;
    
    private static final String connectionUrl = "jdbc:sqlserver://" + server + ":" + port + ";encrypt=false;databaseName=" + database;
    
    private static DB dbase = null;
    private static Connection conn;
    
    private DB() {
        try {
            conn = DriverManager.getConnection(connectionUrl, username, password);
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Connection getConnection() {
        return conn;
    }
    
     public static DB getInstance() {
        if (dbase == null) {
            dbase = new DB();
        }
        return dbase;
    }
    
     
     
}
