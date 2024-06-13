Project Structure Updates:
Split files in org folder to respective source (src) and test (tst) folders. Build target can be found in out/. 

Required tasks: (Lei Sun)

Task 2.1: lines 16-17, 122-126 and 145 are supplemented to enable the cache for contributor data storage

Task 2.2: In attemptLogin, getContributorName and createFund methods, codes are supplemented to throw exceptions when encountering null and they are all labeled with "//Task 2.2" 
          In addition, in UserInterface.main method, a while loop labeled with "// Task 2.2: Retry operation" is added to enable the retry block for users

Task 2.3: In UserInterface class, a map aggregatedDonationsCache is defined, a private static class AggregatedDonation is created, a private method aggregateDonationsByContributor is defined, and the displayFund method is modified to show the aggregated donation information. All these modifications are labeled with "//Task 2.3".  

*In addition, another DataManager bug has been spotted with the help of Prof. Murphy and Grace, which is in line 132 of DataManager: "_id" -> "id"


Additional Tasks:



