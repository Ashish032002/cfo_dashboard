import java.util.Scanner;

public class BankCLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Print the pattern line using a loop
        for (int i = 0; i < 30; i++) {
            System.out.print("$");
        }
        System.out.print(" Welcome to CAMS Bank ");
        for (int i = 0; i < 30; i++) {
            System.out.print("$");
        }
        System.out.println("\n");

        // Display menu
        System.out.println("1) Login");
        System.out.println("2) Register");
        System.out.println("3) Exit");
        System.out.print("\nEnter your choice [1-3]: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice == 1) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            System.out.println("\nLogin Successful and display dashboard\n");

            boolean loggedIn = true;
            while (loggedIn) {
                System.out.println("1) View Accounts");
                System.out.println("2) Add Account");
                System.out.println("3) Edit Account");
                System.out.println("4) Deposit");
                System.out.println("5) Withdraw");
                System.out.println("6) Logout");

                System.out.print("\nEnter your choice [1-6]: ");
                int menuChoice = scanner.nextInt();
                scanner.nextLine();

                switch (menuChoice) {
                    case 1: System.out.println("\nAccounts List\n"); break;
                    case 2: System.out.println("\nAdd New Account\n"); break;
                    case 3: System.out.println("\nEdit Account\n"); break;
                    case 4: System.out.println("\nDeposit\n"); break;
                    case 5: System.out.println("\nWithdraw\n"); break;
                    case 6:
                        System.out.println("\nThanks for using CAMS Bank.!!!");
                        loggedIn = false;
                        break;
                    default:
                        System.out.println("\nInvalid option. Try again.\n");
                }
            }
        } else if (choice == 2) {
            System.out.println("\nRegistration feature coming soon!");
        } else {
            System.out.println("\nExiting CAMS Bank. Goodbye!");
        }

        scanner.close();
    }
}
