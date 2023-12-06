package emse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class aggregates all transactions from a CSV file and keeps track of key details like countries and products.
 */
public class TransactionAggregate {
    private List<Transaction> allTransactions;
    private Set<String> countries;
    private Set<String> products;

    public TransactionAggregate() {
        allTransactions = new ArrayList<>();
        countries = new HashSet<>();
        products = new HashSet<>();
    }

    public List<Transaction> getAllTransactions() {
        return allTransactions;
    }

    public Set<String> getCountries() {
        return countries;
    }

    public Set<String> getProducts() {
        return products;
    }

    public void addTransaction(Transaction transaction) {
        String product = transaction.getProduct();
        String country = transaction.getCountry();

        allTransactions.add(transaction);

        products.add(product);
        countries.add(country);
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

        return totalSales / products.size();
    }
}
