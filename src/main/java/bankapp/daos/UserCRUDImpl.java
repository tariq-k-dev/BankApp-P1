package bankapp.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bankapp.database.DatabaseConnection;
import bankapp.datevalidator.DateValidator;
import bankapp.datevalidator.DateValidatorUsingLocalDate;
import bankapp.main.StartApp;
import bankapp.models.User;

public class UserCRUDImpl implements UserCRUD {
	Boolean isCustomer;
	Scanner sc = new Scanner(System.in);

	public Boolean getIsCustomer() {
		return isCustomer;
	}

	public void setIsCustomer(Boolean isCustomer) {
		this.isCustomer = isCustomer;
	}
	
	@Override
	public void getUserInput() {
		String firstName;
		String lastName;
		String dob;
		String email;
		String phone = null;
		String password;
		DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
		DateValidator validator = new DateValidatorUsingLocalDate(dateFormatter);

		// prompt user for first name
		System.out.println("Please enter your first name: ");
		while (!sc.hasNext()) {
	        System.out.println("Invalid input");
	        sc.nextLine();
	    }
		firstName = sc.next();
		
		// prompt user for last name
		System.out.println("Please enter your last name: ");
		while (!sc.hasNext()) {
	        System.out.println("Invalid input");
	        sc.nextLine();
	    }
		lastName = sc.next();
		
		// prompt user for date of birth
		System.out.println("Please enter your date of birth [year-month-day]: ");
		while (!sc.hasNext()) {
	        System.out.println("Invalid date input");
	        sc.nextLine();
	    }
		// Validate date of birth
		dob = sc.next();
		while (!validator.isValid(dob)) {
			System.out.println("Invalid date of birth");
			System.out.println("Please enter your date of birth: ");
			dob = sc.next();
		}
		
		// prompt user for email
		do {
			System.out.println("Please enter your email: ");
			while (!sc.hasNext()) {
		        System.out.println("Invalid input");
		        sc.nextLine();
		    }
			email = sc.next();
			if (emailUnavailable(email)) {
				System.out.println("\n*** That email address is already taken ***\nPlease try another one\n");
			}
		} while (emailUnavailable(email));
		
		// prompt user for optional phone number
		System.out.println("Enter an optional phone number or 'n' to skip: ");
		phone = sc.next();
		
		if (phone.equalsIgnoreCase("n")) {
			phone = null;
		} else {
			String patterns = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
				      + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$" 
				      + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";
			Pattern phoneNumPatterns = Pattern.compile(patterns);
		    Matcher matcher = phoneNumPatterns.matcher(phone);

			while (!matcher.matches() || phone.equalsIgnoreCase("n")) {
		        System.out.println("Valid phone number must be at least 10 digits or 'n' to skip: ");
				phone = sc.next();
				matcher = phoneNumPatterns.matcher(phone);
		    }
			
		}
		
		// Prompt for password
		System.out.println("Enter a password: ");
		password = sc.next();

		User newUser = new User(firstName, lastName, dob, email, phone, password);
		System.out.println(newUser.toString());
		addUserAccount(newUser);	
		StartApp.userOrCustomer();
	}

	@Override
	public void addUserAccount(User user) {
		try (Connection conn = DatabaseConnection.dbConnection()) {
			String sql = "INSERT INTO users ("
					+ "first_name,"
					+ "last_name,"
					+ "dob,"
					+ "email,"
					+ "phone,"
					+ "password) "
					+ "VALUES(?, ?, ?, ?, ?, crypt(?, gen_salt('bf')))";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, user.getFirstName());
			stmt.setString(2, user.getLastName());
			stmt.setDate(3, java.sql.Date.valueOf(user.getDob()));
			stmt.setString(4, user.getEmail());
			stmt.setString(5, user.getPhone());
			stmt.setString(6, user.getPassword());
			stmt.executeUpdate();
			addUserToAccountType(user.getEmail(), "user");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to signup for a new user account.  Please try again...");
		}
	}

	@Override
	public void addUserToAccountType(String email, String userType) {
		try (Connection conn = DatabaseConnection.dbConnection()) {
			String sql = "INSERT INTO user_types (email, user_type) VALUES (?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, email);
			stmt.setString(2, userType);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void customerAcctSignup(int id) {
		
		try (Connection conn = DatabaseConnection.dbConnection()) {
			
			String successMsg = "\nYou have successfully registered for a Customer Account\n"
					+ "You will notified shortly if your customer account is approved\n"
					+ "Thank you for using Revature Bank!";
			String sql = "UPDATE users SET customer_signup = true WHERE user_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.executeUpdate();
			System.out.println(successMsg);
			
		} catch (SQLException e) {
			System.out.println("Sorry unable to complete your request to signup for a customer account at this time.\nPlease try again.");
			//e.printStackTrace();
		}
	}
	
	private static boolean emailUnavailable (String email) {
		try (Connection conn = DatabaseConnection.dbConnection()) {
			
			String sql = "SELECT ut.email from user_types AS ut "
					+ "FULL OUTER JOIN users AS u ON u.email = ut.email "
					+ "FULL OUTER JOIN customers AS c ON c.email = ut.email "
					+ "WHERE ut.email = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();
			
			return rs.isBeforeFirst();
			
		} catch (SQLException e) {
			System.out.println("Sorry unable to complete your request to signup for a customer account at this time.\nPlease try again.");
			//e.printStackTrace();
		}
		
		return true;
	}
	
}
