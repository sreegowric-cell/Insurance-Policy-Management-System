package insurancemanagement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        File inputFile = new File("input/policies.txt");
        File outputDir = new File("output");

        try {

            // Check input file
            if (!inputFile.exists() ||
                !inputFile.isFile() ||
                !inputFile.canRead()) {

                throw new FileNotFoundException(
                        "Input file is missing or cannot be read.");
            }

            if (inputFile.length() == 0) {
                throw new EmptyInputFileException(
                        "Input file is empty.");
            }

            // Create output folder
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                throw new IOException(
                        "Could not create output directory.");
            }

            File reportFile =
                    new File(outputDir, "premium_report.txt");

            File errorFile =
                    new File(outputDir, "errors.txt");

            try (
                BufferedReader br =
                    new BufferedReader(new FileReader(inputFile));

                BufferedWriter errorWriter =
                    new BufferedWriter(new FileWriter(errorFile, true))
            ) {

                errorWriter.write(
                        "Error Log - " + new java.util.Date());
                errorWriter.newLine();

                // Read record count
                String countLine = br.readLine();

                if (countLine == null ||
                    countLine.trim().isEmpty() ||
                    countLine.trim().equals("0")) {

                    throw new EmptyInputFileException(
                            "Record count is missing or zero.");
                }

                int n;

                try {
                    n = Integer.parseInt(countLine.trim());
                } catch (NumberFormatException ex) {
                    throw new InvalidRecordException(
                            "Invalid record count.", ex);
                }

                if (n <= 0) {
                    throw new EmptyInputFileException(
                            "Record count must be greater than zero.");
                }

                Insurance[] policies = new Insurance[n];
                int[] maturityYears = new int[n];

                int accepted = 0;
                int rejected = 0;
                int recordsRead = 0;

                String line;
                int lineNumber = 1;

                // Read all records
                while ((line = br.readLine()) != null) {

                    lineNumber++;

                    String originalRecord = line;

                    // Skip blank lines and comments
                    if (line.trim().isEmpty() ||
                        line.trim().startsWith("#")) {
                        continue;
                    }

                    try {

                        if (recordsRead >= n) {
                            throw new InvalidRecordException(
                                    "More records than declared count.");
                        }

                        String[] fields = line.split(",", -1);

                        for (int i = 0; i < fields.length; i++) {
                            fields[i] = fields[i].trim();
                        }

                        // General Insurance
                        if (fields[0].equalsIgnoreCase("G")) {

                            if (fields.length != 5) {
                                throw new InvalidRecordException(
                                        "General policy requires 5 fields.");
                            }

                            try {

                                String policyNo = fields[1];
                                String holder = fields[2];

                                double sumAssured =
                                        Double.parseDouble(fields[3]);

                                double discount =
                                        Double.parseDouble(fields[4]);

                                Insurance policy = new Insurance(
                                        policyNo, holder, sumAssured, 0);

                                double finalPremium =
                                        policy.calculatePremium(discount);

                                policy.setPremium(finalPremium);

                                policies[accepted] = policy;
                                accepted++;

                            } catch (
                                NumberFormatException |
                                ArrayIndexOutOfBoundsException ex
                            ) {
                                throw new InvalidRecordException(
                                        "Invalid general policy data.", ex);
                            }

                        }

                        // Life Insurance
                        else if (fields[0].equalsIgnoreCase("L")) {

                            if (fields.length != 7) {
                                throw new InvalidRecordException(
                                        "Life policy requires 7 fields.");
                            }

                            try {

                                String policyNo = fields[1];
                                String holder = fields[2];

                                double sumAssured =
                                        Double.parseDouble(fields[3]);

                                int age =
                                        Integer.parseInt(fields[4]);

                                if (!fields[5].equalsIgnoreCase("true") &&
                                    !fields[5].equalsIgnoreCase("false")) {

                                    throw new InvalidRecordException(
                                            "Smoker must be true or false.");
                                }

                                boolean smoker =
                                        Boolean.parseBoolean(fields[5]);

                                int years =
                                        Integer.parseInt(fields[6]);

                                LifeInsurance policy = new LifeInsurance(
                                        policyNo, holder, sumAssured,
                                        age, smoker);

                                // Validate maturity term
                                policy.calculateMaturityAmount(years);

                                // Dynamic method dispatch
                                Insurance insurance = policy;

                                insurance.calculatePremium();

                                policies[accepted] = insurance;
                                maturityYears[accepted] = years;

                                accepted++;

                            } catch (
                                NumberFormatException |
                                ArrayIndexOutOfBoundsException ex
                            ) {
                                throw new InvalidRecordException(
                                        "Invalid life policy data.", ex);
                            }
                        }

                        else {
                            throw new InvalidRecordException(
                                    "Unknown policy type.");
                        }

                    } catch (InvalidSumAssuredException ex) {

                        rejected++;
                        logError(errorWriter, lineNumber,
                                originalRecord, ex);

                    } catch (InvalidAgeException ex) {

                        rejected++;
                        logError(errorWriter, lineNumber,
                                originalRecord, ex);

                    } catch (InvalidTermException ex) {

                        rejected++;
                        logError(errorWriter, lineNumber,
                                originalRecord, ex);

                    } catch (InvalidDiscountException ex) {

                        rejected++;
                        logError(errorWriter, lineNumber,
                                originalRecord, ex);

                    } catch (InvalidRecordException ex) {

                        rejected++;
                        logError(errorWriter, lineNumber,
                                originalRecord, ex);

                    } finally {

                        // Update records-read counter
                        recordsRead++;
                    }
                }

                errorWriter.flush();

                // Save premiums before finding maximum
                double[] savedPremiums = new double[accepted];

                for (int i = 0; i < accepted; i++) {
                    savedPremiums[i] =
                            policies[i].getPremium();
                }

                String highestPolicy =
                        LifeInsurance.findMaxPremium(policies);

                // Restore original premiums
                for (int i = 0; i < accepted; i++) {
                    policies[i].setPremium(savedPremiums[i]);
                }

                // Generate premium report
                try (
                    BufferedWriter bw =
                        new BufferedWriter(
                            new FileWriter(reportFile))
                ) {

                    bw.write("INSURANCE POLICY PREMIUM REPORT");
                    bw.newLine();

                    bw.write("--------------------------------");
                    bw.newLine();
                    bw.newLine();

                    for (int i = 0; i < accepted; i++) {

                        Insurance policy = policies[i];

                        policy.display(bw);

                        if (policy instanceof LifeInsurance) {

                            LifeInsurance life =
                                    (LifeInsurance) policy;

                            double maturity =
                                    life.calculateMaturityAmount(
                                            maturityYears[i]);

                            bw.write(String.format(
                                    "Maturity Amount: %.2f",
                                    maturity));

                            bw.newLine();
                            bw.newLine();
                        }
                    }

                    // Compare first two accepted policies
                    if (accepted >= 2) {

                        String higher =
                                policies[0].comparePremium(
                                        policies[1]);

                        bw.write(
                                "Higher premium among first two: "
                                + higher);

                        bw.newLine();

                    } else {

                        bw.write(
                                "Not enough policies to compare.");

                        bw.newLine();
                    }

                    bw.write("Highest Premium Policy: "
                            + (highestPolicy == null
                            ? "None" : highestPolicy));

                    bw.newLine();

                    bw.write("Policy Count: "
                            + Insurance.getPolicyCount());

                    bw.newLine();

                    bw.write("Records read: " + recordsRead
                            + ", accepted: " + accepted
                            + ", rejected: " + rejected);

                    bw.newLine();
                }
            }

            System.out.println(
                    "Processing complete. See output folder.");

        } catch (FileNotFoundException ex) {

            System.out.println(
                    "Input file not found: " + ex.getMessage());

        } catch (EmptyInputFileException ex) {

            System.out.println(
                    "Empty input: " + ex.getMessage());

        } catch (InvalidRecordException ex) {

            System.out.println(
                    "Invalid input: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println(
                    "File error: " + ex.getMessage());
        }
    }

    // Log rejected records
    private static void logError(
            BufferedWriter writer,
            int lineNumber,
            String record,
            Exception ex) throws IOException {

        writer.write("Line " + lineNumber
                + " | Record: " + record
                + " | Exception: "
                + ex.getClass().getSimpleName()
                + " | Message: " + ex.getMessage());

        writer.newLine();
    }
}