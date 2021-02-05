package bankapp.system;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import bankapp.daos.AccountsCRUDImpl;
import bankapp.database.DatabaseConnection;

public class TransferFund {
	private AccountsCRUDImpl acct = new AccountsCRUDImpl();

	public void transferFunds(int custID, int acctID, BigDecimal amnt) {
		acct.getAccountInfo(custID, acctID);
		try (Connection conn = DatabaseConnection.dbConnection()) {
			 String sql = "";
			 PreparedStatement stmt = conn.prepareStatement(sql);
			 stmt.setInt(1, acctID);
			 stmt.executeUpdate();
		 } catch (SQLException e) {
			 e.printStackTrace();
		}
	}
}
