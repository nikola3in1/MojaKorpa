package Crawler;
import Model.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class IdeaScraper extends Scraper{
    private static IdeaScraper ourInstance = new IdeaScraper();

    public static IdeaScraper getInstance() {
        return ourInstance;
    }

    private IdeaScraper() {
    }

    public void scrape(){
        System.out.println("Scraping Idea...");
        IdeaCategory[] categories = getCategories();

        for (IdeaCategory category : categories) {
            IdeaProduct[] productsArr = getProducts(category);
            productsRaw.addAll(Arrays.asList(productsArr));
        }
        System.out.println("Done!");
        format();
    }

    @Override
    public void format() {
        System.out.println("Formating Idea...");

        for (JSONProduct jsonRaw : productsRaw) {
            IdeaProduct pRaw = (IdeaProduct) jsonRaw;
            if (pRaw.barcodes == null) {
                continue;
            }
            for (String barcode : pRaw.barcodes) {
                String shop = "idea";
                String name = pRaw.name;
                String description = pRaw.description;
                String manufacturer = pRaw.manufacturer;
                double price = pRaw.price.amount / 100;
                String image = pRaw.image_path;
                String statistical_price = pRaw.statistical_price.replace("Din","rsd");
                Product product = new Product(shop, name, description, manufacturer, price, barcode, image, statistical_price);
                products.add(product);
            }
        }

        System.out.println("Done!");
    }

    IdeaCategory[] getCategories() {

        try {
            URL url = new URL("https://www.idea.rs/online/v2/categories");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            Gson gson = new GsonBuilder().create();
            IdeaCategory[] categories = gson.fromJson(response.toString(), IdeaCategory[].class);

            return categories;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    IdeaProduct[] getProducts(IdeaCategory category){
        //Preuzimas proizvode neke kategorije

        int perPage = 5000;
        try {

//            System.out.println("Fetching products of " + category.getName() + ":" + category.getId());
            URL url = new URL("https://www.idea.rs/online/v2/categories/" + category.getId() + "/products?per_page=" + perPage);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Gson gson = new GsonBuilder().create();
            IdeaProduct[] products = gson.fromJson(response.toString(),ParseHelper.class).getProducts();

            return products;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private final class ParseHelper {
        IdeaProduct[] products;

        public IdeaProduct[] getProducts() {
            return products;
        }
        public void setProducts(IdeaProduct[] products) {
            this.products = products;
        }
    }
}
