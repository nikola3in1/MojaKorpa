package GUI;

import Model.Product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Korpa {
    //    Key=Barkod; Value=Product;
    private HashMap<String, Product> proizvodi = new HashMap<>();
    private int vrednost = 0;

    public int racunajKorpu() {
        double sum = 0.0;
        for (Product p : proizvodi.values()) {
            sum += p.getPrice() * p.getQuantity();
        }
        this.vrednost = (int) Math.ceil(sum);
        return (int) Math.ceil(sum);
    }

    //Dodaje u korpu
    public void dodajUKorpu(Product product) {
        System.out.println(product.getShop()+ " "+product.getPrice()+" "+product.getBarcode());
        if (this.proizvodi.containsKey(product.getBarcode())) {
            Product p = this.proizvodi.get(product.getBarcode());
            p.setQuantity(p.getQuantity() + 1);
            p.setCena((int)(p.getQuantity()*p.getPrice()));
        } else {
            product.setQuantity(1);
            this.proizvodi.put(product.getBarcode(), product);
        }
    }

    //Uklanja iz korpe
    public void ukloniIzKorpe(Product product) {
        if (this.proizvodi.containsKey(product.getBarcode()) && this.proizvodi.get(product.getBarcode()).getQuantity() > 1) {
            Product p = this.proizvodi.get(product.getBarcode());
            p.setQuantity(p.getQuantity() - 1);
        } else {
            this.proizvodi.remove(product.getBarcode());
        }
    }

    public int getSize() {
        return proizvodi.keySet().size();
    }

    public int getVrednost() {
        return vrednost;
    }

    public Collection<Product> getProizvodi(){
        Collection<Product> products = new ArrayList<>();

        for (Product p : proizvodi.values()) {
            for (int i = 0; i < p.getQuantity(); i++) {
                products.add(p);
            }
        }

        return products;
    }

}

