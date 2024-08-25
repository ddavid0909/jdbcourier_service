/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.DistrictOperations;

/**
 *
 * @author pc
 */
public class dd210102_DistrictOperations implements DistrictOperations {

    @Override
    public int insertDistrict(String string, int i, int i1, int i2) {
        Connection conn = DB.getInstance().getConnection();
        int IdGra = i;
        // Grad postoji
        try(PreparedStatement stmt = conn.prepareStatement("SELECT IdGra FROM Grad WHERE IdGra=?");
            PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Opstina (Naziv, x, y, IdGra) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, IdGra);
            ResultSet rset = stmt.executeQuery();
            if (!rset.next()) return -1;
            //System.out.println("Grad postoji!");
            insertStmt.setString(1, string);
            insertStmt.setInt(2, i1);
            insertStmt.setInt(3, i2);
            insertStmt.setInt(4, i);
            
            insertStmt.executeUpdate();
            
            ResultSet keys = insertStmt.getGeneratedKeys();
            if (keys.next())
                return keys.getInt(1);
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int deleteDistricts(String... strings) {
        Connection conn = DB.getInstance().getConnection();
        int num = 0;
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Opstina WHERE Naziv = ?")) {
            for (String name : strings) {
                pstmt.setString(1, name);
                num += pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return num;
    }

    @Override
    public boolean deleteDistrict(int i) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Opstina WHERE IdOps = ?")) {
            pstmt.setInt(1,i);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public int deleteAllDistrictsFromCity(String string) {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> list = new ArrayList<>();
        
        try(PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Opstina WHERE IdGra = (SELECT IdGra FROM Grad WHERE Naziv = ?)")) {
            pstmt.setString(1, string);
            return pstmt.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public List<Integer> getAllDistrictsFromCity(int i) {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> list = new ArrayList<>();
        
        try(PreparedStatement pstmt = conn.prepareStatement("SELECT IdOps FROM Opstina WHERE IdGra = ?")) {
            pstmt.setInt(1, i);
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()) {
                list.add(rset.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    @Override
    public List<Integer> getAllDistricts() {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rset = stmt.executeQuery("SELECT IdOps FROM Opstina");
            while (rset.next()) {
                list.add(rset.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_DistrictOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    
}
