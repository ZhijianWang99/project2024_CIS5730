Required tasks: (Lei Sun)

Task 1.1: Three test files are completed: DataManager_createFund_Test.java, DataManager_attemptLogin_Test.java, and DataManager_getContributorName_Test.java

Task 1.2: Two bugs in DataManager have been found: "descrption" -> "description" in line 43, and /findContributrNameById -> /findContributorNameById in line 96

Task 1.3: The start method (lines 34-56) in the UserInterface class has been modified to achieve the required task 

Task 1.4: The displayFund method (lines 100-110) in the UserInterface class has been modified to achieve the required task 


Additional Tasks:
Task 1.9 (Steven)
Changes to DataManager: throw IllegalStateException in try-catch block in attemptLogin method
Changes to UserInterface: Introduced try-catch block for exception thrown in attemptLogin from DataManager object.
Testing for implementation in DataManager can be found in DataManager_attemptLogin_Test.java file.
- Method: testCommunicationError()
Testing for implementation in UserInterface can be found in UserInterfaceTest.java file.


Task 1.10 (Steven)
Made changes to UserInterface. Implemented static method formatDate() to format dates given Donation object using basic
string manipulation. The formatted String is passed to the line displaying donation information in UserInterface.

Testing for the static method can be found in the UserInterfaceTest.java file.
- possible bug TODO: contributer name not sucessfully retrieved by API.

