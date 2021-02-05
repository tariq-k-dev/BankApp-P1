package bankapp.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import bankapp.daos.CustomerCRUDImpl;
import bankapp.daos.UserCRUDImpl;
import bankapp.database.DatabaseConnection;
import bankapp.employee.Employee;
import bankapp.main.StartApp;

public class SystemLogin {
	private static Scanner sc = new Scanner(System.in);

	public static void registeredUserLogin(String email, String pwd) {
		try (Connection conn = DatabaseConnection.dbConnection()) {
			String sql = "SELECT * FROM registered_master WHERE email = ? AND password = crypt(?, password)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, email);
			stmt.setString(2, pwd);
			ResultSet rs = stmt.executeQuery();
			int id = 0;
			String userType = null;
			String firstName = null;
			String lastName = null;
			boolean isRegistered = rs.isBeforeFirst();
			
			// if user exist
			if (!isRegistered) {
				System.out.println("\n*** No user was found matching those login credentials ***");
			} else if (isRegistered) {
				while (rs.next()) {
					id = rs.getInt(2);
					firstName = rs.getString(3);
					lastName = rs.getString(4);
					userType = rs.getString(6);
				}
				
				String resp = null;
				
				 if (userType.equals("customer")) {
					 CustomerCRUDImpl customer = new CustomerCRUDImpl();
					 customer.customerOptions(id, firstName, lastName);
				 } else if (userType.equals("user")) {
					UserCRUDImpl user = new UserCRUDImpl();
					// Welcome existing user
					System.out.println("\nWelcome back " + firstName + " " + lastName + "!");
					
					do {
						
						System.out.println("\nWould you like to sign up for an account [y/n]? ");
						resp = sc.next();
						
					} while (!resp.equalsIgnoreCase("y") && !resp.equalsIgnoreCase("n"));
					
					if (resp.equalsIgnoreCase("y")) {
						user.customerAcctSignup(id);
					} else {
						StartApp.userOrCustomer();
					}
				} else if (userType.equals("employee")) {
					Employee emp = new Employee();
					emp.welcomeEmployee(email);
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
