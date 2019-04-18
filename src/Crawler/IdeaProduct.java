package Crawler;

import java.util.Arrays;

public class IdeaProduct extends JSONProduct{
    //MAJOR.............
    String name;
    String description;
    String manufacturer;
    Price price;
    String[] barcodes;
    String image_path;
    String statistical_price;
    //..................

    //OPTIONAL..........
    String id;
    String code;
    //..................

    @Override
    public String toString() {
        return "IdeaProduct{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", image_path='" + image_path + '\'' +
                ", barcodes=" + Arrays.toString(barcodes) +
                ", price=" + price +
                ", statistical_price='" + statistical_price + '\'' +
                '}';
    }

    class Price {
        String formatted_price;
        double amount;

        @Override
        public String toString() {
            return "Price{" +
                    "formatted_price='" + formatted_price + '\'' +
                    ", amount=" + amount +
                    '}';
        }
    }

}
