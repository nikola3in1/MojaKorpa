package Crawler;

import java.util.Arrays;

public class MaxiProduct extends JSONProduct{
    //MAJOR..............
    String name;
    String description;
    String manufacturer;
    Price price;
    String[] eanCodes; //barkodovi
    Images[] images;
    //..................

    //OPTIONAL..........
    String code;
    //..................

    class Price {
        double value;
        String supplementaryPriceLabel1;

        @Override
        public String toString() {
            return "Price{" +
                    "value=" + value +
                    ", supplementaryPriceLabel1='" + supplementaryPriceLabel1 + '\'' +
                    '}';
        }
    }
    class Images {
        @Override
        public String toString() {
            return "Images{" +
                    "url='" + url + '\'' +
                    '}';
        }

        String url;
    }
    @Override
    public String toString() {
        return "MaxiProduct{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", price=" + price +
                ", images=" + Arrays.toString(images) +
                ", eanCodes=" + Arrays.toString(eanCodes) +
                '}';
    }
}
