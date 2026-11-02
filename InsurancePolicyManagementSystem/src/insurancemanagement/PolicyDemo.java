package insurancemanagement;
import java.io.*;
import java.util.Scanner;

	

	class InvalidPolicyException extends Exception 
	{
		private static final long serialVersionUID = 1L;
	
	    InvalidPolicyException(String msg) 
	    {
	    
	        super(msg);
	    }
}
	public class PolicyDemo 
	{



	static void validate(int age, double premium)
	        throws InvalidPolicyException 
	{
	        if (age < 18 || premium <= 0) 
	        {
	            throw new InvalidPolicyException("Invalid policy details!");
	        }
	    }

	    static void logError(String msg) {
	        try (FileWriter fw =
	                 new FileWriter("error.log", true)) {
	            fw.write(msg + "\n");
	        } catch (IOException e) {
	            System.out.println("Logging failed");
	        }
	    }

	    public static void main(String[] args) {
	        Scanner sc = new Scanner(System.in);

	        try {
	            System.out.print("Enter policy number: ");
	            String number = sc.nextLine();

	            System.out.print("Enter holder name: ");
	            String name = sc.nextLine();

	            System.out.print("Enter age: ");
	            int age = sc.nextInt();

	            System.out.print("Enter premium: ");
	            double premium = sc.nextDouble();

	            validate(age, premium);

	            try (FileWriter fw =
	                     new FileWriter("policies.txt", true)) {
	                fw.write("Policy Number: " + number + "\n");
	                fw.write("Holder Name: " + name + "\n");
	                fw.write("Age: " + age + "\n");
	                fw.write("Premium: Rs. " + premium + "\n");
	            }

	            System.out.println("Policy saved successfully!");

	            System.out.println("\nPolicy Details:");

	            try (BufferedReader br = new BufferedReader(
	                     new FileReader("policies.txt"))) 
	            {
	                String line;
	                while ((line = br.readLine()) != null) {
	                    System.out.println(line);
	                }
	            }

	        } catch (InvalidPolicyException e) {
	            System.out.println("Error: " + e.getMessage());
	            logError(e.getMessage());

	        } catch (IOException e) {
	            System.out.println("File error: " + e.getMessage());
	            logError(e.getMessage());

	        } finally {
	            sc.close();
	        }
	    }
	}
	


