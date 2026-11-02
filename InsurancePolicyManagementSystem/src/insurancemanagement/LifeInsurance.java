package insurancemanagement;

import java.io.BufferedWriter;
import java.io.IOException;

public class LifeInsurance extends Insurance {

    private int age;
    private boolean isSmoker;

    private static double maturityBonusRate = 4;

    // Default constructor
    public LifeInsurance() {
        super();
        this.age = 18;
        this.isSmoker = false;
    }

    // Parameterized constructor
    public LifeInsurance(String policyNo, String holderName,
                         double sumAssured, int age,
                         boolean isSmoker)
            throws InvalidSumAssuredException,
                   InvalidAgeException {

    	super(policyNo, holderName,
    		      validateLifeInputs(sumAssured, age), 0);

        setAge(age);
        setSmoker(isSmoker);}
    
    private static double validateLifeInputs(
            double sumAssured, int age)
                throws InvalidSumAssuredException, InvalidAgeException {

            if (!Double.isFinite(sumAssured) || sumAssured <= 0) {
                throw new InvalidSumAssuredException(
                        "Sum assured must be greater than zero.",
                        sumAssured);
            }

            if (age < 18 || age > 80) {
                throw new InvalidAgeException(
                        "Age must be between 18 and 80.");
            }

            return sumAssured;
        
    }

    
	

	// Getters
    public int getAge() {
        return age;
    }

    public boolean isSmoker() {
        return isSmoker;
    }

    public static double getMaturityBonusRate() {
        return maturityBonusRate;
    }

    // Setters
    public void setAge(int age) throws InvalidAgeException {

        if (age < 18 || age > 80) {
            throw new InvalidAgeException(
                    "Age must be between 18 and 80.");
        }

        this.age = age;
    }

    public void setSmoker(boolean isSmoker) {
        this.isSmoker = isSmoker;
    }

    // Overridden premium calculation
    @Override
    public double calculatePremium() {

        double p = super.calculatePremium();

        double loading = 0;

        if (age > 45) {
            loading += p * 20 / 100;
        }

        if (isSmoker) {
            loading += p * 15 / 100;
        }

        double finalPremium = p + loading;

        setPremium(finalPremium);

        return finalPremium;
    }

    // Calculate maturity amount
    public double calculateMaturityAmount(int years) {

        if (years <= 0) {
            throw new InvalidTermException(
                    "Maturity years must be greater than zero.");
        }

        double sumAssured = getSumAssured();

        return sumAssured
                + (sumAssured * maturityBonusRate / 100 * years);
    }

    // Overridden display method
    @Override
    public void display(BufferedWriter bw) throws IOException {

        super.display(bw);

        bw.write("Age: " + age);
        bw.newLine();

        bw.write("Smoker: " + isSmoker);
        bw.newLine();

        bw.newLine();
    }

    // Find highest-premium policy
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
}