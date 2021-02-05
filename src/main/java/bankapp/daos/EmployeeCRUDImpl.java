package bankapp.daos;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import bankapp.database.DatabaseConnection;

public class EmployeeCRUDImpl implements EmployeeCRUD {
	private String userEmail = null;

	@Override
	public ArrayList<String> getEmployeeInfo(String email) {
		ArrayList<String> empValues = new ArrayList<String>();
		
		try (Connection conn = DatabaseConnection.dbConnection()) {
			String sql = "SELECT * FROM employees WHERE email = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();
			String firstName = null;
			String lastName = null;
			
			while (rs.next()) {
				firstName = rs.getString("first_name");
				lastName = rs.getString("last_name");
			}
			
			empValues.add(firstName);
			empValues.add(lastName);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return (empValues);	
	}

	@Override
	public void getCustomerSignup() {
		try (Connection conn = DatabaseConnection.dbConnection()) {
			String sql = "SELECT * FROM users WHERE customer_signup = true ORDER BY created_at";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			System.out.println("\n*** Customer Accounts Ready for Approval ***");
			if (!rs.isBeforeFirst()) {
				System.out.println("There are currently no applications for customer accounts\n");
			} else {
				while (rs.next()) {
					int id  = rs.getInt("user_id");
					String firstName = rs.getString("first_name");
					String lastName = rs.getString("last_name");
					String email = rs.getString("email");
					String createdAt = rs.getString("created_at");
					userEmail = email;
					
					//Display values
					System.out.print("ID: " + id);
					System.out.print(" | First Name: " + firstName);
					System.out.print(" | Last Name: " + lastName);
					System.out.print(" | Email: " + email);
					System.out.println(" | Signup Date: " + createdAt + "\n");
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void approveCustomer(int id) {
		try (Connection conn = DatabaseConnection.dbConnection()) {
			String sql = "UPDATE users SET customer_signup = true WHERE user_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.executeUpdate();
			transferToCustomer(id);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void approveAllCustomers() {
		// TODO Auto-generated method stub
		
	}

	public void transferToCustomer(int id) {
		try (Connection conn = DatabaseConnection.dbConnection()) {
			
			String sql = "INSERT INTO customers (first_name, last_name, dob, email, phone, password) "
					+ "(SELECT first_name, last_name, dob, email, phone, password "
					+ "FROM users WHERE user_id = ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.executeUpdate();
			removeFromUsers(id);
			
		} catch (SQLException e) {
			System.out.println("*** This customer id already exists in the customers table ***");
		}	
	}

	@Override
	public void removeFromUsers(int id) {

		try (Connection conn = DatabaseConnection.dbConnection()) {
			
			String sql = "DELETE FROM users WHERE user_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.executeUpdate();
			updateUserTypes();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private void updateUserTypes() {
		try (Connection conn = DatabaseConnection.dbConnection()) {
			
			String sql = "UPDATE user_types SET user_type = 'customer' WHERE email = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userEmail);
			stmt.executeUpdate();
			getCustomerSignup();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void transferToChecking(int id, BigDecimal amount) {
try (Connection conn = DatabaseConnection.dbConnection()) {
			
			String sql = "INSERT INTO accounts (customer_id, balance) VALUES (?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.setBigDecimal(2, amount);
			stmt.executeUpdate();
			removeFromAcctSignup(id);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void removeFromAcctSignup(int id) {
		try (Connection conn = DatabaseConnection.dbConnection()) {
					
					String sql = "DELETE FROM account_signup WHERE customer_id = ?";
					PreparedStatement stmt = conn.prepareStatement(sql);
					stmt.setInt(1, id);
					stmt.executeUpdate();
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

}
