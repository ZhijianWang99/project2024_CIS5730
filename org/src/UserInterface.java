import java.util.*;


public class UserInterface {


    private DataManager dataManager;
    private Organization org;
    private Scanner in = new Scanner(System.in);
    
    // Task 2.3
    private Map<String, Map<String, AggregatedDonation>> aggregatedDonationsCache = new HashMap<>();

    public UserInterface(DataManager dataManager, Organization org) {
        this.dataManager = dataManager;
        this.org = org;
    }

    public boolean start() {

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
            // task 2.8
            System.out.println("Enter 'log' to logout") ;

            System.out.println("Enter 'q' or 'quit' to exit.");

            String inputString = in.nextLine();

            if (inputString.equals("quit") || inputString.equals("q")) {
                System.out.println("Good bye!");
                return false ;
            }
            if (inputString.equals("log")) {
                System.out.println("Logged out") ;
                return true ;
            }

            try {

                int option = Integer.parseInt(inputString);

                if (option == 0) {
                    createFund();
                } else if (option > 0 && option - 1 < org.getFunds().size()) {
                    displayFund(option);
                } else {
                    System.out.println("Invalid fund number! Please enter a valid fund number or 0 to create a new fund, or enter 'q' or 'quit' to exit. There are " + String.valueOf(org.getFunds().size()) + " funds now.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid fund number, 0 to create a new fund, or 'q' or 'quit' to exit.");
            }

        }

    }

