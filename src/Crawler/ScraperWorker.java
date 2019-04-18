package Crawler;

import Model.DB;
import Model.Product;

import java.util.ArrayList;

public class ScraperWorker {

    private DB db = DB.getInstance();
    private MaxiScraper maxi = MaxiScraper.getInstance();
    private IdeaScraper idea = IdeaScraper.getInstance();


    public boolean updateData(){
        Thread maxiThread = new Thread(() -> {
            maxi.scrape();
        });
        Thread ideaThread = new Thread(() -> {
            idea.scrape();
        });
        maxiThread.start();
        ideaThread.start();
        try {
            maxiThread.join();
            ideaThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        ArrayList<Product> merged = new ArrayList<>();
        System.out.println(maxi.products.get(0).getShop()+ " SHOP ID");
        merged.addAll(maxi.products);
        merged.addAll(idea.products);

        boolean status = (db.generateScript(merged) && db.executeScript());
        return status;
    }
    private static ScraperWorker ourInstance = new ScraperWorker();

    public static ScraperWorker getInstance() {
        return ourInstance;
    }

    private ScraperWorker() {
    }
}
