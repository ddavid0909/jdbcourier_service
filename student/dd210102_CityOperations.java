/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.util.List;
import rs.etf.sab.operations.CityOperations;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;


/**
 *
 * @author pc
 */
public class dd210102_CityOperations implements CityOperations {

    @Override
    public int insertCity(String string, String string1) {
         Connection conn = DB.getInstance().getConnection();
         try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM Grad WHERE Naziv = ? OR PB = ?");
              PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Grad(Naziv, PB) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS)
                 ) {
             pstmt.setString(1, string);
             pstmt.setInt(2, Integer.parseInt(string1));
             ResultSet rset = pstmt.executeQuery();
             
             if (rset.next()) return -1;
             
             insertStmt.setString(1, string);
             insertStmt.setInt(2, Integer.parseInt(string1));
             insertStmt.executeUpdate();
             
             ResultSet generatedKeys = insertStmt.getGeneratedKeys();
             if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
             }
             
             
             
         } catch (SQLException ex) {
            Logger.getLogger(dd210102_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    public int deleteCity(String... strings) {
        int num = 0;
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Grad WHERE Naziv=?")) {
            for (String string : strings) {
                pstmt.setString(1, string);
                num += pstmt.executeUpdate();
            }
            return num;
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return num;
    }

    @Override
    public boolean deleteCity(int i) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Grad WHERE IdGra = ?")) {
            pstmt.setInt(1, i);
            int ret = pstmt.executeUpdate();
            
            return ret > 0;
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
           
        return false;
    }

    @Override
    public List<Integer> getAllCities() {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> answer = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet set = stmt.executeQuery("SELECT IdGra FROM Grad")) {
            
             while (set.next()) {
                 answer.add(set.getInt(1));
             }
                
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return answer;
    }
    
    public static void main(String[] args) {
        dd210102_CityOperations co = new dd210102_CityOperations();
        /*String name1 = "Tokyo";
        String name2 = "Beijing";
        String postalCode1 = "100";
        String postalCode2 = "065001";
        co.insertCity(name1, postalCode1);
        co.insertCity(name2, postalCode2);
        List<Integer> list = co.getAllCities();
        for (Integer elem : list) {
            System.out.println("Element " + elem);
        }
        System.out.println("Obrisano " + co.deleteCity(new String[]{name1, name2}));*/
        /*
        String name = "Tokyo";
        String postalCode = "100";
        int rowId = co.insertCity(name, postalCode);
        List<Integer> list = co.getAllCities();
        System.out.println(list.size() == 1);
        System.out.println(list.contains(rowId));*/
        /*
        String name = "Tokyo";
        String postalCode = "100";
        int rowIdValid = co.insertCity(name, postalCode);
        int rowIdInvalid = co.insertCity(name, postalCode);
        System.out.println(rowIdInvalid == -1);
        List<Integer> list = co.getAllCities();
        System.out.println(list.size() == 1);
        System.out.println(list.contains(rowIdValid));
        */
        
     /* String name = "Tokyo";
      String postalCode1 = "100";
      String postalCode2 = "1020";
      int rowIdValid = co.insertCity(name, postalCode1);
      System.out.println(rowIdValid);
      int rowIdInvalid = co.insertCity(name, postalCode2);
      System.out.println(-1L == (long)rowIdInvalid);
      List<Integer> list = co.getAllCities();
      System.out.println(1L ==(long)list.size());
      System.out.println(list.contains(rowIdValid));*/
     /*
      String name1 = "Tokyo";
      String name2 = "Beijing";
      String postalCode = "100";
      int rowIdValid = co.insertCity(name1, postalCode);
      int rowIdInvalid = co.insertCity(name2, postalCode);
      System.out.println(-1L == (long)rowIdInvalid);
      List<Integer> list = co.getAllCities();
      System.out.println(1L == (long)list.size());
      System.out.println(list.contains(rowIdValid));
     */
     /*
      String name1 = "Tokyo";
      String name2 = "Beijing";
      String postalCode1 = "100";
      String postalCode2 = "065001";
      int rowId1 = co.insertCity(name1, postalCode1);
      int rowId2 = co.insertCity(name2, postalCode2);
      List<Integer> list = co.getAllCities();
      System.out.println(2L == (long)list.size());
      System.out.println(list.contains(rowId1));
      System.out.println(list.contains(rowId2));
     */
     /*
      String name = "Beijing";
      String postalCode = "065001";
      int rowId = co.insertCity(name, postalCode);
      System.out.println(-1L != (long)rowId);
      System.out.println(co.deleteCity(rowId));
      System.out.println(0L == (long)co.getAllCities().size());
    */
     /*
     Random random = new Random();
      int rowId = random.nextInt();
      System.out.println(!co.deleteCity(rowId));
      System.out.println(0L == (long)co.getAllCities().size());
     */
     /*
     String name = "Beijing";
     String postalCode = "065001";
     int rowId = co.insertCity(name, postalCode);
     System.out.println(-1L != (long)rowId);
     System.out.println(1L == (long)co.deleteCity(new String[]{name}));
     System.out.println(0L == (long)co.getAllCities().size());
*/
/*
     String name1 = "Tokyo";
      String name2 = "Beijing";
      String postalCode1 = "100";
      String postalCode2 = "065001";
      co.insertCity(name1, postalCode1);
      co.insertCity(name2, postalCode2);
      List<Integer> list = co.getAllCities();
      System.out.println(2L == (long)list.size());
      System.out.println(2L == (long)co.deleteCity(new String[]{name1, name2}));

*/
/*
    String name = "Tokyo";
    System.out.println(0L == (long)co.deleteCity(new String[]{name}));
    System.out.println(0L == (long)co.getAllCities().size());
*/

    }
    
}
