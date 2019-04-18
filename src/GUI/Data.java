package GUI;

import Crawler.ScraperWorker;
import Model.DB;
import Model.Product;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Data {

    //Reference
    final DB db = DB.getInstance();

    //Cuvamo pronadjene proizvode radi brzeg racunanja cene
    Korpa korpaMaxi = new Korpa();
    Korpa korpaIdea = new Korpa();
    Korpa korpaMoja = new Korpa();

    //<Barcode <Shop, Product>>
    public HashMap<String, HashMap<String, Product>> mainMap = new HashMap<>();

    //K = barcode, V= proizvod
    Map<String, ArrayList<Product>> barcodeMapa = db.getProductsBarcode();
    //K = ImeProzivoda, V = proizvod
    Map<String, ArrayList<Product>> proizvodiMap = db.getProductsName();

    //Vraca jeftiniji proizvod
    Product findMin(Product maxi, Product idea) {
        if (maxi.getPrice() < idea.getPrice()) {
            return maxi;
        } else if (maxi.getPrice() == idea.getPrice()) {
            maxi.setShop("/");
            return maxi;
        } else {
            return idea;
        }
    }

    //Filtriranje podataka dobijenih iz baze
    ArrayList<String> filterData() {
        HashMap<String, HashMap<String, Product>> newMap = new HashMap<>();
        SortedSet<String> proizvodiGUI = new TreeSet<>();

        for (Map.Entry<String, ArrayList<Product>> entrySet : barcodeMapa.entrySet()) {
            HashMap<String, Product> shops = new HashMap<>();
            for (Product p : entrySet.getValue()) {
                shops.putIfAbsent(p.getShop(), p);
                newMap.put(entrySet.getKey(), shops);
            }
        }

        HashSet<String> added = new HashSet<>();

        for (Map.Entry<String, HashMap<String, Product>> entrySet : newMap.entrySet()) {
            HashMap<String, Product> products = entrySet.getValue();

            if (products.size() > 1) {
                Product temp = products.get("Maxi");
                proizvodiGUI.add(temp.getName());
                //Pamtimo koje producte dodajemo
                added.add(temp.getName());
                added.add(products.get("Idea").getName());
            } else {
                Product p = products.values().iterator().next();
                if (!added.contains(p.getName())) {
                    added.add(p.getName());
                    proizvodiGUI.add(p.getName());
                }
            }
        }
        mainMap = newMap;

        ArrayList<String> formatedData = new ArrayList<>();
        formatedData.addAll(proizvodiGUI);
        return formatedData;
    }

    //Vraca timestamp poslednjeg update-a
    String lastUpdate() {
        return db.lastUpdate();
    }

    //Pokrece proces azuriranja baze
    boolean azuriraj() {
        boolean status = ScraperWorker.getInstance().updateData();
        if (status) {
            System.out.println("STATUS AZURIRANJA: " + status);

            try {
                //Azuriraj podatke
                Thread.sleep(3000);
                barcodeMapa = db.getProductsBarcode();
                proizvodiMap = db.getProductsName();
                mainMap = new HashMap<>();
                korpaMoja = new Korpa();
                korpaIdea = new Korpa();
                korpaMaxi = new Korpa();

                return true;
            } catch (InterruptedException ex) {
                Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    private static Data ourInstance = new Data();

    public static Data getInstance() {
        return ourInstance;
    }

    private Data() {
    }
}
