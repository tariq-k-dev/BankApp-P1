package bankapp.daos;

public interface LoginDAO {
	public <T> void verifyUser(T accountType);
}
