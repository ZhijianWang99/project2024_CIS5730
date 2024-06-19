Project Structure Updates:
Split files in org folder to respective source (src) and test (tst) folders. Build target can be found in out/. 

Required tasks: (Lei Sun)

Task 2.1: A map for cache is implemented to enable the cache for contributor data storage in the DataManager class, and all the related modifications are labeled with "//Task 2.1"

Task 2.2: In the attemptLogin, getContributorName and createFund methods of the DataManager class, codes are supplemented to throw exceptions when encountering null and they are all labeled with "//Task 2.2" 
          In addition, in UserInterface.main method, 2 while loops labeled with "// Task 2.2: Retry operation" have been added to enable the retry block for users (one for attempLogin, the other for createFund)

Task 2.3: In UserInterface class, a map aggregatedDonationsCache is defined, a private static class AggregatedDonation is created, a private method aggregateDonationsByContributor is defined, and the displayFund method is modified to show the aggregated donation information. All these modifications are labeled with "// Task 2.3"  

*In addition, another DataManager bug has been spotted with the help of Prof. Murphy and Grace, which is in line 132 of DataManager: "_id" -> "id"


Additional Tasks:

Task 2.7 (Steven Shi) : A deleteFund method with defensive programming is implemented in DataManager class and some modifications have been made in the UserInterface class too (all related code lines are labeled with "// Task 2.7")
          In addition, tests for deleteFund are created in DataManagerRobustnessTest and DataManagerTest

Task 2.8 (Steven Shi) : UserInterface class has been modified to enable logout and re-login without restarting the app, and all the related modifications are labeled with "// Task 2.8"
        Fixed small bug in attemptLogin throwing exceptions upon unsuccessful logins

Task 2.9 (Zhijian Wang) : In UserInterface class, a listContributions method is implemented to enable showing all the contributions and some other modifications are also made (e.g. in start method), and all the related modifications are labeled with "// Task 2.9"



