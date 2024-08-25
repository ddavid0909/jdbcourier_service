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
import rs.etf.sab.operations.CourierRequestOperation;

/**
 *
 * @author pc
 */
public class dd210102_CourierRequestOperation implements CourierRequestOperation {

    @Override
    // username, licensePlate
    public boolean insertCourierRequest(String string, String string1) {
        Connection conn = DB.getInstance().getConnection();
        // mozda ne smije jedan kurir da stavi vise zahtjeva
        
        try (PreparedStatement insertRequest = conn.prepareStatement("INSERT INTO ZahtevZaKurira(IdKor, IdVoz) VALUES(?, ?)");
             PreparedStatement checkRequestForUserExists = conn.prepareStatement("SELECT IdKor FROM ZahtevZaKurira WHERE IdKor = ?");
             PreparedStatement checkCourier = conn.prepareStatement("SELECT k.IdKor FROM Kurir k JOIN Korisnik kor ON (k.IdKor = kor.IdKor) WHERE KorinickoIme = ?");
             PreparedStatement checkVehicle = conn.prepareStatement("SELECT IdVoz FROM Vozilo WHERE RB = ?");
             PreparedStatement getUserID = conn.prepareStatement("SELECT IdKor FROM Korisnik WHERE KorinickoIme = ?");
             PreparedStatement checkExists = conn.prepareStatement("SELECT IdKor, IdVoz FROM ZahtevZaKurira WHERE IdKor = ? AND IdVoz = ?");
                ) {
           
            checkCourier.setString(1, string);
            ResultSet rs = checkCourier.executeQuery();
            if (rs.next()) return false;
            
            getUserID.setString(1, string);
            rs = getUserID.executeQuery();
            if (!rs.next()) return false;
            int IdKor = rs.getInt(1);
            
            checkRequestForUserExists.setInt(1, IdKor);
            rs = checkRequestForUserExists.executeQuery();
            if (rs.next()) return false;
            
            checkVehicle.setString(1, string1);
            rs = checkVehicle.executeQuery();
            if (!rs.next()) return false;
            int IdVoz = rs.getInt(1);
            
            checkExists.setInt(1, IdKor);
            checkExists.setInt(2, IdVoz);
            rs = checkExists.executeQuery();
            if (rs.next()) return false;
            
           
            insertRequest.setInt(1, IdKor);
            insertRequest.setInt(2, IdVoz);
            return insertRequest.executeUpdate() > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean deleteCourierRequest(String string) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement deleteRequest = conn.prepareStatement("DELETE FROM ZahtevZaKurira WHERE IdKor = ?");
             PreparedStatement findUser = conn.prepareStatement("SELECT IdKor FROM Korisnik WHERE KorinickoIme = ?");
                ) {
                findUser.setString(1, string);
                ResultSet rs = findUser.executeQuery();
                if (!rs.next()) return false;
                
                deleteRequest.setInt(1, rs.getInt(1));
                return deleteRequest.executeUpdate() > 0;
                
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    
    public boolean changeVehicleInCourierRequest(String string, String string1) {
       Connection conn = DB.getInstance().getConnection();
       try (PreparedStatement getUser = conn.prepareStatement("SELECT IdKor FROM Korisnik WHERE KorinickoIme = ?");
            PreparedStatement getVehicle = conn.prepareStatement("SELECT IdVoz FROM Vozilo WHERE RB = ?");
            PreparedStatement updateRequest = conn.prepareStatement("UPDATE ZahtevZaKurira SET IdVoz = ? WHERE IdKor = ?")
               ) {
               
           getUser.setString(1, string);
           ResultSet rs = getUser.executeQuery();
           if (!rs.next()) return false;
           int IdKor = rs.getInt(1);
           
           getVehicle.setString(1, string1);
           rs = getVehicle.executeQuery();
           
           if (!rs.next()) return false;
           int IdVoz = rs.getInt(1);
           
           updateRequest.setInt(1, IdVoz);
           updateRequest.setInt(2, IdKor);
           
           return updateRequest.executeUpdate() > 0;
           
       } catch (SQLException ex) {
            Logger.getLogger(dd210102_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
       
        }

    @Override
    public List<String> getAllCourierRequests() {
        List<String> list = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();
        try (Statement stmt = conn.createStatement();
             PreparedStatement getUsername = conn.prepareStatement("SELECT KorinickoIme FROM Korisnik WHERE IdKor = ?");
                ) {
            ResultSet rs = stmt.executeQuery("SELECT IdKor FROM ZahtevZaKurira");
            while (rs.next()) {
                getUsername.setString(1, rs.getString(1));
                list.add(getUsername.executeQuery().getString(1));
            }
    }   catch (SQLException ex) {
            Logger.getLogger(dd210102_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public boolean grantRequest(String string) {
        Connection conn = DB.getInstance().getConnection();
        try (CallableStatement stmt = conn.prepareCall("{? = call dbo.dd210102_stored_procedure(?)}")) {
            stmt.setString(2, string);
            
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.executeUpdate();
            
            return stmt.getInt(1) == 0;
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_CourierRequestOperation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public static void main(String[] args) {
        dd210102_GeneralOperations gop = new dd210102_GeneralOperations();
        dd210102_UserOperations uop = new dd210102_UserOperations();
        dd210102_DistrictOperations dop = new dd210102_DistrictOperations();
        dd210102_CityOperations cop = new dd210102_CityOperations();
        dd210102_PackageOperations pop = new dd210102_PackageOperations();
        dd210102_CourierOperations couop = new dd210102_CourierOperations();
        dd210102_CourierRequestOperation crqop = new dd210102_CourierRequestOperation();
        dd210102_VehicleOperations vop = new dd210102_VehicleOperations();
        
        gop.eraseAll();
        
        System.out.println(uop.insertUser("ddavid0909", "David", "Duric", "ddavid0909")); 
        System.out.println(vop.insertVehicle("BG210102", 0, BigDecimal.ONE));
        System.out.println(vop.insertVehicle("ZV210102", 0, BigDecimal.ONE));
        
        
        System.out.println(!crqop.insertCourierRequest("ddavid0909", "AP210102"));
         
        System.out.println(crqop.insertCourierRequest("ddavid0909", "BG210102"));
        System.out.println(!crqop.insertCourierRequest("ddavid0909", "ZV210102"));
        
        System.out.println(crqop.grantRequest("ddavid0909"));
        
    }
    
}
