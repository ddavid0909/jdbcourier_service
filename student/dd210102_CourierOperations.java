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
import rs.etf.sab.operations.CourierOperations;

/**
 *
 * @author pc
 */
public class dd210102_CourierOperations implements CourierOperations {

    @Override
    public boolean insertCourier(String string, String string1) {
        int IdKor, IdVoz;
        // string - username , string1 - registarski broj
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement userStmt = conn.prepareStatement("SELECT IdKor FROM Korisnik WHERE KorinickoIme = ? AND IdKor NOT IN (SELECT IdKor FROM Kurir)");
             PreparedStatement vehicleStmt = conn.prepareStatement("SELECT IdVoz FROM Vozilo WHERE RB = ? AND IdVoz NOT IN (SELECT IdVoz FROM Kurir)");
             PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Kurir(IdKor, IdVoz, BrPaketa, Profit, Status) VALUES (?, ?, ?, ?, ?)")
                ) {
            userStmt.setString(1, string);
            vehicleStmt.setString(1, string1);
            
            ResultSet rs = userStmt.executeQuery();
            if (!rs.next()) {
                return false;
            }
            IdKor = rs.getInt(1);
            System.out.println("IdKorisnika " + IdKor);
            
            
            rs = vehicleStmt.executeQuery();
            if (!rs.next()) {
                return false;
            }
            IdVoz = rs.getInt(1);
            
            insertStmt.setInt(1, IdKor);
            insertStmt.setInt(2, IdVoz);
            insertStmt.setInt(3, 0);
            insertStmt.setBigDecimal(4, new BigDecimal(0));
            insertStmt.setInt(5, 0);
            
            return insertStmt.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean deleteCourier(String string) {
        Connection conn = DB.getInstance().getConnection();
        
        try (PreparedStatement CourierStmt = conn.prepareStatement("DELETE FROM Kurir WHERE IdKor = (SELECT IdKor FROM Korisnik WHERE KorinickoIme = ?)");
             PreparedStatement UserStmt = conn.prepareStatement("DELETE FROM Korisnik WHERE KorinickoIme = ?");
                ) {
            CourierStmt.setString(1, string);
            UserStmt.setString(1, string);
            
            CourierStmt.executeUpdate();
            return UserStmt.executeUpdate() > 0;
            
           
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public List<String> getCouriersWithStatus(int i) {
        List<String> list = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT KorinickoIme FROM Korisnik WHERE IdKor IN (SELECT IdKor FROM Kurir WHERE Status = ?))")) {
            pstmt.setInt(1, i);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("KorinickoIme"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }

    @Override
    public List<String> getAllCouriers() {
        List<String> list = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();
        try (Statement pstmt = conn.createStatement()) {
            ResultSet rs = pstmt.executeQuery("SELECT KorinickoIme FROM Korisnik WHERE IdKor IN (SELECT IdKor FROM Kurir)");
            while (rs.next()) {
                list.add(rs.getString("KorinickoIme"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return list;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int i) {
        BigDecimal bd = new BigDecimal(0);
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT AVG(Profit) FROM Kurir WHERE BrPaketa >= ?")) {
            pstmt.setInt(1, i);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                bd = rs.getBigDecimal(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_CourierOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return bd;
        }
    
    
    public static void main(String[] args) {
        dd210102_CourierOperations co = new dd210102_CourierOperations();
        dd210102_VehicleOperations vo = new dd210102_VehicleOperations();
        dd210102_GeneralOperations gop = new dd210102_GeneralOperations();
        dd210102_UserOperations uo = new dd210102_UserOperations();
        
        gop.eraseAll();
        System.out.println("COURIER OPERATIONS");
        System.out.println(uo.insertUser("David", "David", "Duric", "Duric1234567"));
        System.out.println(vo.insertVehicle("210102P", 0, new BigDecimal(20)));
        System.out.println(co.insertCourier("David", "210102P"));
        System.out.println(!co.insertCourier("David1", "210102P"));
        System.out.println(!co.insertCourier("David", "210102P1"));
        
        System.out.println(co.getAllCouriers().size() == 1);
        
        gop.eraseAll();
        
    }
    
}
