package emse;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVParser {
    private static final String DEFAULT_CSV_FILE_PATH = "data.csv";

    public static void main(String[] args) throws IOException, CsvException {

        try (
                Reader fileReader = Files.newBufferedReader(Paths.get(args[0])); // Use provided file path
                CSVReader csvFileReader = new CSVReader(fileReader);
        ) {
            List<ArrayList<Integer>> salesDataByCountryAndProduct = new ArrayList<>();
            TransactionRecord transactionRecords = new TransactionAggregate();

            String[] record;
            csvFileReader.skip(1); // Skip the header row
            // Iterating over each record in the CSV

            while ((record = csvFileReader.readNext()) != null) {
                String productName = record[2];
                String productPrice = record[3];
                String countryName = record[8];
                // Creating a Transaction instance
                transactionRecords.addTransaction(new Transaction(productName, countryName, Integer.parseInt(productPrice)));
            }

            // Attempting to create a new file
            try {
                File outputFile = new File("data.txt");
                if (outputFile.createNewFile()) {
                    System.out.println("File created: " + outputFile.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred while creating the file.");
                e.printStackTrace();
            }

            // Writing analysis results into the file
            try (FileWriter fileWriter = new FileWriter("data.txt")) {
                String lineSeparator = System.getProperty("line.separator");
                for (String country : transactionRecords.getCountries()) {
                    fileWriter.write("Country: " + country + lineSeparator);
                    fileWriter.write("Average sales per product in " + country + ": " + transactionRecords.averageSalesPerCountryPerProduct(country) + "$" + lineSeparator);
                    for (String product : transactionRecords.getProducts()) {
                        fileWriter.write("Sales of " + product + ": " + transactionRecords.totalSalesByCountryAndProduct(country, product) + " units, TOTAL: " + transactionRecords.salesAmountByCountryAndProduct(country, product) + "$" + lineSeparator);
                    }
                    fileWriter.write("==========================" + lineSeparator);
                }
                System.out.println("Data successfully written to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred while writing to the file.");
                e.printStackTrace();
            }
        }
    }
}
