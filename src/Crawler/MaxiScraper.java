package Crawler;

import Model.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MaxiScraper extends Scraper {

    private static MaxiScraper ourInstance = new MaxiScraper();
    public static MaxiScraper getInstance() {
        return ourInstance;
    }

    private MaxiScraper() {}

    public void scrape() {
        System.out.println("Scraping Maxi...");
        ArrayList<MaxiCategory> categories = getCategories();
        for (MaxiCategory c : categories) {
            productsRaw.addAll(Arrays.asList(c.results));
        }
        System.out.println("Done!");
        format();
    }

    public void format() {
        System.out.println("Formating products...");

        for (JSONProduct jsonRaw : productsRaw) {

            MaxiProduct pRaw = (MaxiProduct) jsonRaw;
            if (pRaw.eanCodes == null) {
                continue;
            }
            for (String barcode : pRaw.eanCodes) {
                String shop = "maxi";
                String name = pRaw.name;
                String description = pRaw.description;
                String manufacturer = pRaw.manufacturer;
                double price = pRaw.price.value;
                String image = (pRaw.images != null ? pRaw.images[0].url : "");
                String statistical_price = pRaw.price.supplementaryPriceLabel1;
                Product product = new Product(shop, name, description, manufacturer, price, barcode, image, statistical_price);
                products.add(product);
            }
        }
        System.out.println("Done!");
    }

    ArrayList<MaxiCategory> getCategories() {
        ArrayList<MaxiCategory> categories = new ArrayList<>();
        for (int i = 1; ; i++) {
            //Formatiramo id
            String id = "";
            if (i < 10) {
                id = "0" + i;
            } else {
                id = "" + i;
            }
            MaxiCategory category = getCategory(id);
            if (category != null) {
                categories.add(category);
            } else {
                break;
            }
        }
        return categories;
    }

    MaxiCategory getCategory(String id) {
        try {
            int productsPerPage = 5000;

            URL url = new URL("https://www.maxi.rs/online/asd/c/" + id + "/getSearchPageData?pageSize=" + productsPerPage);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            Gson gson = new GsonBuilder().create();
            MaxiCategory category = gson.fromJson(response.toString(), MaxiCategory.class);
            return category;
        } catch (IOException e) {
            return null;
        }
    }
}
