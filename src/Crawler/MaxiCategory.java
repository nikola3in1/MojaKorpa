package Crawler;

import java.util.Arrays;

public class MaxiCategory {
    String categoryCode;
    String categoryName;

    @Override
    public String toString() {
        return "MaxiCategory{" +
                "categoryCode='" + categoryCode + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", results=" + Arrays.toString(results) +
                '}';
    }

    //Proizvodi
    MaxiProduct[] results;
}
