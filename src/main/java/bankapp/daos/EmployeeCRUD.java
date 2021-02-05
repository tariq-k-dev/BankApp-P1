package bankapp.daos;

import java.math.BigDecimal;
import java.util.ArrayList;

public interface EmployeeCRUD {
	public ArrayList<String> getEmployeeInfo(String email);
	public void getCustomerSignup();
	public void approveCustomer(int id);
	public void approveAllCustomers();
	public void transferToChecking(int id, BigDecimal amount);
	public void removeFromUsers(int id);
}
