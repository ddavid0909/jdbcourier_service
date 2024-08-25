/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.PackageOperations;
import rs.etf.sab.operations.PackageOperations.Pair;

/**
 *
 * @author pc
 */
public class dd210102_PackageOperations implements PackageOperations {

    private void calculateProfit(String string) {
        // pronadji voznju koja nije zavrsena.
        // zavrsi voznju, a onda pronadji sve pakete iz te voznje u hronoloskom poretku
        // nadji vozilo kurira koji je vozio.
        int IdKurira;
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement getIdKurira = conn.prepareStatement("SELECT IdKor FROM Kurir WHERE IdKor = (SELECT IdKor FROM Korisnik WHERE KorinickoIme = ?)");
             PreparedStatement getTipPotrosnja = conn.prepareStatement("SELECT TipGoriva, Potrosnja FROM Vozilo WHERE IdVoz = (SELECT IdVoz FROM Kurir WHERE IdKor = ?)")
                ) {
            getIdKurira.setString(1, string);
            ResultSet rs = getIdKurira.executeQuery();
            if (!rs.next()) return;
            IdKurira = rs.getInt(1);
            
            
            getTipPotrosnja.setInt(1, IdKurira);
            rs = getTipPotrosnja.executeQuery();
            if (!rs.next()) return;
            int Tip = rs.getInt(1);
            double Potrosnja = rs.getBigDecimal(2).doubleValue();
            
            int[] waste = {15, 36, 32};
            
            try(PreparedStatement getDistances = conn.prepareStatement("SELECT SUM(Cena), SUM(sqrt((o1.x-o2.x)*(o1.x-o2.x) + (o1.y-o2.y)*(o1.y-o2.y))) FROM Paket p JOIN Opstina o1 ON (p.IdOps1 = o1.IdOps) JOIN Opstina o2 ON (p.IdOps2 = o2.IdOps) WHERE IdVoznje = ?");
                PreparedStatement getInBetweens = conn.prepareStatement("WITH tabela(IdPak, IdOps1, IdOps2, RedniBroj) AS "
                        + "(SELECT p.IdPak, p.IdOps1, p.IdOps2,  COUNT(*) as 'RedniBroj' "
                        + "FROM Paket p JOIN Paket p1 ON (p.VremePrihvatanja >= p1.VremePrihvatanja AND p.IdVoznje = p1.IdVoznje)"
                        + "WHERE p.IdVoznje = ? "
                        + "GROUP BY p.IdPak, p.IdOps1, p.IdOps2) "
                        + "SELECT SUM(sqrt((o1.x-o2.x)*(o1.x-o2.x) + (o1.y-o2.y)*(o1.y-o2.y))) "
                        + "FROM tabela s1 JOIN tabela s2 ON (s1.RedniBroj + 1 = s2.RedniBroj) "
                        + "JOIN Opstina o1 ON (s1.IdOps2 = o1.IdOps) JOIN Opstina o2 ON (s2.IdOps1 = o2.IdOps)");
                PreparedStatement getDriveID = conn.prepareStatement("SELECT IdVoznje FROM Voznja WHERE Zavrsena = 0 AND IdKurir = ?");
                PreparedStatement updateDrive = conn.prepareStatement("UPDATE Voznja SET Zavrsena = 1, ProfitVoznje = ? WHERE IdVoznje = ?");
                PreparedStatement updateCourier = conn.prepareStatement("UPDATE Kurir SET Profit = Profit + ? WHERE IdKor = ?");
                    ) {
                getDriveID.setInt(1, IdKurira);
                rs = getDriveID.executeQuery();
                if (!rs.next()) return;
                int IdVoznje = rs.getInt(1);
                
                getDistances.setInt(1, IdVoznje);
                rs = getDistances.executeQuery();
                if (!rs.next()) return;
                double prices = rs.getBigDecimal(1).doubleValue();
                double distance = rs.getBigDecimal(2).doubleValue();
                
                getInBetweens.setInt(1, IdVoznje);
                rs = getInBetweens.executeQuery();
                if (rs.next())
                    distance += rs.getBigDecimal(1).doubleValue();
                
                double profit = prices - distance * waste[Tip] * Potrosnja;
                
                
                
                updateDrive.setBigDecimal(1, new BigDecimal(profit));
                updateDrive.setInt(2, IdVoznje);
                
                updateDrive.executeUpdate();
                
                updateCourier.setBigDecimal(1, new BigDecimal(profit));
                updateCourier.setInt(2, IdKurira);
                
                updateCourier.executeUpdate();
                
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
      private class MyPair<A, B> implements Pair {
        private final A idPon;
        private final B Cena;
        
        public MyPair(A idPon, B Cena){
            this.Cena = Cena;
            this.idPon = idPon;
        }
        
        @Override
        public A getFirstParam() {
            return this.idPon; 
        }

        @Override
        public B getSecondParam() {
            return this.Cena;
        }
        
    }

    @Override
    public int insertPackage(int i, int i1, String string, int i2, BigDecimal bd) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT IdOps FROM Opstina WHERE IdOps = ?");
             PreparedStatement pstmtUser = conn.prepareStatement("SELECT IdKor FROM Korisnik WHERE KorinickoIme = ?");
             PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Paket(Tip, Tezina, Status, IdOps1, IdOps2, IdKor) VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS) 
                ) {
            pstmt.setInt(1, i);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) return -1;
            rs.close();
            pstmt.setInt(1, i1);
            rs = pstmt.executeQuery();
            if (!rs.next()) return -1;
            rs.close();
            
