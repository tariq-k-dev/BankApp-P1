package bankapp.daos;

import java.math.BigDecimal;

public interface CustomerCRUD {
	public void customerOptions(int id, String firstName, String lastName);
	public void depositFunds(int customerID, int acctID, BigDecimal amnt);
}
