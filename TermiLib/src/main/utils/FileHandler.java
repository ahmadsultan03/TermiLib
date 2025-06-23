package main.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler {
    private static final String DATA_DIR = "data";

    public FileHandler() {
        createDataDirectory();
    }

    private void createDataDirectory() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (Exception e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }

    public String[] readFromFile(String filename) {
        String filepath = DATA_DIR + File.separator + filename;
        try {
            if (!Files.exists(Paths.get(filepath))) {
                return new String[0];
            }

            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            String[] lines = new String[10000]; // Maximum lines
            String line;
            int count = 0;

            while ((line = reader.readLine()) != null && count < lines.length) {
                lines[count] = line;
                count++;
            }
            reader.close();

            // Trim array to actual size
            String[] result = new String[count];
            System.arraycopy(lines, 0, result, 0, count);
            return result;

        } catch (Exception e) {
            System.err.println("Error reading file " + filename + ": " + e.getMessage());
            return new String[0];
        }
    }

    public void writeToFile(String filename, String[] lines) {
        String filepath = DATA_DIR + File.separator + filename;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
            for (String line : lines) {
                if (line != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            writer.close();
        } catch (Exception e) {
            System.err.println("Error writing to file " + filename + ": " + e.getMessage());
        }
    }
}