            pstmtUser.setString(1, string);
            rs = pstmtUser.executeQuery();
            
            if (!rs.next()) {
                return -1;
            }
            int IdKor = rs.getInt(1);
            rs.close();

            if (i2 < 0  || i2 > 2) return -1;
            if (bd.doubleValue() < 0) return -1;
            
            insertStmt.setInt(1, i2);
            insertStmt.setBigDecimal(2, bd);
            insertStmt.setInt(3,0);
            insertStmt.setInt(4, i);
            insertStmt.setInt(5, i1);
            insertStmt.setInt(6, IdKor);
            
            insertStmt.executeUpdate();
            
            rs = insertStmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    @Override
    // couriername , package_id, price_percentage
    public int insertTransportOffer(String string, int i, BigDecimal bd) {
        Connection conn = DB.getInstance().getConnection();
        
        try (PreparedStatement courierExists = conn.prepareStatement("SELECT IdKor FROM Kurir WHERE IdKor = (SELECT IdKor FROM Korisnik WHERE KorinickoIme = ?)");
             PreparedStatement packageStatus = conn.prepareStatement("SELECT Status FROM Paket WHERE IdPak = ?");
             PreparedStatement insertOffer = conn.prepareStatement("INSERT INTO PONUDA(Cena, IdPak, IdKor) VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)
             
        ){
            courierExists.setString(1, string);
            ResultSet rs = courierExists.executeQuery();
            if (!rs.next()) return -1;
            int IdKurir = rs.getInt(1);
            
            packageStatus.setInt(1, i);
            rs = packageStatus.executeQuery();
            if (!rs.next()) return -1;
            if (rs.getInt(1) != 0) return -1;
            
            if (bd == null) {
                bd = new BigDecimal(Math.random()*10);
            }
            insertOffer.setBigDecimal(1, bd);
            insertOffer.setInt(2, i);
            insertOffer.setInt(3, IdKurir);
            
            insertOffer.executeUpdate();
            rs = insertOffer.getGeneratedKeys();
            
            if (!rs.next()) return -1;
            return rs.getInt(1);
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
                
            return -1;    
    }
    

    private BigDecimal calculatePrice(int i, Connection conn) {
        // CenaJedneIsporuke= (OSNOVNA_CENA[i] + (TEŽINSKI_FAKTOR[i] * weight) * CENA_PO_KG[i] ) * euklidska_distanca
        // potrebne tezine i distance;
        // informacije o vozilu - koji je tip.
        try (PreparedStatement paketInfo = conn.prepareStatement("SELECT p.IdPak, Tezina, Tip, IdOps1, IdOps2 FROM Paket p JOIN Ponuda pon ON (p.IdPak = pon.IdPak) WHERE pon.IdPon = ? AND p.Cena IS NULL");
             PreparedStatement coordinates = conn.prepareStatement("SELECT x, y FROM Opstina WHERE IdOps = ?");
             PreparedStatement procenat = conn.prepareStatement("SELECT Cena FROM Ponuda WHERE IdPon = ?");
                ) {
                paketInfo.setInt(1, i);
                ResultSet rs = paketInfo.executeQuery();
                if (!rs.next()) return null;
                int IdPak = rs.getInt(1);
                BigDecimal Tezina = rs.getBigDecimal(2);
                int Tip = rs.getInt(3);
                
                double osnovnaCena = 0;
                double TezinskiFaktor = 0;
                double CenaPoKG = 0;
                switch(Tip) {
                    case 0:
                        osnovnaCena = 10;
                        break;
                    case 1:
                        osnovnaCena = 25;
                        TezinskiFaktor = 1;
                        CenaPoKG = 100;
                        break;
                    case 2:
                        osnovnaCena = 75;
                        TezinskiFaktor = 2;
                        CenaPoKG = 300;
                        break;
                }
                int IdOps1 =rs.getInt(4);
                int IdOps2 = rs.getInt(5);
                double x1, x2, y1, y2;
                
                coordinates.setInt(1, IdOps1);
                rs = coordinates.executeQuery();
                rs.next();
                x1 = rs.getBigDecimal(1).doubleValue();
                y1 = rs.getBigDecimal(2).doubleValue();
                
                coordinates.setInt(1, IdOps2);
                rs = coordinates.executeQuery();
                rs.next();
                x2 = rs.getBigDecimal(1).doubleValue();
                y2 = rs.getBigDecimal(2).doubleValue();
                
                double distance = Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
                
                procenat.setInt(1, i);
                rs = procenat.executeQuery();
                if (!rs.next()) {
                    System.out.println("CALCULATE PRICE");
                    return new BigDecimal(-1);
                }
                double dodatno = rs.getBigDecimal(1).doubleValue();
                
                return new BigDecimal((osnovnaCena + TezinskiFaktor*Tezina.doubleValue()*CenaPoKG)*distance*(1+dodatno/100.0));
                
                
                
        } catch (SQLException ex) {
              Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
          }
        return null;
    }
    
    @Override
    // offerId sent.
    public boolean acceptAnOffer(int i) {
        // find package the offer refers to.
        // see if it has already been accepted.
        // if it has - return false;
        // else - update the package state - update time of acceptance, calculate price, and courier.
        // on package update OF status - trigger deletes all the offers.
        
        Connection conn = DB.getInstance().getConnection();
        BigDecimal price = this.calculatePrice(i, conn);
        if (price == null) return false;
        try (PreparedStatement accept = conn.prepareStatement("UPDATE Paket SET Status = 1, Cena = ?, VremePrihvatanja = getdate(), "
                + "IdKurir = (SELECT IdKor FROM Ponuda WHERE IdPon = ?)"
                + "WHERE Status = 0 AND IdPak = (SELECT IdPak FROM Ponuda WHERE IdPon = ?)")) {
                    accept.setInt(2, i);
                    accept.setInt(3, i);
                    accept.setBigDecimal(1, price);
                    
                    return accept.executeUpdate() > 0;
            
        } catch (SQLException ex) {
              Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
          }
        
        return false;
        // TO-DO: Treba sa trigerom
        
        
    }

    @Override
    public List<Integer> getAllOffers() {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> ret = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT IdPon FROM Ponuda");
            while (rs.next()) {
                ret.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public List<Pair<Integer, BigDecimal>> getAllOffersForPackage(int i) {
        Connection conn = DB.getInstance().getConnection();
        List<Pair<Integer, BigDecimal>> ret = new ArrayList<>();
        
        try (PreparedStatement retVals = conn.prepareStatement("SELECT IdPon, Cena FROM Ponuda WHERE IdPak = ?")) {
            retVals.setInt(1, i);
            ResultSet rs = retVals.executeQuery();
            while (rs.next()) {
                ret.add(new MyPair(rs.getInt(1), rs.getBigDecimal(2)));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public boolean deletePackage(int i) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Paket WHERE IdPak = ?")) {
            pstmt.setInt(1, i);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
              Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
          }
        return false;
    }

    @Override
    public boolean changeWeight(int i, BigDecimal bd) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE Paket SET Tezina = ? WHERE IdPak = ? AND Status = 0")) {
            pstmt.setBigDecimal(1, bd);
            pstmt.setInt(2, i);
            return pstmt.executeUpdate()>0;
        } catch (SQLException ex) {
              Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
          }
        return false;
    }

    @Override
    public boolean changeType(int i, int i1) { 
        if (i1 < 0 || i1 > 2) return false;
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("UPDATE Paket SET Tip = ? WHERE IdPak = ? AND Status = 0")) {
            pstmt.setInt(1, i1);
            pstmt.setInt(2, i);
            return pstmt.executeUpdate()>0;
        } catch (SQLException ex) {
              Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
          }
        return false;
    }

    @Override
    public Integer getDeliveryStatus(int i) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT Status FROM Paket WHERE IdPak = ?")) {
            pstmt.setInt(1, i);
            ResultSet rset = pstmt.executeQuery();
            if (!rset.next()) return null;
            return rset.getInt(1);
        } catch (SQLException ex) {
              Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
          }
        return null;
    }

