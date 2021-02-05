package bankapp.daos;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import bankapp.database.DatabaseConnection;

public class AccountsCRUDImpl implements AccountsCRUD {
//	private int accountId;
//	private BigDecimal balance;
	
	@Override
	public void accountSignup(int customerID, BigDecimal amnt, String acctType) {
		// Add user to accounts table
		try (Connection conn = DatabaseConnection.dbConnection()) {
			String sql = "INSERT INTO account_signup (customer_id, amount, account_type)"
					+ "VALUES (?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, customerID);
			stmt.setBigDecimal(2, amnt);
			stmt.setString(3,  acctType);
			stmt.executeUpdate();
			String confirmation = "\nYour request for a new account has been submitted.\n"
					+ "You will be notified of your account status shortly.\n"
					+ "Thank you for banking with Revature Bank!\n";
			System.out.println(confirmation);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getAcctSignup() {
		try (Connection conn = DatabaseConnection.dbConnection()) {
			String sql = "SELECT customer_id, amount::money::numeric::float8, account_type, date_of_app FROM account_signup ORDER BY date_of_app";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			
			if (!rs.isBeforeFirst()) {
				System.out.println("\n*** No new accounts requiring approval ***");
			} else {
				while (rs.next()) {
					int custID = rs.getInt("customer_id");
					BigDecimal amount = rs.getBigDecimal("amount");
					String acctType = rs.getString("account_type");
					Timestamp appDate = rs.getTimestamp("date_of_app");
					
					System.out.println("[ Customer ID: " + custID  + " | Amount: " + amount + " | Type: " + acctType + " | Application Date: " + appDate + " ]");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean acctStatus(int userID) {
		// Returns if account is active
		try (Connection conn = DatabaseConnection.dbConnection()) {
			String sql = "SELECT isActive FROM accounts WHERE acctID = ? AND isActive = true";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, userID);
			ResultSet rs = stmt.executeQuery();
			boolean isActive = rs.isBeforeFirst();
			
			return isActive;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public BigDecimal getBalance(int customerID, int accountID) {
		try (Connection conn = DatabaseConnection.dbConnection()) {
			BigDecimal balance = null;
			// int accountID = 0;
			String sql = "SELECT balance::money::numeric::float8 FROM accounts WHERE customer_id = ? AND account_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, customerID);
			stmt.setInt(2, accountID);
			ResultSet rs = stmt.executeQuery();
			boolean isRecord = rs.isBeforeFirst();
			
			if (isRecord) {
				while (rs.next()) {
					// accountID = rs.getInt("accountID");
					balance = rs.getBigDecimal("balance");
				}
			}
			
			return balance;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void getAccountInfo(int custID, int acctID) {
		try (Connection conn = DatabaseConnection.dbConnection()) {
			int accountID = 0;
			String firstName = "";
			String lastName = "";
			BigDecimal balance = null;
			String sql = "SELECT first_name, last_name, account_id, balance::money::numeric::float8 "
					+ "FROM customers AS c "
					+ "JOIN accounts AS a "
					+ "ON c.customer_id = a.customer_id "
					+ "WHERE c.customer_id = ? AND a.account_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			
			stmt.setInt(1, custID);
			stmt.setInt(2, acctID);
			ResultSet rs = stmt.executeQuery();
			boolean isRecord = rs.isBeforeFirst();
			
			if (isRecord) {
				while (rs.next()) {
					firstName = rs.getString("first_name");
					lastName = rs.getString("last_name");
					accountID = rs.getInt("account_id");
					balance = rs.getBigDecimal("balance");
					String details = "\n[ Customer Name: " + firstName + " " + lastName + " | "
							+ "User ID: " + custID + " | Account ID: " + accountID + " | Balance: $" + balance + " ]";
					System.out.println(details);
				}
			} else {
				System.out.println("** No account information was available for that account ***");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void makeDeposit(int customerID, int acctID, BigDecimal amount) {
		if (verifyAccountNum(customerID, acctID)) {
			try (Connection conn = DatabaseConnection.dbConnection()) {
				System.out.print("\nOriginal account details:");
				getAccountInfo(customerID, acctID);
				BigDecimal currBalance = getBalance(customerID, acctID);
				BigDecimal updatedBalance = amount.add(currBalance);
				String sql = "UPDATE accounts SET balance = ? WHERE account_id = ? AND customer_id = ?";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setBigDecimal(1, updatedBalance);
				stmt.setInt(2, acctID);
				stmt.setInt(3, customerID);
				stmt.executeUpdate();
				System.out.print("\nUpdated account details:");
				getAccountInfo(customerID, acctID);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("That account number could not be verified\nPlease try again");
		}
	}

	@Override
	public boolean verifyAccountNum(int userID, int acctID) {
		try (Connection conn = DatabaseConnection.dbConnection()) {
			String sql = "SELECT * FROM accounts WHERE customer_id = ? AND account_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, userID);
			stmt.setInt(2, acctID);
			ResultSet rs = stmt.executeQuery();
			boolean isRecord = rs.isBeforeFirst();
			
			return isRecord;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	

	public boolean verifyAccountNumOnly (int acctID) {
		try (Connection conn = DatabaseConnection.dbConnection()) {
			String sql = "SELECT * FROM accounts WHERE account_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, acctID);
			ResultSet rs = stmt.executeQuery();
			boolean isRecord = rs.isBeforeFirst();
			
			return isRecord;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void makeWithdrawal(int customerID, int acctID, BigDecimal amount) {
		try (Connection conn = DatabaseConnection.dbConnection()) {
			BigDecimal currBalance = getBalance(customerID, acctID);
			if (currBalance.compareTo(amount) >= 0) {
				System.out.print("\nOriginal account details:");
				getAccountInfo(customerID, acctID);
				BigDecimal newBalance = currBalance.subtract(amount);
				String sql = "UPDATE accounts SET balance = ? WHERE customer_id = ? AND account_id = ?";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setBigDecimal(1, newBalance);
				stmt.setInt(2, customerID);
				stmt.setInt(3, acctID);
				stmt.executeUpdate();
				System.out.print("\nUpdated account details:");
				getAccountInfo(customerID, acctID);
			} else {
				System.out.println("\n*** There is not enough money in your account to withdraw $" + amount + " ***");
				System.out.println("\nThe current balance of this account is $" + currBalance);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void transferFunds(int customerID, int accountID, int transferToID, BigDecimal amount) {
		AccountsCRUDImpl acct = new AccountsCRUDImpl();
		acct.getAccountInfo(customerID, accountID);
		
		try (Connection conn = DatabaseConnection.dbConnection()) {
			BigDecimal currBalance = getBalance(customerID, accountID);
	
			if (currBalance.compareTo(amount) >= 0) {
				System.out.print("\nOriginal account details:");
				getAccountInfo(customerID, accountID);
				String sql = "INSERT INTO transfers (customer_id, account_id, transfer_to_acct, amount) VALUES (?, ?, ?, ?) WHERE customer_id = ? AND account_id = ?";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setInt(1, customerID);
				stmt.setInt(2, accountID);
				stmt.setInt(3,  transferToID);
				stmt.setBigDecimal(3, amount);
				stmt.executeUpdate();
			}  else {
				System.out.println("\n*** There is not enough money in your account to transfer $" + amount + " ***");
				System.out.println("\nThe current balance of this account is $" + currBalance);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
