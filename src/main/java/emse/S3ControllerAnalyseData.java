package emse;

import java.io.*;
public class S3ControllerAnalyseData {

    public static final String csvDelimiter = ",";

    public static void analyzeCsvData(String csvFilePath) {

        System.out.println("Initiating analysis of CSV data for minimum, maximum, and sum calculations");

        int maximumValue = 0;
        int minimumValue = Integer.MAX_VALUE;
        int totalSum = 0;

        // Uncomment the following code block for CSV file processing
        /*
        try {
            File file = new File(csvFilePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            String[] tempValues;
            while ((line = bufferedReader.readLine()) != null) {
                tempValues = line.split(csvDelimiter);
                for (String tempValue : tempValues) {
                    int numericValue = Integer.parseInt(tempValue);
                    if (numericValue > maximumValue) {
                        maximumValue = numericValue;
                    }
                    if (numericValue < minimumValue) {
                        minimumValue = numericValue;
                    }
                    totalSum += numericValue;
                }
            }
            System.out.println("Maximum value: " + maximumValue);
            System.out.println("Minimum value: " + minimumValue);
            System.out.println("Total sum: " + totalSum);
            bufferedReader.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        */

        // Writing analysis results to a file
        try (FileWriter writer = new FileWriter("analysisResults.txt")) {
            writer.write("Data Analysis Results\n");
            writer.write("Maximum value: " + maximumValue + "\n");
            writer.write("Minimum value: " + minimumValue + "\n");
            writer.write("Total sum: " + totalSum + "\n");
            System.out.println("Data analysis results successfully written to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // File path of the CSV file to be analyzed
        S3ControllerAnalyseData.analyzeCsvData(args[0]);
    }
}
