/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import rs.etf.sab.operations.GeneralOperations;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pc
 */
public class dd210102_GeneralOperations implements GeneralOperations {

    @Override
    public void eraseAll() {
        Connection conn = DB.getInstance().getConnection();
         try(Statement stmt = conn.createStatement()) {
             stmt.executeUpdate("DELETE FROM ZahtevZaKurira");
             stmt.executeUpdate("DELETE FROM Ponuda");
             stmt.executeUpdate("DELETE FROM Paket");
             stmt.executeUpdate("DELETE FROM Voznja");
             stmt.executeUpdate("DELETE FROM Opstina");
             stmt.executeUpdate("DELETE FROM Grad");
             stmt.executeUpdate("DELETE FROM Kurir");
             stmt.executeUpdate("DELETE FROM Korisnik");
             stmt.executeUpdate("DELETE FROM Vozilo");
         } catch (SQLException ex) {
            Logger.getLogger(dd210102_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
