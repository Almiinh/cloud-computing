
package emse;
public class Transaction {
    /**
     * Represents a single transaction, capturing essential details for analysis from a sales CSV file.
     */
    private String product;
    private String country;
    private int price;

    public String getProduct() {
        return product;
    }

    public String getCountry() {
        return country;
    }

    public int getPrice() {
        return price;
    }

    public Transaction(String product, String country, int price) {
        this.product = product;
        this.country = country;
        this.price = price;
    }

    public void printDetails(){
        System.out.println("Product: " + this.product);
        System.out.println("Price: " + this.price);
        System.out.println("Country: " + this.country);
        System.out.println("----------------------------");
    }
}
