package insurancemanagement;

import java.io.BufferedWriter;
import java.io.IOException;

public class Insurance {

    private String policyNo;
    private String holderName;
    private double sumAssured;
    private double premium;

    private static String companyName = "ABC Life Insurance";
    private static double serviceTaxRate = 18;
    private static double premiumRatePercent = 2;
    private static int policyCount = 0;

    // Default constructor: increments counter exactly once
    public Insurance() {
        policyCount++;
        this.policyNo = "DEFAULT" + policyCount;
        this.holderName = "Unknown";
        this.sumAssured = 100000;
        this.premium = 0;
    }

 // Two-argument constructor
    public Insurance(String policyNo, String holderName)
            throws InvalidSumAssuredException {
        this(policyNo, holderName, 100000, 0);
    }


 // Four-argument constructor
    public Insurance(String policyNo, String holderName,
                     double sumAssured, double premium)
            throws InvalidSumAssuredException {

        this(validatePolicyNo(policyNo),
             validateHolderName(holderName),
             validateSumAssuredValue(sumAssured),
             validatePremiumValue(premium), true);
    }
    
 // Private constructor
    private Insurance(String policyNo, String holderName,
                      double sumAssured, double premium,
                      boolean initialize) {

        this();

        this.policyNo = policyNo;
        this.holderName = holderName;
        this.sumAssured = sumAssured;
        this.premium = premium;
    }

    // Validation helpers
    private static String validatePolicyNo(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Policy number cannot be empty.");
        }
        return value.trim();
    }

    private static String validateHolderName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Holder name cannot be empty.");
        }
        return value.trim();
    }

    private static double validateSumAssuredValue(double value)
            throws InvalidSumAssuredException {

        if (!Double.isFinite(value) || value <= 0) {
            throw new InvalidSumAssuredException(
                    "Sum assured must be greater than zero.", value);
        }
        return value;
    }

    private static double validatePremiumValue(double value) {
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException(
                    "Premium cannot be negative or invalid.");
        }
        return value;
    }

    // Getters
    public String getPolicyNo() {
        return policyNo;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getSumAssured() {
        return sumAssured;
    }

    public double getPremium() {
        return premium;
    }

    public static String getCompanyName() {
        return companyName;
    }

    public static double getServiceTaxRate() {
        return serviceTaxRate;
    }

    public static double getPremiumRatePercent() {
        return premiumRatePercent;
    }

    public static int getPolicyCount() {
        return policyCount;
    }

    // Validated setters
    public void setPolicyNo(String policyNo) {
        if (policyNo == null || policyNo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Policy number cannot be empty.");
        }
        this.policyNo = policyNo.trim();
    }

    public void setHolderName(String holderName) {
        if (holderName == null || holderName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Holder name cannot be empty.");
        }
        this.holderName = holderName.trim();
    }

    public void setSumAssured(double sumAssured)
            throws InvalidSumAssuredException {

        if (!Double.isFinite(sumAssured) || sumAssured <= 0) {
            throw new InvalidSumAssuredException(
                    "Sum assured must be greater than zero.",
                    sumAssured);
        }
        this.sumAssured = sumAssured;
    }

    public void setPremium(double premium) {
        if (!Double.isFinite(premium) || premium < 0) {
            throw new IllegalArgumentException(
                    "Premium cannot be negative or invalid.");
        }
        this.premium = premium;
    }

    // Static setters
    public static void setCompanyName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid company name.");
        }
        companyName = name.trim();
    }

    public static void setServiceTaxRate(double rate) {
        if (!Double.isFinite(rate) || rate < 0 || rate > 100) {
            throw new IllegalArgumentException("Invalid tax rate.");
        }
        serviceTaxRate = rate;
    }

    public static void setPremiumRatePercent(double rate) {
        if (!Double.isFinite(rate) || rate <= 0) {
            throw new IllegalArgumentException(
                    "Premium rate must be positive.");
        }
        premiumRatePercent = rate;
    }

    // Basic premium calculation
    public double calculatePremium() {
        double basePremium =
                sumAssured * premiumRatePercent / 100;

        premium = basePremium
                + (basePremium * serviceTaxRate / 100);

        return premium;
    }

    // Discounted premium calculation
    public double calculatePremium(double discountPercent) {

        if (!Double.isFinite(discountPercent)
                || discountPercent < 0
                || discountPercent > 100) {

            throw new InvalidDiscountException(
                    "Discount must be between 0 and 100.");
        }

        double basePremium =
                sumAssured * premiumRatePercent / 100;

        double discountedBase =
                basePremium * (1 - discountPercent / 100);

        return discountedBase
                + (discountedBase * serviceTaxRate / 100);
    }

    // Compare premiums
    public String comparePremium(Insurance other) {

        if (other == null) {
            throw new IllegalArgumentException(
                    "Cannot compare with null.");
        }

        if (this.premium >= other.premium) {
            return this.policyNo;
        }

        return other.policyNo;
    }

    // Find highest premium
    public static String findMaxPremium(Insurance[] policies) {

        if (policies == null) {
            return null;
        }

        Insurance maxPolicy = null;
        double maxPremium = -1;

        for (Insurance policy : policies) {

            if (policy != null) {
                double calculated = policy.calculatePremium();

                if (maxPolicy == null || calculated > maxPremium) {
                    maxPremium = calculated;
                    maxPolicy = policy;
                }
            }
        }

        return maxPolicy == null
                ? null : maxPolicy.getPolicyNo();
    }

    // Display details in report file
    public void display(BufferedWriter bw) throws IOException {

        bw.write("Company Name: " + companyName);
        bw.newLine();

        bw.write("Policy Number: " + policyNo);
        bw.newLine();

        bw.write("Holder Name: " + holderName);
        bw.newLine();

        bw.write(String.format(
                "Sum Assured: %.2f", sumAssured));
        bw.newLine();

        bw.write(String.format(
                "Premium: %.2f", premium));
        bw.newLine();

        bw.newLine();
    }
}