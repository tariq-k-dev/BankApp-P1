package bankapp.employee;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import bankapp.daos.AccountsCRUDImpl;
import bankapp.daos.EmployeeCRUDImpl;

public class Employee {
	private static Scanner sc = new Scanner(System.in);
	private static EmployeeCRUDImpl emp = new EmployeeCRUDImpl();
	private static AccountsCRUDImpl acct = new AccountsCRUDImpl();
	private static String resp = null;
	
	public void welcomeEmployee (String email) {
		ArrayList<String> empInfo = emp.getEmployeeInfo(email);
		System.out.println("\nWelcome back to work " + empInfo.get(0) + " " + empInfo.get(1) + "!");
		String menu = "\nWhat would you like to work on? "
				+ "\n[1] New customer account sign up request"
				+ "\n[2] New banking accounts signups"
				+ "\n[3] Exit the system";
		
		do {
			System.out.println(menu);
			resp = sc.next();
			
			switch (resp) {
				case "1":
					emp.getCustomerSignup();
					do {
						System.out.println("\nEnter a user's ID to approve 'e' to exit to previous menu: ");
						resp = sc.next();
						if (!resp.equalsIgnoreCase("e")) {
							try {
								int id = Integer.parseInt(resp);
								emp.approveCustomer(id);
				
								break;
							} catch (Exception e) {
								System.out.println("\n*** Unable to process that accounts approval ***");
							}
							
						} else if (resp.equalsIgnoreCase("e")) {
							break;
						}
					} while (!resp.equalsIgnoreCase("e"));
					break;
				case "2":
					acct.getAcctSignup();
					do {
						System.out.println("\nEnter a user's ID to approve 'e' to exit to previous menu: ");
						resp = sc.next();
						if (resp.equalsIgnoreCase("e")) {
							break;
						}
						System.out.println("\nEnter the amount to approve 'e' to exit to previous menu: ");
						if (resp.equalsIgnoreCase("e")) {
							break;
						}
						
						try {
							BigDecimal amnt = sc.nextBigDecimal();
							
							while (amnt.compareTo(BigDecimal.ZERO) <= 0) {
								System.out.println("***\n Invalid amount ***");
								System.out.println("\nEnter the amount to approve 'e' to exit to previous menu: ");
								amnt = sc.nextBigDecimal();
							};
							
							if (!resp.equalsIgnoreCase("e")) {
								try {
									int id = Integer.parseInt(resp);
									emp.transferToChecking(id, amnt);
									break;
								} catch (Exception e) {
									System.out.println("\n*** Unable to process that accounts approval ***");
								}
								
							}
						} catch (InputMismatchException e) {
							System.out.println("\n*** Not valid input ***\n");
						}
					} while (!resp.equalsIgnoreCase("e"));
					break;
				case "3":
					System.out.println("Goody Bye!");
					sc.close();
					System.exit(0);
				default:
					continue;
			}
			
		} while (!resp.equals("3"));
	}
}
