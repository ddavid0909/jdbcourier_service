/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author pc
 */
public class dd210102_VehicleOperations implements VehicleOperations {

    @Override
    public boolean insertVehicle(String string, int i, BigDecimal bd) {
        // licencePlateNumber, fuelType, fuelConsumption
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Vozilo WHERE RB = ?");
             PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Vozilo (RB, TipGoriva, Potrosnja) VALUES (?, ?, ?)");
                ) {
            pstmt.setString(1, string);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) return false;
            
            insertStmt.setString(1, string);
            insertStmt.setInt(2, i);
            insertStmt.setBigDecimal(3, bd);
            
            insertStmt.executeUpdate();
            
            return true;
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return false;
    }

    @Override
    public int deleteVehicles(String... strings) {
        int num = 0;
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Vozilo WHERE RB = ?")) {
            for (String string : strings) {
                pstmt.setString(1, string);
                num += pstmt.executeUpdate();
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return num;
    }

    @Override
    public List<String> getAllVehichles() {
         Connection conn = DB.getInstance().getConnection();
         List<String> list = new ArrayList<>();
         
         try (Statement stmt = conn.createStatement()) {
             ResultSet rs = stmt.executeQuery("SELECT RB FROM Vozilo");
             
             while (rs.next()) {
                 list.add(rs.getString("RB"));
             }
             
         } catch (SQLException ex) {
            Logger.getLogger(dd210102_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
         return list;
    }

    @Override
    public boolean changeFuelType(String string, int i) {
        // OBRATI PAZNJU - sta ako se mijenja u onaj koji vec jeste?
        
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE Vozilo SET TipGoriva = ? WHERE RB = ?");
             PreparedStatement checkStmt = conn.prepareStatement("SELECT TipGoriva FROM Vozilo WHERE RB = ?")) {
                checkStmt.setString(1, string);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    if (rs.getInt("TipGoriva") == i) return false;
                }
            
            
                pstmt.setInt(1, i);
                pstmt.setString(2, string);

                return pstmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
        
    }

    @Override
    public boolean changeConsumption(String string, BigDecimal bd) {
            Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE Vozilo SET Potrosnja = ? WHERE RB = ?");
             PreparedStatement checkStmt = conn.prepareStatement("SELECT Potrosnja FROM Vozilo WHERE RB = ?")) {
                checkStmt.setString(1, string);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    if (rs.getBigDecimal("Potrosnja") == bd) return false;
                }
            
            
                pstmt.setBigDecimal(1, bd);
                pstmt.setString(2, string);

                return pstmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_VehicleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
        
        
        
        }
    
}
