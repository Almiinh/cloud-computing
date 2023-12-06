
package emse;
public class Transaction {
    /**
     * Represents a single transaction, capturing essential details for analysis from a sales CSV file.
     */
    private String item;
    private String region;
    private int amount;

    public String getItem() {
        return item;
    }

    public String getRegion() {
        return region;
    }

    public int getAmount() {
        return amount;
    }

    public Transaction(String item, String region, int amount) {
        this.item = item;
        this.region = region;
        this.amount = amount;
    }

    public void printDetails(){
        System.out.println("Item: " + this.item);
        System.out.println("Amount: " + this.amount);
        System.out.println("Region: " + this.region);
        System.out.println("----------------------------");
    }
}

```

TransactionAggregate
```
package emse;

import java.util.ArrayList;
import java.util.List;

/**
 * This class aggregates all transactions from a CSV file and keeps track of key details like countries and products.
 */
public class TransactionAggregate {
    private List<Transaction> allTransactions;
    private Set<String> uniqueCountries;
    private Set<String> uniqueProducts;

    public TransactionAggregate() {
        allTransactions = new ArrayList<>();
        uniqueCountries = new HashSet<>();
        uniqueProducts = new HashSet<>();
    }

    public List<Transaction> getAllTransactions() {
        return allTransactions;
    }

    public Set<String> getUniqueCountries() {
        return uniqueCountries;
    }

    public Set<String> getUniqueProducts() {
        return uniqueProducts;
    }

    public void recordTransaction(Transaction transaction) {
        String product = transaction.getProduct();
        String country = transaction.getCountry();

        allTransactions.add(transaction);

        uniqueProducts.add(product);
        uniqueCountries.add(country);
    }

    public int countSalesByCountryAndProduct(String country, String product) {
        return (int) allTransactions.stream()
                .filter(t -> t.getCountry().equals(country) && t.getProduct().equals(product))
                .count();
    }

    public int totalSalesByCountryAndProduct(String country, String product) {
        return allTransactions.stream()
                .filter(t -> t.getCountry().equals(country) && t.getProduct().equals(product))
                .mapToInt(Transaction::getPrice)
                .sum();
    }

    public int averageSalesPerCountry(String country) {
        int totalSales = allTransactions.stream()
                .filter(t -> t.getCountry().equals(country))
                .mapToInt(Transaction::getPrice)
                .sum();

        return totalSales / uniqueProducts.size();
    }
}
