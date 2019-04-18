package Model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

//apache config -> php.ini ->   post_max_size=20M; upload_max_filesize=20M;
//mysql config -> my.ini -> max_allowed_packet = 100M;
public class DB {

    private static DB instance = null;
    private Connection connection;
    private final String dbName = "mojakorpa";
    private final String url = "jdbc:mysql://localhost/" + dbName;
    private final String user = "root";
    private final String pass = "";
    private final HashMap<String, String> charReplacements = new HashMap() {
        {
            //č
            put("├ä´┐¢", "c");
            //ž
            put("├à┬¥", "z");
            //š
            put("├à┬í", "s");
            //ć
            put("├äôçí", "c");
            //đ
            put("├äÔÇÿ", "dj");

        }
    };

    public Map<String, ArrayList<Product>> getProductsBarcode() {
        Map<String, ArrayList<Product>> products = new HashMap<>();
        String query = "SELECT * FROM `product`";
        try {
            PreparedStatement ps = (PreparedStatement) connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product current = new Product(rs.getInt("product_id"), rs.getString("product_shop"), rs.getString("product_name"),
                        rs.getString("product_description"), rs.getString("product_manufacturer"), rs.getDouble("product_price"),
                        rs.getString("product_barcode"), rs.getString("product_image"), rs.getString("product_statistical_price"));

                //Formatiranje naziva proizvoda
                String name = current.getName().trim().toLowerCase().replace("?", "c");
                if (name.charAt(0) > 64 && name.charAt(0) < 123) {
                    char capitalFirst = (char) (name.charAt(0) - 32);
                    name = name.replaceFirst(name.charAt(0) + "", capitalFirst + "");
                }

                //Zamena karaktera sa pogresnim enkodingom.
                for (String encodedChar : charReplacements.keySet()) {
                    if (name.contains(encodedChar)) {
                        name = name.replaceAll(encodedChar, charReplacements.get(encodedChar));
                    }
                }
                current.setName(name);
                
                //Dodavanje u mapu
                if (products.containsKey(current.getBarcode())) {
                    products.get(current.getBarcode()).add(current);
                } else {
                    ArrayList<Product> productList = new ArrayList<>();
                    productList.add(current);
                    products.put(current.getBarcode(), productList);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return products;
    }

    public Map<String, ArrayList<Product>> getProductsName() {
        Map<String, ArrayList<Product>> products = new HashMap<String, ArrayList<Product>>();
        String query = "SELECT * FROM `product`";
        try {
            PreparedStatement ps = (PreparedStatement) connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product current = new Product(rs.getInt("product_id"), rs.getString("product_shop"), rs.getString("product_name"),
                        rs.getString("product_description"), rs.getString("product_manufacturer"), rs.getDouble("product_price"),
                        rs.getString("product_barcode"), rs.getString("product_image"), rs.getString("product_statistical_price"));

                //Formatiranje naziva proizvoda
                String name = current.getName().trim().toLowerCase().replace("?", "c");
                if (name.charAt(0) > 64 && name.charAt(0) < 123) {
                    char capitalFirst = (char) (name.charAt(0) - 32);
                    name = name.replaceFirst(name.charAt(0) + "", capitalFirst + "");
                }
                 //Zamena karaktera sa pogresnim enkodingom.
                for (String encodedChar : charReplacements.keySet()) {
                    if (name.contains(encodedChar)) {
                        name = name.replaceAll(encodedChar, charReplacements.get(encodedChar));
                    }
                }
                current.setName(name);

                //Dodavanje u mapu
                if (products.containsKey(name)) {
                    products.get(name).add(current);
                } else {
                    ArrayList<Product> productList = new ArrayList<>();
                    productList.add(current);
                    products.put(current.getName(), productList);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return products;
    }

    public boolean generateScript(ArrayList<Product> products) {
        System.out.println("Generating script");
        try {
            StringBuilder query = new StringBuilder("TRUNCATE TABLE `product`;INSERT INTO `product` "
                    + "(`product_shop`,`product_name`,`product_description`,`product_manufacturer`,"
                    + "`product_price`,`product_barcode`,`product_image`,`product_statistical_price`,`product_timestamp`) VALUES");

            for (int j = 0; j < products.size(); j++) {
                Product p = products.get(j);

                int shopId = (p.getShop().toLowerCase().equals("maxi") ? 1 : 2);

                String name = "";
                String description = "";
                String manufacturer = "";
                double price = p.getPrice();
                String barcode = "";
                String img = "";
                String statistcalPrice = "";

                if (p.getName() != null) {
                    name = p.getName().replace("\"", "");
                }
                if (p.getDescription() != null) {
                    description = p.getDescription().replace("\"", "");
                }
                if (p.getManufacturer() != null) {
                    manufacturer = p.getManufacturer().replace("\"", "");
                }
                if (p.getBarcode() != null) {
                    barcode = p.getBarcode().replace("\"", "");
                }
                if (p.getImage() != null) {
                    img = p.getImage().replace("\"", "");
                }
                if (p.getStatisticalPrice() != null) {
                    statistcalPrice = p.getStatisticalPrice().replace("\"", "");
                }

                if (j == products.size() - 1) {
                    query.append(" ( " + shopId + ",\"" + name + "\",\"" + description + "\",\"" + manufacturer + "\",\"" + price + "\",\"" + barcode + "\",\"" + img + "\",\"" + statistcalPrice + "\",NOW())");
                } else {
                    query.append(" ( " + shopId + ",\"" + name + "\",\"" + description + "\",\"" + manufacturer + "\",\"" + price + "\",\"" + barcode + "\",\"" + img + "\",\"" + statistcalPrice + "\",NOW()),");
                }

            }

            // do stuff
            PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream("bulk.sql"), StandardCharsets.UTF_8), true);
            out.println(query.toString());
            out.close();
            System.out.println("Done!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }

    public boolean executeScript() {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("cmd /c start /min \"\" update.bat");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checkIfUpdated();
    }

    public boolean checkIfUpdated() {
        String testQuery = "SELECT `product`.`product_id` FROM `product` WHERE `product`.`product_id` = 1";
        try {
            PreparedStatement ps = (PreparedStatement) connection.prepareStatement(testQuery);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Baza podataka je azurirana.");
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Doslo je do greske, baza podataka nije azurirana.");
        return false;
    }

    public String lastUpdate() {
        String date = "";
        String query = "SELECT `product`.`product_timestamp` FROM `product`";
        try {
            PreparedStatement ps = (PreparedStatement) connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                date = rs.getString("product_timestamp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static DB getInstance() {
        if (instance == null) {
            instance = new DB();
        }
        return instance;
    }

    private DB() {
        try {
            connection = (Connection) DriverManager.getConnection(url, user, pass);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnnection() throws SQLException {
        connection.close();
    }
}
