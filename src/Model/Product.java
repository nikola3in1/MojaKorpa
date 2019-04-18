package Model;

public class Product implements Comparable<Product> {

    //From DB - crawled data
    private int id;
    private String shop;
    private String name;
    private String description;
    private String manufacturer;
    private Double price;
    private String barcode;
    private String image;
    private String statisticalPrice;

    //Other util data
    private int quantity;
    private int cena;

    public Product(Product copy) {
        this.shop = copy.shop;
        this.name = copy.name;
        this.description = copy.description;
        this.manufacturer = copy.manufacturer;
        this.price = copy.price;
        this.barcode = copy.barcode;
        this.image = copy.image;
        this.statisticalPrice = copy.statisticalPrice;
        this.quantity = copy.quantity;
    }

    public Product(String shop, String name, String description, String manufacturer, double price, String barcode, String image, String statisticalPrice) {
        if (shop.equals("1")) {
            this.shop = "Maxi";
        } else if (shop.equals("2")) {
            this.shop = "Idea";
        } else {
            this.shop = shop;
        }
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.price = price;
        this.barcode = barcode;
        this.image = image;
        this.statisticalPrice = statisticalPrice;
    }

    public String getStatisticalPrice() {
        return statisticalPrice;
    }

    public void setStatisticalPrice(String statisticalPrice) {
        this.statisticalPrice = statisticalPrice;
    }

    public Product() {
        this.price = Double.MAX_VALUE;
    }

    public Product(int id, String shop, String name, String description, String manufacturer, double price, String barcode, String image, String statisticalPrice) {
        this.id = id;
        if (shop.equals("1")) {
            this.shop = "Maxi";
        } else if (shop.equals("2")) {
            this.shop = "Idea";
        } else {
            this.shop = shop;
        }
        this.name = name;
        this.description = description;
        this.manufacturer = manufacturer;
        this.price = price;
        this.barcode = barcode;
        this.image = image;
        this.statisticalPrice = statisticalPrice;
    }

    public int getCena() {
        return cena;
    }

    public void setCena(int cena) {
        this.cena = cena;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {

        this.image = image;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", shop='" + shop + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", price=" + price +
                ", barcode='" + barcode + '\'' +
                ", image='" + image + '\'' +
                ", statisticalPrice='" + statisticalPrice + '\'' +
                '}';
    }

    @Override
    public int compareTo(Product p) {
        return this.getName().compareTo(p.getName());
    }
}
