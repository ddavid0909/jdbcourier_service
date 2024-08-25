/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author pc
 */
public class dd210102_UserOperations implements UserOperations {

    @Override
    public boolean insertUser(String string, String string1, String string2, String string3) {
        // username, firstName, lastName, password
        
        if (!Character.isUpperCase(string1.charAt(0)) || !Character.isUpperCase(string2.charAt(0))
              || string3.length() < 8) 
                    return false;
        
        boolean containsNum = false;
        boolean containsLetter = false;
        
        for (int i = 0 ; i < string3.length(); i++) {
            if (Character.isLetter(string3.charAt(i))) {
                containsLetter = true;
            } else if (Character.isDigit(string3.charAt(i))) {
                containsNum = true;
            }
        }
        if (!containsNum || !containsLetter) return false;
        
        
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Korisnik WHERE KorinickoIme=?");
            PreparedStatement insertPstmt = conn.prepareStatement("INSERT INTO Korisnik (Ime, Prezime, KorinickoIme, Sifra, BrPaketa) VALUES(?, ?, ?, ?, ?)");
                ) {
            pstmt.setString(1, string);
            ResultSet rs = pstmt.executeQuery();
            
            if(rs.next()) return false;
            
            insertPstmt.setString(1, string1);
            insertPstmt.setString(2, string2);
            insertPstmt.setString(3, string);
            insertPstmt.setString(4, string3);
            insertPstmt.setInt(5, 0);
            
            return insertPstmt.executeUpdate() > 0;
            
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public int declareAdmin(String string) {
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Korisnik WHERE KorinickoIme = ?");
            PreparedStatement declStmt = conn.prepareStatement("UPDATE Korisnik SET Admin = 1 WHERE KorinickoIme = ?", Statement.RETURN_GENERATED_KEYS)
                ) {
            pstmt.setString(1, string);
            
            ResultSet rset = pstmt.executeQuery();
            
            if (!rset.next()) return 2;
            if (rset.getInt("Admin") == 1) return 1;
            
            declStmt.setString(1, string);
            declStmt.executeUpdate();
            /*
            rset = declStmt.getGeneratedKeys();
            
            if (rset.next()) {
                return rset.getInt(1);
            }*/
            return 0;
            
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public Integer getSentPackages(String... strings) {
        Connection conn = DB.getInstance().getConnection();
        int num = -1;
        
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT BrPaketa FROM Korisnik WHERE KorinickoIme = ?")) {
            for (String string : strings) {
                pstmt.setString(1, string);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    if (num == -1) num = rs.getInt("BrPaketa");
                    else num += rs.getInt("BrojPaketa");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return num == -1? null : num;
    }

    @Override
    public int deleteUsers(String... strings) {
        Connection conn = DB.getInstance().getConnection();
        int num = 0;
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Korisnik WHERE KorinickoIme = ?")) {
            for (String string : strings) {
                pstmt.setString(1, string);
                num += pstmt.executeUpdate();
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return num;
    }

    @Override
    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();
        
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT KorinickoIme FROM Korisnik");
            
            while (rs.next()) {
                users.add(rs.getString("KorinickoIme"));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_UserOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return users;
    }
    
    
    
}
