package sale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;

public class SaleHandler {

    static SaleSummary parseSales(String filePath) {
        SaleSummary summary = new SaleSummary();
        try (Reader in = new FileReader(filePath)) {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setDelimiter(';')
                    .setHeader("Date_Time", "Store", "Product", "Quantity", "Unit_Price", "Unit_Cost", "Unit_Profit", "Total_Price")
                    .setSkipHeaderRecord(true)
                    .build();
            Iterable<CSVRecord> records = csvFormat.parse(in);

            // Iterating over each sale in the CSV
            for (CSVRecord record : records) {
                String store = record.get("Store");
                String product = record.get("Product");
                int quantity = Integer.parseInt(record.get("Quantity"));
                float unitPrice = Float.parseFloat(record.get("Unit_Price"));
                float unitProfit = Float.parseFloat(record.get("Unit_Profit"));
                summary.addSale(new Sale(store, product, quantity, unitPrice, unitProfit));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return summary;
    }

    static void writeSummaryByProduct(SaleSummary summary, String outputFile) {
        try (FileWriter fileWriter = new FileWriter(outputFile)) {

            fileWriter.write("Store;\"Total Profit\";\n");
            for (String store : summary.getStores())
                fileWriter.write(store + ";" + summary.totalProfitByStore(store) + "$;\n");

            System.out.println("[Worker] Data per store successfully updated into " + Paths.get(outputFile).toUri());
        } catch (IOException e) {
            System.err.println("[Worker] An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    static void writeSummaryByStore(SaleSummary summary, String outputFile) {
        try (FileWriter fileWriter = new FileWriter(outputFile)) {

            fileWriter.write("Product;\"Total Profit\";\"Total Quantity\";\"Total Sold\"\n");
            for (String product : summary.getProducts())
                fileWriter.write(product + ";" + summary.totalProfitByProduct(product) + "$;"
                        + summary.totalQuantityByProduct(product) + "$;"
                        + "\"" + summary.totalSoldByProduct(product) + " units\";\n");

            System.out.println("[Worker] Data per product successfully updated into " + Paths.get(outputFile).toUri());
        } catch (IOException e) {
            System.err.println("[Worker] An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
}