    @Override
    public BigDecimal getPriceOfDelivery(int i) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT Cena FROM Paket WHERE IdPak = ?")) {
            pstmt.setInt(1, i);
            ResultSet rset = pstmt.executeQuery();
            if (!rset.next()) return null;
            return rset.getBigDecimal(1);
        } catch (SQLException ex) {
              Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
          }
        return null;
    }

    @Override
    public Date getAcceptanceTime(int i) {
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT VremePrihvatanja FROM Paket WHERE IdPak = ?")) {
            pstmt.setInt(1, i);
            ResultSet rset = pstmt.executeQuery();
            if (!rset.next()) return null;
            return rset.getDate(1);
        } catch (SQLException ex) {
              Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
          }
        return null;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int i) {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> lista = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT IdPak FROM Paket WHERE Tip = ?")) {
            pstmt.setInt(1, i);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
              Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
          }
        return lista;
    }

    @Override
    public List<Integer> getAllPackages() {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> lista = new ArrayList<>();
        try (Statement pstmt = conn.createStatement()) {
            ResultSet rs = pstmt.executeQuery("SELECT IdPak FROM Paket");
            while (rs.next()) {
                lista.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
              Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
          }
        return lista;
        }

    @Override
    public List<Integer> getDrive(String string) {
        //pronaci najnoviju voznju za kurira. Ako je aktivna, onda je samo 
        // vratiti, a ako nije, onda vratiti null
        Connection conn = DB.getInstance().getConnection();
        try (PreparedStatement lastActiveDrive = conn.prepareStatement("SELECT IdVoznje FROM Voznja WHERE IdKurir = "
                + "(SELECT IdKor FROM Korisnik WHERE KorinickoIme = ?) AND Zavrsena = 0");
             PreparedStatement findPackages = conn.prepareStatement("SELECT IdPak FROM Paket WHERE IdVoznje = ? AND Status = 2 ORDER BY VremePrihvatanja")
                ) {
                lastActiveDrive.setString(1, string);
                ResultSet rs = lastActiveDrive.executeQuery();
                
                if (!rs.next()) return null;
                int idVoznje = rs.getInt(1);
                    
                findPackages.setInt(1, idVoznje);
                rs = findPackages.executeQuery();
                
                
                List<Integer> ret = new LinkedList<>();
                
                while(rs.next()) {
                    ret.add(rs.getInt(1));
                }
                return ret;
                
        } catch (SQLException ex) {
              Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
          }
        return null;
    }

    @Override
    public int driveNextPackage(String string) {
        //This method invocation starts a drive of deliveries. 
        //If drive has already started, then drive next delivery according to the given algorithm (see project documentation).
        //So each time this method is called, if there are pending packages, one of them is delivered.
        List<Integer> toDrive = this.getDrive(string);
        Connection conn = DB.getInstance().getConnection();
        if (toDrive == null || toDrive.isEmpty()) {
            // provjeri da li ima paketa koji su za ovog kurira 
            try(    PreparedStatement getIdKurira = conn.prepareStatement("SELECT IdKor FROM Kurir WHERE IdKor = (SELECT IdKor FROM Korisnik WHERE KorinickoIme=?)");
                    PreparedStatement updatePackages = conn.prepareStatement("UPDATE Paket SET Status = 2 WHERE IdKurir = ?");
                    PreparedStatement updatePackagesDrive = conn.prepareStatement("UPDATE Paket SET IdVoznje = ? WHERE IdKurir = ? AND Status = 2");
                    PreparedStatement newDrive = conn.prepareStatement("INSERT INTO Voznja(IdKurir, Zavrsena, ProfitVoznje) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
                    ) {
                getIdKurira.setString(1, string);
                ResultSet rs = getIdKurira.executeQuery();
                if (!rs.next()) return -2;
                int IdKurir = rs.getInt(1);
                
                updatePackages.setInt(1, IdKurir);
                if (updatePackages.executeUpdate() == 0) return -1;
                
                newDrive.setInt(1, IdKurir);
                newDrive.setInt(2, 0);
                newDrive.setBigDecimal(3, new BigDecimal(0));
                
                newDrive.executeUpdate();
                
                rs = newDrive.getGeneratedKeys();
                if (!rs.next()) return -2;
                int IdVoznje = rs.getInt(1);
                
                updatePackagesDrive.setInt(1, IdVoznje);
                updatePackagesDrive.setInt(2, IdKurir);
                
                updatePackagesDrive.executeUpdate();
                
                
            } catch (SQLException ex) {
                Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            // prebaci kurira u stanje vozi.
            // insert into Voznja -> update Paket WHERE IdKor = string and IdVoznja is null and Status = 1 -> Sada je ovo IdVoznja
        } 
        toDrive = this.getDrive(string);
        // ponovo se dohvata lista, koja ako je i dalje prazna znaci da vozac nema sta da dostavlja.
        if (toDrive == null || toDrive.isEmpty()) {
            return -1;
        }

        int idPak = toDrive.remove(0);
        try(PreparedStatement packageSent = conn.prepareStatement("UPDATE Paket SET Status = 3 WHERE IdPak = ?");) {
            
            packageSent.setInt(1, idPak);
            packageSent.executeUpdate();
            
            
        } catch (SQLException ex) {
              Logger.getLogger(dd210102_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
          }
        if (toDrive.isEmpty()) {
            calculateProfit(string);
        }
        
        return idPak;

        }
    
    



    static double euclidean(int x1, int y1, int x2, int y2) {
      return Math.sqrt((double)((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
   }

   static BigDecimal getPackagePrice(int type, BigDecimal weight, double distance, BigDecimal percentage) {
      percentage = percentage.divide(new BigDecimal(100));
      switch(type) {
      case 0:
         return (new BigDecimal(10.0D * distance)).multiply(percentage.add(new BigDecimal(1)));
      case 1:
         return (new BigDecimal((25.0D + weight.doubleValue() * 100.0D) * distance)).multiply(percentage.add(new BigDecimal(1)));
      case 2:
         return (new BigDecimal((75.0D + weight.doubleValue() * 300.0D) * distance)).multiply(percentage.add(new BigDecimal(1)));
      default:
         return null;
      }
   
}
    
    public static void main(String[] args) {
        dd210102_GeneralOperations gop = new dd210102_GeneralOperations();
        dd210102_UserOperations uop = new dd210102_UserOperations();
        dd210102_DistrictOperations dop = new dd210102_DistrictOperations();
        dd210102_CityOperations cop = new dd210102_CityOperations();
        dd210102_PackageOperations pop = new dd210102_PackageOperations();
        dd210102_CourierOperations couop = new dd210102_CourierOperations();
        dd210102_VehicleOperations vop = new dd210102_VehicleOperations();
        dd210102_CourierRequestOperation crqop = new dd210102_CourierRequestOperation();
        gop.eraseAll();
        /*
        System.out.println(uop.insertUser("ddavid0909", "David", "Duric", "ddavid0909")); 
        int cid = cop.insertCity("Belgrade", "01023");
        System.out.println(cid != -1);
        int d1 = dop.insertDistrict("Palilula", cid, 1, 2);
        System.out.println(d1 != -1);
        int d2= dop.insertDistrict("Vracar", cid, 3, 4);
        System.out.println(d2 != -1);
        int id = pop.insertPackage(d2, d1, "ddavid0909", 1, BigDecimal.ONE);
        System.out.println(id != -1);
        
        System.out.println(vop.insertVehicle("BG210102", 1, BigDecimal.ONE));
        System.out.println(uop.insertUser("ddavid0901", "David", "Duric", "ddavid0909"));
        
        System.out.println(couop.insertCourier("ddavid0901", "BG210102"));
        
        int offerId = pop.insertTransportOffer("ddavid0901", id, BigDecimal.ONE);
        int offerId1 = pop.insertTransportOffer("ddavid0901", id, BigDecimal.ONE);
        System.out.println(offerId != -1);
        
        System.out.println(pop.acceptAnOffer(offerId));
        
        //gop.eraseAll();
        */
       
        
      String courierLastName = "Ckalja";
      String courierFirstName = "Pero";
      String courierUsername = "perkan";
      String password = "sabi2018";
      System.out.println(uop.insertUser(courierUsername, courierFirstName, courierLastName, password));
      String licencePlate = "BG323WE";
      int fuelType = 0;
      BigDecimal fuelConsumption = new BigDecimal(8.3D);
      vop.insertVehicle(licencePlate, fuelType, fuelConsumption);
      crqop.insertCourierRequest(courierUsername, licencePlate);
      crqop.grantRequest(courierUsername);
      System.out.println(couop.getAllCouriers().contains(courierUsername));
      String senderUsername = "masa";
      String senderFirstName = "Masana";
      String senderLastName = "Leposava";
      password = "lepasampasta1";
      uop.insertUser(senderUsername, senderFirstName, senderLastName, password);
      int cityId = cop.insertCity("Novo Milosevo", "21234");
      int cordXd1 = 10;
      int cordYd1 = 2;
      int districtIdOne = dop.insertDistrict("Novo Milosevo", cityId, cordXd1, cordYd1);
      int cordXd2 = 2;
      int cordYd2 = 10;
      int districtIdTwo = dop.insertDistrict("Vojinovica", cityId, cordXd2, cordYd2);
      int type1 = 0;
      BigDecimal weight1 = new BigDecimal(123);
      
      int packageId1 = pop.insertPackage(districtIdOne, districtIdTwo, senderUsername, type1, weight1);
      BigDecimal packageOnePrice = getPackagePrice(type1, weight1, euclidean(cordXd1, cordYd1, cordXd2, cordYd2), new BigDecimal(5));
      int offerId = pop.insertTransportOffer(courierUsername, packageId1, new BigDecimal(5));
      pop.acceptAnOffer(offerId);
      int type2 = 1;
      BigDecimal weight2 = new BigDecimal(321);
      int packageId2 = pop.insertPackage(districtIdTwo, districtIdOne, courierUsername, type2, weight2);
      BigDecimal packageTwoPrice = getPackagePrice(type2, weight2, euclidean(cordXd1, cordYd1, cordXd2, cordYd2), new BigDecimal(5));
      offerId = pop.insertTransportOffer(courierUsername, packageId2, new BigDecimal(5));
      pop.acceptAnOffer(offerId);
      int type3 = 1;
      BigDecimal weight3 = new BigDecimal(222);
      int packageId3 = pop.insertPackage(districtIdTwo, districtIdOne, courierUsername, type3, weight3);
      BigDecimal packageThreePrice = getPackagePrice(type3, weight3, euclidean(cordXd1, cordYd1, cordXd2, cordYd2), new BigDecimal(5));
      offerId = pop.insertTransportOffer(courierUsername, packageId3, new BigDecimal(5));
      pop.acceptAnOffer(offerId);
      System.out.println("LINE 632 " + (1L == (long)pop.getDeliveryStatus(packageId1)));
      System.out.println("LINE 633" + ((long)packageId1 == (long)pop.driveNextPackage(courierUsername)));
      System.out.println("LINE 634" + (3L == (long)pop.getDeliveryStatus(packageId1)));
      System.out.println("LINE 635" + (2L == (long)pop.getDeliveryStatus(packageId2)));
      System.out.println("LINE 636" + ((long)packageId2 == (long)pop.driveNextPackage(courierUsername)));
      System.out.println("LINE 637" + (3L == (long)pop.getDeliveryStatus(packageId2)));
      System.out.println("LINE 638" + (2L == (long)pop.getDeliveryStatus(packageId3)));
      System.out.println("LINE 639" + ((long)packageId3 == (long)pop.driveNextPackage(courierUsername)));
      System.out.println("LINE 640" + (3L == (long)pop.getDeliveryStatus(packageId3)));
      BigDecimal gain = packageOnePrice.add(packageTwoPrice).add(packageThreePrice);
      BigDecimal loss = (new BigDecimal(euclidean(cordXd1, cordYd1, cordXd2, cordYd2) * 4.0D * 15.0D)).multiply(fuelConsumption);
      System.out.println(gain.subtract(loss));
      BigDecimal actual = couop.getAverageCourierProfit(0);
      System.out.println("LOSS " + loss);
      System.out.println(gain.subtract(loss).compareTo(actual.multiply(new BigDecimal(1.001D))) < 0);
      System.out.println(actual);
      System.out.println(gain.subtract(loss).compareTo(actual.multiply(new BigDecimal(0.999D))) > 0);

   }
}

       

