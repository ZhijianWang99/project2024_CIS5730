import java.util.*;


public class UserInterface {


    private DataManager dataManager;
    private Organization org;
    private Scanner in = new Scanner(System.in);

    private String cachedLogin;
    // Task 2.3
    private Map<String, Map<String, AggregatedDonation>> aggregatedDonationsCache = new HashMap<>();

    public UserInterface(DataManager dataManager, Organization org) {
        this.dataManager = dataManager;
        this.org = org;
    }

    public boolean start() {
        Scanner scanner = new Scanner(System.in);
        boolean haveCredentials = org != null;
        boolean exitStatus;
        while (true) {
            if (haveCredentials) {
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
                    // Task 3.2: Option to change password
                    // Task 3.3
                    System.out.println("Enter 'c' to change the organization's password");
                    System.out.println("Enter 0 to create a new fund");
                    System.out.println("Enter 'l' to list all the contributions");
                    System.out.println("Enter 'e' to edit the organization name or description");
                    System.out.println("Enter 'logout' to logout");
                    System.out.println("Enter 'q' or 'quit' to exit.");


                    String inputString = in.nextLine();

                    if (inputString.equals("quit") || inputString.equals("q")) {
                        System.out.println("Good bye!");
                        return false;
                    }
                    if (inputString.equals("logout")) {
                        System.out.println("Logged out");
                        return true;
                    }
                    // Enter 'l' to list all the contributions
                    if (inputString.equals("l")) {
                        listContributions();
                    }
                    // Task 3.2: Enter 'c' to change password
                    else if (inputString.equals("c")) {
                        changePassword();
                    }
                    else if (inputString.equals("e")) {
                        editAccountInfo(cachedLogin);
                    } else {
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
                            System.out.println("Invalid input! Please enter a valid input, or 'q' or 'quit' to exit.");
                        }
                    }
                }
            } else {
                System.out.println("--------------------------------");
                System.out.println("Log in to Organization or Register a New Organization");
                System.out.println("Enter 'q' or 'quit' to exit, 'r' to register a new organization, and anything else to continue login");
                String respStr = scanner.nextLine();
                if (respStr.equals("quit") || respStr.equals("q")) {
                    System.out.println("Good bye!");
                    return false;
                }

                // Task 3.1: Register and create a new organization
                if (respStr.equals("r")) {
                    registerNewOrganization();
                } else {
                    System.out.println("Enter login:");
                    String login = scanner.nextLine();
                    System.out.println("Enter password:");
                    String password = scanner.nextLine();
                    try {
                        org = dataManager.attemptLogin(login, password);
                        if (org == null) {
                            System.out.println("Login failed. (Y/y) to Enter New Credentials, 'r' to Register, or anything else to exit");
                            String userRsp = scanner.nextLine().trim().toLowerCase();
                            if (userRsp.equals("r")) {
                                registerNewOrganization();
                            } else if (!userRsp.equals("y")) {
                                System.out.println("Goodbye!");
                                return false;
                            }
                        } else {
                            System.out.println("Login succeed.");
                            haveCredentials = true;
                        }
                    } catch (Exception e) {
                        System.out.println("Error message: " + e.getMessage());
                        System.out.println("Want to retry login? (Enter Y/y for Yes, or anything else for No)");
                        String userRsp = scanner.nextLine().trim().toLowerCase();
                        if (!userRsp.equals("y")) {
                            return false;
                        }
                    }
                }
            }
        }
    }


    public void createFund() {

        // Task 2.2: Retry operation for createFund
        while (true) {

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
                    System.out.println("Failed to create the fund since is is null!");
                }
                break;

            } catch (Exception e) {
                // Task 2.2: Prompt for user during retry
                System.out.println("Error message: " + e.getMessage());
                System.out.println("Want to retry creating fund? (Enter Y/y for Yes, or anything else for No)");
                String userRsp = scanner.nextLine().trim().toLowerCase();

                if (!userRsp.equals("y")) {
                    break;
                }
            }

        }

    }


    /*
     * Task 3.1
     * Method to register and create a new organization
     */
    private void registerNewOrganization() {
        Scanner scanner = new Scanner(System.in);

        String login;
        while (true) {
            System.out.println("Enter login name: ");
            login = scanner.nextLine().trim();
            if (!login.isEmpty()) {
                break;
            }
            System.out.println("Login name cannot be blank. Please enter again.");
        }

        String password;

        while (true) {
            System.out.println("Enter password: ");
            password = scanner.nextLine().trim();
            if (!password.isEmpty()) {
                break;
            }
            System.out.println("Password cannot be blank. Please enter again.");
        }

        String name;
        while (true) {
            System.out.println("Enter organization name: ");
            name = scanner.nextLine().trim();
            if (!name.isEmpty()) {
                break;
            }
            System.out.println("Organization name cannot be blank. Please enter again.");
        }

        String description;
        while (true) {
            System.out.println("Enter organization description: ");
            description = scanner.nextLine().trim();
            if (!description.isEmpty()) {
                break;
            }
            System.out.println("Organization description cannot be blank. Please enter again.");
        }

        try {
            Organization newOrg = dataManager.createOrganization(login, password, name, description);
            if (newOrg != null) {
                System.out.println("Organization created successfully. Please log in using the new credentials.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Want to retry registering? (Enter Y/y for Yes, or anything else for No)");
            String userRsp = scanner.nextLine().trim().toLowerCase();
            if (userRsp.equals("y")) {
                registerNewOrganization();
            }
        }
    }

    /*
     * Task 3.2
     * Method to change password
     */
    private void changePassword() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter current password:");
        String currentPassword = scanner.nextLine();

        System.out.println("Enter new password:");
        String newPassword = scanner.nextLine();

        System.out.println("Re-enter new password:");
        String newPasswordConfirm = scanner.nextLine();

        if (!newPassword.equals(newPasswordConfirm)) {
            System.out.println("New passwords do not match! Please enter 'c' to try again.");
            return;
        }

        try {
            boolean success = dataManager.changePassword(org.getId(), currentPassword, newPassword);
            if (success) {
                System.out.println("Password changed successfully.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.out.println("Password change failed!  Please enter 'c' to try again.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Password change failed!  Please enter 'c' to try again.");
        }
    }

    /*
     * Task 3.2
     * Method to change password
     */
    private void editAccountInfo(String login) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your password:");
        String password = scanner.nextLine();
        try {
            Organization tempOrg = dataManager.attemptLogin(login, password);
            System.out.println(login);
            System.out.println(password);
            if (tempOrg == null) {
                System.out.println("Password is wrong, go back to the previous menu.");
            } else {
                String newName = null;
                System.out.println("Edit name? (Enter T/F)");
                String option = scanner.nextLine().toUpperCase();
                while (!option.equals("T") && !option.equals("F")) {
                    System.out.println("Edit name? (Enter T/F)");
                    option = scanner.nextLine().toUpperCase().strip();
                }
                if (option.equals("T")) {
                    System.out.println("Enter new name:");
                    newName = scanner.nextLine();
                }

                String newDescription = null;
                System.out.println("Edit Description? (Enter T/F)");
                option = scanner.nextLine().toUpperCase().strip();
                while (!option.equals("T") && !option.equals("F")) {
                    System.out.println("Edit Description? (Enter T/F)");
                    option = scanner.nextLine().toUpperCase();
                }
                if (option.equals("T")) {
                    System.out.println("Enter new name:");
                    newDescription = scanner.nextLine();
                }
                boolean success = dataManager.editAccountInfo(org.getId(), newName, newDescription);
                if (success){
                    System.out.println("Editing Information is successful");
                }
            }
        } catch (Exception e) {
            System.out.println("Edit Information failed");
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

        List<Map.Entry<String, AggregatedDonation>> sortedAggregatedDonations = new ArrayList<>(aggregatedDonations.entrySet());

        // Sort by total amount in descending order	
        sortedAggregatedDonations.sort((entry1, entry2) -> Long.compare(entry2.getValue().totalAmount, entry1.getValue().totalAmount));

        System.out.println("Aggregated donations:");
        for (Map.Entry<String, AggregatedDonation> entry : sortedAggregatedDonations) {
            AggregatedDonation aggDonation = entry.getValue();
            System.out.printf("* %s, %d donations, $%d total\n", entry.getKey(), aggDonation.count, aggDonation.totalAmount);
        }

        long totalDonationAmount = aggregatedDonations.values().stream().mapToLong(agg -> agg.totalAmount).sum();
        double percentageGot = (double) totalDonationAmount / fund.getTarget();
        percentageGot *= 100;
        System.out.printf("Total donation amount: $%d (%.2f%% of target)\n", totalDonationAmount, percentageGot);
        System.out.println("Press the Enter key to go back to the listing of funds");

        // Task 2.7: Mechanism to enable fund deletion
        System.out.println("To Delete the Fund: type 'delete' and Press Enter");
        String input = in.nextLine();
        if (Objects.equals(input, "delete")) {
            confirmDeleteFund(fund);
        }

    }

    // Task 2.7: Method to confirm whether to delete the fund
    public void confirmDeleteFund(Fund fund) {
        String input = "";
        int attempts = 0;
        // Allow for 3 attempts
        while (attempts <= 3) {
            System.out.println("Y: Confirm Deletion?");
            System.out.println("N: Cancel");
            input = in.nextLine().trim();

            if (input.equals("N") || input.equals("n")) {
                System.out.println("Canceled Deletion.");
                return;
            }

            if (input.equals("Y") || input.equals("y")) {
                System.out.println("Deleting Fund...");
                try {
                    this.dataManager.deleteFund(fund.getId());
                    org.deleteFund(fund);
                    aggregatedDonationsCache.remove(fund.getId());
                    System.out.println("Delete succeed.");
                } catch (Exception e) {
                    System.out.println("An unexpected error occurred: " + e.getMessage());
                }
                return;
            }

            System.out.println("Invalid Input, attempts remaining before cancel: " + (3 - attempts));
            attempts++;
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

    // Task 2.9:
    // Method to list all the contributions
    public void listContributions() {

        List<Donation> allDonations = new ArrayList<>();
        Map<String, String> Id2NameMap = new HashMap<>();
        for (Fund fund : org.getFunds()) {
            allDonations.addAll(fund.getDonations());
            for (Donation d : fund.getDonations()) {
                Id2NameMap.put(d.getFundId(), fund.getName());
            }
        }

        // Sort donations in descending order by date 
        allDonations.sort((d1, d2) -> d2.getDate().compareTo(d1.getDate()));

        System.out.println("List of all contributions:");
        for (Donation donation : allDonations) {
            String fundName = Id2NameMap.get(donation.getFundId());
            System.out.printf("Fund Name: %s, Contributor Name: %s, Donation Amount: $%d, Donation Date: %s\n", fundName, donation.getContributorName(), donation.getAmount(), donation.getDate());
        }

        System.out.println("Press the Enter key to go back to the listing of funds");
        in.nextLine();

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
        String login = args.length > 0 ? args[0] : "";
        String password = args.length > 1 ? args[1] : "";
        Scanner scanner = new Scanner(System.in);

        Organization org = null;

        boolean haveCredentials = (!login.isEmpty() && !password.isEmpty());
        boolean exitStatus;

        while (true) {
            if (haveCredentials) {
                try {
                    org = ds.attemptLogin(login, password);
                    if (org == null) {
                        // Task 3.1: Give the option to register a new organization with 'r'
                        System.out.println("Login failed. (Y/y) to Enter New Credentials, 'r' to Register New Organization, or anything else to exit");
                        String userRsp = scanner.nextLine().trim().toLowerCase();
                        if (userRsp.equals("r")) {
                            new UserInterface(ds, null).registerNewOrganization();
                        } else if (!userRsp.equals("y")) {
                            System.out.println("Goodbye!");
                            return;
                        }
                    } else {
                        System.out.println("Login succeed.");
                        UserInterface ui = new UserInterface(ds, org);
                        ui.cachedLogin = login;
                        exitStatus = ui.start();
                        if (!exitStatus) {
                            return;
                        }
                    }
                    haveCredentials = false;
                } catch (Exception e) {
                    System.out.println("Error message: " + e.getMessage());
                    System.out.println("Want to retry login? (Enter Y/y for Yes, or anything else for No)");
                    String userRsp = scanner.nextLine().trim().toLowerCase();
                    if (!userRsp.equals("y")) {
                        return;
                    }
                }
            } else {
                System.out.println("--------------------------------");
                System.out.println("Log in to Organization or Register a New Organization");
                // Task 3.1: Give the option to register a new organization with 'r'
                System.out.println("Enter 'q' or 'quit' to exit, 'r' to register a new organization, and anything else to continue login");
                String respStr = scanner.nextLine();
                if (respStr.equals("quit") || respStr.equals("q")) {
                    System.out.println("Good bye!");
                    return;
                }
                if (respStr.equals("r")) {
                    new UserInterface(ds, null).registerNewOrganization();
                } else {
                    System.out.println("Enter login:");
                    login = scanner.nextLine();
                    System.out.println("Enter password:");
                    password = scanner.nextLine();
                    haveCredentials = true;
                }
            }
        }
    }


    // Task 2.3: Define an AggregatedDonation class
    private static class AggregatedDonation {
        int count = 0;
        long totalAmount = 0;
    }

}
