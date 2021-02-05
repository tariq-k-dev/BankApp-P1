package bankapp.daos;

import java.math.BigDecimal;

public interface AccountsCRUD {
	public void accountSignup(int customerID, BigDecimal amnt, String acctType);
	public void getAcctSignup();
	public boolean acctStatus(int customerID);
	public boolean verifyAccountNum(int customerID, int acctID);
	public BigDecimal getBalance(int customerID, int accountID);
	public void getAccountInfo(int customerID, int accountID);
	public void makeDeposit(int customerID, int acctID, BigDecimal amount);
	public void makeWithdrawal(int customerID, int acctID, BigDecimal amount);
	public void transferFunds(int custID, int acctID, int transferTO, BigDecimal amnt);
}