    public void createFund() {
        Scanner scanner = new Scanner(System.in);

        // Getting the name with validation
        String fundName;
        while (true) {
            System.out.println("Enter fund name: ");
            fundName = scanner.nextLine().trim();
            if (!fundName.isEmpty()) {
                break;
            }
            System.out.println("Fund name cannot be blank. Please enter again.");
        }

        // Getting the description with validation
        String fundDescription;
        while (true) {
            System.out.println("Enter fund description: ");
            fundDescription = scanner.nextLine().trim();
            if (!fundDescription.isEmpty()) {
                break;
            }
            System.out.println("Fund description cannot be blank. Please enter again.");
        }

        // Getting the target with validation
        Long fundTarget;
        while (true) {
            System.out.println("Enter fund target: ");
            try {
                fundTarget = Long.parseLong(scanner.nextLine().trim());
                if (fundTarget < 0) {
                    System.out.println("Fund target cannot be negative. Please enter again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Fund target needs to be a integer number. Please enter again.");
            }
        }
        
        
        
        try {
            Fund newFund = dataManager.createFund(org.getId(), fundName, fundDescription, fundTarget);
            if (newFund != null) {
                org.addFund(newFund);
                System.out.println("Fund created successfully");
            } else {
                System.out.println("Failed to create fund.");
            }
        } catch (IllegalStateException e) { 
            System.out.println("Error in communicating with server: " + e.getMessage());
        } catch (Exception e) { 
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
        
    }


    public void displayFund(int fundNumber) {

        Fund fund = org.getFunds().get(fundNumber - 1);

        System.out.println("\n\n");
        System.out.println("Here is information about this fund:");
        System.out.println("Name: " + fund.getName());
        System.out.println("Description: " + fund.getDescription());
        System.out.println("Target: $" + fund.getTarget());
        
        // Task 2.3 codes
        List<Donation> donations = fund.getDonations();
        if (!aggregatedDonationsCache.containsKey(fund.getId())) {
            aggregatedDonationsCache.put(fund.getId(), aggregateDonationsByContributor(donations));
        }
        Map<String, AggregatedDonation> aggregatedDonations = aggregatedDonationsCache.get(fund.getId());

        System.out.println("Aggregated donations:");
        for (Map.Entry<String, AggregatedDonation> entry : aggregatedDonations.entrySet()) {
            AggregatedDonation aggDonation = entry.getValue();
            System.out.printf("* %s, %d donations, $%d total\n", entry.getKey(), aggDonation.count, aggDonation.totalAmount);
        }

        long totalDonationAmount = aggregatedDonations.values().stream().mapToLong(agg -> agg.totalAmount).sum();
        double percentageGot = (double) totalDonationAmount / fund.getTarget();
        percentageGot *= 100;
        System.out.printf("Total donation amount: $%d (%.2f%% of target)\n", totalDonationAmount, percentageGot);
        System.out.println("Press the Enter key to go back to the listing of funds");
        System.out.println("To Delete the Fund: type 'delete' and Press Enter") ;
        String input = in.nextLine();

        if (Objects.equals(input, "delete")) {
            confirmDeleteFund(fund) ;
        }
    }

    public void confirmDeleteFund(Fund fund) {
        String input = "" ;
        int attempts = 0 ;
        while (attempts <= 3) {
            System.out.println("Y: Confirm Deletion?") ;
            System.out.println("N: Cancel") ;
            input = in.nextLine().trim() ;

            if (input.equals("N") || input.equals("n")) {
                System.out.println("Canceled Deletion.") ;
                return ;
            }

            if (input.equals("Y") || input.equals("y")) {
                System.out.println("Deleting Fund...") ;
                try {
                    this.dataManager.deleteFund(fund.getId()) ;
                    org.deleteFund(fund) ;
                    aggregatedDonationsCache.remove(fund.getId()) ;
                } catch (Exception e) {
                    System.out.println("An unexpected error occurred: " + e.getMessage());
                }
                return ;
            }

            System.out.println("Invalid Input, attempts remaining before cancel: " + (3 - attempts)) ;
            attempts++ ;
        }
    }
    
    // Task 2.3: Method to aggregate the contributor donations
    private Map<String, AggregatedDonation> aggregateDonationsByContributor(List<Donation> donations) {
        Map<String, AggregatedDonation> donationMap = new HashMap<>();
        for (Donation donation : donations) {
            String contributorName = donation.getContributorName();
            if (!donationMap.containsKey(contributorName)) {
                donationMap.put(contributorName, new AggregatedDonation());
            }
            AggregatedDonation aggDonation = donationMap.get(contributorName);
            aggDonation.count++;
            aggDonation.totalAmount += donation.getAmount();
        }
        return donationMap;
    }

    // Task 1.10
    public static String formatDate(Donation donation) {
        if (donation == null || donation.getDate() == null) {
            return "--/--/----";
        }
        String[] dateArr = donation.getDate().split("T")[0].split("-");
        return String.format("%s/%s/%s", dateArr[1], dateArr[2], dateArr[0]);
    }

    public static void main(String[] args) {
        DataManager ds = new DataManager(new WebClient("localhost", 3001));
        String login = args[0];
        String password = args[1];
        System.out.println("Login and Password: "+login + " " + password);
        Organization org = null;
        Scanner scanner = new Scanner(System.in);
        boolean haveCredentials = true ;
        boolean exitStatus ; // exit status from either quit (false) or logout (true)

        while(true) { // loop provides login option
            if (haveCredentials)  {
                // Task 2.2: Retry operation
                while (true) {
                    try {
                        org = ds.attemptLogin(login, password);
                        break;
                    } catch (IllegalStateException e) {
                        System.out.println("Error message: " + e.getMessage());
                        System.out.println("Want to retry? (Enter Y/y for Yes, or anything else for No)");
                        String userRsp = scanner.nextLine().trim().toLowerCase();

                        if (!userRsp.equals("y")) {
                            return;
                        }

                    } catch (Exception e) {
                        System.out.println("Error unexpected: " + e.getMessage());
                        return;
                    }
                }
                if (org == null) {
                    System.out.println("Login failed. (Y/y) to Enter New Credentials, (N/n) to exit");
                    String userRsp = scanner.nextLine().trim().toLowerCase() ;
                    if (userRsp.equals("n")) {
                        System.out.println("Goodbye!") ;
                        return ;
                    }
                } else {
                    UserInterface ui = new UserInterface(ds, org);
                    exitStatus = ui.start();
                    if (!exitStatus) { // false => quit
                        return ;
                    }
                }
                haveCredentials = false ;
            } else {
                System.out.println("--------------------------------") ;
                System.out.println("Log in to Organization") ;
                System.out.println("Enter login:") ;
                login = scanner.nextLine().trim() ;
                System.out.println("Enter password:") ;
                password = scanner.nextLine().trim() ;
                haveCredentials = true ;
            }
        }
    }
    
    // Task 2.3: Define an AggregatedDonation class
    private static class AggregatedDonation {
        int count = 0;
        long totalAmount = 0;
    }

}
