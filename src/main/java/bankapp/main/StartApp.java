package bankapp.main;

import java.util.Scanner;

import bankapp.daos.UserCRUDImpl;
import bankapp.system.SystemLogin;

public class StartApp {
	private static Scanner sc = new Scanner(System.in);
	private static String response = "";
	static UserCRUDImpl user = new UserCRUDImpl();
	static SystemLogin sysLogin = new SystemLogin();

	public static void userOrCustomer() {
		while (!response.equals("q")) {
			String greeting = "\nPlease select from the following option by entering the option number:\n"
					+ "[1] Login\n"
					+ "[2] New User\n"
					+ "[q] Quit Program";
			System.out.println(greeting);
			response = sc.next();
	
			switch (response) {
				case "1":
					System.out.println("\nEnter your email: ");
					String email = sc.next();
					
					// check for no input
					if (sc.nextLine() != null) {
						System.out.println("Enter your password: ");
						String pwd = sc.next();
						if (sc.nextLine() != null) {
							user = new UserCRUDImpl();
							SystemLogin.registeredUserLogin(email, pwd);
						}
					}
					break;
				case "2":
					System.out.println("Would you like to sign up as new user [y/n]? ");
					String signup = sc.next();
					
					if (signup.equals("y")) {
						user = new UserCRUDImpl();
						user.getUserInput();
					}
					
					break;
				case "q":
					sc.close();
					System.out.println("\nGood Bye!");
					System.exit(0);
				default:
					break;
			}
		}
	}
}
