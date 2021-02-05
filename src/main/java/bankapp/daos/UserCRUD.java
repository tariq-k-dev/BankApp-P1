package bankapp.daos;

import bankapp.models.User;

public interface UserCRUD {
	public void getUserInput();
	public void addUserAccount(User user);
	public void addUserToAccountType(String email, String userType);
	public void customerAcctSignup(int userID);
}
