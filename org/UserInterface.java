import java.util.List;
import java.util.Scanner;

public class UserInterface {
	
	
	private DataManager dataManager;
	private Organization org;
	private Scanner in = new Scanner(System.in);
	
	public UserInterface(DataManager dataManager, Organization org) {
		this.dataManager = dataManager;
		this.org = org;
	}
	
	public void start() {
				
		while (true) {
			System.out.println("\n\n");
			if (org.getFunds().size() > 0) {
				System.out.println("There are " + org.getFunds().size() + " funds in this organization:");
			
				int count = 1;
				for (Fund f : org.getFunds()) {
					
					System.out.println(count + ": " + f.getName());
					
					count++;
				}
				System.out.println("Enter the fund number to see more information.");
			}
			System.out.println("Enter 0 to create a new fund");
			
			System.out.println("Enter 'q' or 'quit' to exit.");
			
			String inputString = in.nextLine();

			if (inputString.equals("quit") || inputString.equals("q")) {
				System.out.println("Good bye!");
				break;
			}
			
			try {
				
				int option = Integer.parseInt(inputString);

				if (option == 0) {
					createFund();
				} else if (option>0 && option-1<org.getFunds().size() ) {
					displayFund(option);
				} else {
					System.out.println("Invalid fund number! Please enter a valid fund number or 0 to create a new fund, or enter 'q' or 'quit' to exit. There are " +String.valueOf(org.getFunds().size())+" funds now.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input! Please enter a valid fund number, 0 to create a new fund, or 'q' or 'quit' to exit.");
			}
			
//			int option = in.nextInt();
//			in.nextLine();
//			if (option == 0) {
//				createFund(); 
//			}
//			else {
//				displayFund(option);
//			}
			
		}			
			
	}
	
	public void createFund() {
		
		System.out.print("Enter the fund name: ");
		String name = in.nextLine().trim();
		
		System.out.print("Enter the fund description: ");
		String description = in.nextLine().trim();
		
		System.out.print("Enter the fund target: ");
		long target = in.nextInt();
		in.nextLine();

		Fund fund = dataManager.createFund(org.getId(), name, description, target);
		org.getFunds().add(fund);

		
	}
	
	
	public void displayFund(int fundNumber) {
		
		Fund fund = org.getFunds().get(fundNumber - 1);
		
		System.out.println("\n\n");
		System.out.println("Here is information about this fund:");
		System.out.println("Name: " + fund.getName());
		System.out.println("Description: " + fund.getDescription());
		System.out.println("Target: $" + fund.getTarget());
		
		List<Donation> donations = fund.getDonations();
		long totalDonationAmount = 0;
		System.out.println("Number of donations: " + donations.size());
		for (Donation donation : donations) {
			System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + formatDate(donation));
		}
		
		double percentageGot=(double)totalDonationAmount/fund.getTarget();
		percentageGot*=100;
        System.out.printf("Total donation amount: $%d (%.2f%% of target)\n", totalDonationAmount,percentageGot);

		System.out.println("Press the Enter key to go back to the listing of funds");
		in.nextLine();
		
		
		
	}

	// task 1.10
	public static String formatDate(Donation donation) {
		if (donation == null || donation.getDate() == null) {
			return "--/--/----" ;
		}
		String[] dateArr = donation.getDate().split("T")[0].split("-") ;
		return String.format("%s/%s/%s", dateArr[1], dateArr[2], dateArr[0]) ;
	}

	public void start(String[] args) {

	}
	
	public static void main(String[] args) {

		DataManager ds = new DataManager(new WebClient("localhost", 3001));

		String login = args[0];
		String password = args[1];
		System.out.println(login+" "+password);
		Organization org = null;

		try {
			org = ds.attemptLogin(login, password);
		} catch (Exception e) {
			if (e instanceof IllegalStateException) {
				System.out.println("Error in communicating with server") ;
			} else {
				e.printStackTrace();
			}
			return ;
		}

		if (org == null) {
			System.out.println("Login failed.");
		}
		else {

			UserInterface ui = new UserInterface(ds, org);

			ui.start();

		}
	}

}
