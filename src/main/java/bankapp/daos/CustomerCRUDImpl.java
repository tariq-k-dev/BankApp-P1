package bankapp.daos;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

import bankapp.database.DatabaseConnection;

public class CustomerCRUDImpl implements CustomerCRUD {
	Scanner sc = new Scanner(System.in);
	AccountsCRUDImpl accts = new AccountsCRUDImpl();
	int acctID;
	BigDecimal amnt;
	
	@Override
	public void customerOptions(int customerID, String firstName, String lastName) {
		String choice = "";
		
		// Welcome existing user/customer
		System.out.println("\nWelcome back " + firstName + " " + lastName + "!");
		do {
			String options = "\nPlease select from the following options by entering the option number:\n"
					+ "[1] New Account\n"
					+ "[2] Account Balance\n"
					+ "[3] Deposit Funds\n"
					+ "[4] Withdraw Funds\n"
//					+ "[5] Transfer Funds\n"
					+ "[q] Quit Program";
			System.out.println(options);
			choice = sc.next();
		
			switch (choice) {
				// open a new account
				case "1":
					System.out.println("\nEnter the amount to open you account with: ");
					try {
						amnt = sc.nextBigDecimal();
						
						if (amnt.compareTo(BigDecimal.ZERO) > 0) {
							accts.accountSignup(customerID, amnt, "checking");
						} else {
							System.out.println("\n*** Invalid amount - must be greater then 0 ***");
						}
						
					} catch (InputMismatchException e) {
						System.out.println("\n*** Invalid input type (must be an integer) ***");
		                sc.nextLine();
					}
					break;
				
				case "2": // get account balance
					System.out.println("\nEnter your account number to view your balance: ");
					try {
						int acctID = sc.nextInt();
	
						if (accts.verifyAccountNum(customerID, acctID)) {
							accts.getAccountInfo(customerID, acctID);
							System.out.println("\nPress <enter> to continue");
							// pause to let user read output
							try {
								System.in.read();
							} catch(Exception e){
								e.printStackTrace();
							}
						} else {
							System.out.println("\n*** Invalid account number ***");
						}
					} catch (InputMismatchException e) {
						System.out.println("\n*** Invalid input type (must be an integer) ***");
		                sc.nextLine();
					}
					break;
				
				case "3": // deposit funds
					System.out.println("\nEnter your account number: ");
					try {
						acctID = sc.nextInt();
						if (accts.verifyAccountNum(customerID, acctID)) {
							System.out.println("Enter the amount for your deposit: ");
							do {
								amnt = sc.nextBigDecimal();
							
								if (amnt.compareTo(BigDecimal.ZERO) <= 0) {
									System.out.println("\n*** Invalid amount for deposit! Must be greater then 0 ***");
									sc.nextLine();
									break;
								}
							} while (amnt.compareTo(BigDecimal.ZERO) <= 0);
							
							if (amnt.compareTo(BigDecimal.ZERO) > 0) {
								accts.makeDeposit(customerID, acctID, amnt);
							}
						} else {
							System.out.println("\n*** That account number wasn't valid ***");
						}
					} catch (InputMismatchException e) {
						System.out.println("\n*** Invalid input type ***");
		                sc.nextLine();
					}
					break;
				
				case "4": // withdraw funds
					System.out.println("\nEnter your account number: ");
					try {
						acctID = sc.nextInt();
						if (accts.verifyAccountNum(customerID, acctID)) {
							System.out.println("Enter the amount to withdraw: ");
							do {
								amnt = sc.nextBigDecimal();
							
								if (amnt.compareTo(BigDecimal.ZERO) <= 0) {
									System.out.println("\n*** Invalid amount to withdraw! Must be greater then 0 ***");
									sc.nextLine();
									break;
								}
							} while (amnt.compareTo(BigDecimal.ZERO) <= 0);
							
							if  (amnt.compareTo(BigDecimal.ZERO) > 0) {
								accts.makeWithdrawal(customerID, acctID, amnt);
							}
						} else {
							System.out.println("\n*** That account number wasn't valid ***");
						}
						
					} catch (InputMismatchException e) {
						System.out.println("\n*** Invalid input type ***");
		                sc.nextLine();
					}
					break;
					
				case "5": // transfer funds
					System.out.println("\nEnter your account number: ");
					try {
						acctID = sc.nextInt();
						if (accts.verifyAccountNum(customerID, acctID)) {
							System.out.println("Enter the amount to transfer: ");
							do {
								amnt = sc.nextBigDecimal();
							
								if (amnt.compareTo(BigDecimal.ZERO) <= 0) {
									System.out.println("\n*** Invalid amount to withdraw! Must be greater then 0 ***");
									sc.nextLine();
									break;
								}
							} while (amnt.compareTo(BigDecimal.ZERO) <= 0);
							
							System.out.println("Enter the account ID to transfer to: ");
							int transferToID = sc.nextInt();
							
							if  (amnt.compareTo(BigDecimal.ZERO) > 0) {
								accts.transferFunds(customerID, acctID, transferToID, amnt);
							}
														
							
						} else {
							System.out.println("\n*** That account number wasn't valid ***");
						}
						
					} catch (InputMismatchException e) {
						System.out.println("\n*** Invalid input type ***");
		                sc.nextLine();
					}
					break;
					
				case "q":
					System.out.println("\nGood Bye!");
					System.exit(0);
				default:
					break;
			}
		} while (!choice.equals("q"));
	}

	@Override
	public void depositFunds(int customerID, int acctID, BigDecimal amnt) {
		if (accts.verifyAccountNum(customerID, acctID)) {
			try (Connection conn = DatabaseConnection.dbConnection()) {
				String sql = "UPDATE accounts SET balance = ? WHERE account_id = ? AND customer_id = ?"
						+ "VALUES (?, ?)";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setBigDecimal(1, amnt);
				stmt.setInt(2, acctID);
				stmt.setInt(3, acctID);
				stmt.executeUpdate();
				String confirmation = "\nYour request for a new account has been submitted.\n"
						+ "You will be notified of your account status shortly.\n"
						+ "Thank you for banking with Revature Bank!\n";
				System.out.println(confirmation);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (!accts.acctStatus(customerID)) {
			System.out.println("Sorry that account is not currently approved.");
		}
	}

}
