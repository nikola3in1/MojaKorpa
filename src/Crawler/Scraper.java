package Crawler;

import Model.Product;

import java.util.ArrayList;

public abstract class Scraper implements Scrapable {

    ArrayList<Product> products = new ArrayList<>();
    ArrayList<JSONProduct> productsRaw = new ArrayList<>();

    public ArrayList<Product> getProducts() {
        return products;
    }
    public ArrayList<JSONProduct> getProductsRaw() {
        return productsRaw;
    }

    @Override
    public void scrape() {

    }

    @Override
    public void format() {}

}
