package sale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private final Set<String> stores = new HashSet<>();     // A set allows to only keep unique values
    private final Set<String> products = new HashSet<>();   // which is useful for filtering by set
    private final DecimalFormat currencyFormat = new DecimalFormat("0.00");

    private SaleSummary(){
    }

    /**
     * Parses the CSV file to build the summary
     *
     * @param filePath file to parse
     * @return a SaleSummary
     */
    public static SaleSummary parseSales(String filePath) {
        SaleSummary summary = new SaleSummary();
        try (Reader file = new FileReader(filePath)) {
            // Parse records from CSV
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                    .setDelimiter(';')
                    .setHeader("Date_Time", "Store", "Product", "Quantity", "Unit_Price", "Unit_Cost", "Unit_Profit", "Total_Price")
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(file);

            // Iterate over each record in CSV
            for (CSVRecord record : records) {
                String store = record.get("Store");
                String product = record.get("Product");
                int quantity = Integer.parseInt(record.get("Quantity"));
                float unitPrice = Float.parseFloat(record.get("Unit_Price"));
                float unitProfit = Float.parseFloat(record.get("Unit_Profit"));

                // Add record as sale
                summary.addSale(new Sale(store, product, quantity, unitPrice, unitProfit));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return summary;
    }

    private void addSale(Sale sale) {
        allSales.add(sale);
        products.add(sale.product());
        stores.add(sale.store());
    }

    public static void writeSummaryByStore(SaleSummary summary, String outputFile) {
        Path filepath = Path.of(outputFile);
        boolean appendMode = !Files.exists(filepath);

        try (FileWriter fileWriter = new FileWriter(outputFile, appendMode)) {
            // Write the header only at the first line
            if (!appendMode)
                fileWriter.write("Store;\"Total Profit\";\n");
            // Write the stores
            for (String store : summary.stores)
                fileWriter.write(store + ";" + summary.totalProfitByStore(store) + "$;\n");

            System.out.println("[Worker] Data per store successfully updated into " + filepath.toUri());
        } catch (IOException e) {
            System.err.println("[Worker] An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public static void writeSummaryByProduct(SaleSummary summary, String outputFile) {
        Path filepath = Path.of(outputFile);
        boolean appendMode = Files.exists(filepath);

        try (FileWriter fileWriter = new FileWriter(outputFile, appendMode)) {

            // Write the header only at the first line
            if (!appendMode)
                fileWriter.write("Product;\"Total Profit\";\"Total Quantity\";\"Total Sold\"\n");
            // Write the summary per product
            for (String product : summary.products)
                fileWriter.write(product + ";" + summary.totalProfitByProduct(product) + "$;"
                        + summary.totalQuantityByProduct(product) + "$;"
                        + "\"" + summary.totalSoldByProduct(product) + " units\";\n");

            System.out.println("[Worker] Data per product successfully updated into " + filepath.toUri());
        } catch (IOException e) {
            System.err.println("[Worker] An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    private String totalProfitByStore(String store) {
        return currencyFormat.format(allSales.stream()
                .filter(t -> t.store().equals(store))
                .mapToDouble(Sale::price)
                .sum());
    }

    private int totalQuantityByProduct(String product) {
        return allSales.stream()
                .filter(t -> t.product().equals(product))
                .mapToInt(Sale::quantity)
                .sum();
    }

    private String totalProfitByProduct(String product) {
        return currencyFormat.format(allSales.stream()
                .filter(t -> t.product().equals(product))
                .mapToDouble(t -> t.quantity() * t.profit())
                .sum());
    }

    private int totalSoldByProduct(String product) {
        return (int) allSales.stream()
                .filter(t -> t.product().equals(product))
                .count();
    }
}
