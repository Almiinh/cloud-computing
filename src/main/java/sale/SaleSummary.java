package sale;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class computes statistics to summarize sales.
 */
public class SaleSummary {
    private final List<Sale> allSales = new ArrayList<>();
    private final Set<String> stores = new HashSet<>();
    private final Set<String> products = new HashSet<>();
    private DecimalFormat currencyFormat = new DecimalFormat("0.00");

    public Set<String> getStores() {
        return stores;
    }

    public Set<String> getProducts() {
        return products;
    }

    public void addSale(Sale sale) {
        allSales.add(sale);
        products.add(sale.product());
        stores.add(sale.store());
    }

    public String totalProfitByStore(String store) {
        return currencyFormat.format(allSales.stream()
                .filter(t -> t.store().equals(store))
                .mapToDouble(Sale::price)
                .sum());
    }

    public int totalQuantityByProduct(String product) {
        return allSales.stream()
                .filter(t -> t.product().equals(product))
                .mapToInt(Sale::quantity)
                .sum();
    }

    public String totalProfitByProduct(String product) {
        return currencyFormat.format(allSales.stream()
                .filter(t -> t.product().equals(product))
                .mapToDouble(t -> t.quantity() * t.profit())
                .sum());
    }

    public int totalSoldByProduct(String product) {
        return (int) allSales.stream()
                .filter(t -> t.product().equals(product))
                .count();
    }
}
