Project Structure Updates:  
Split files in org folder to respective source (src) and test (tst) folders. Build target can be found in out/. 

Tasks:

Task 3.1 (Lei Sun) : A new method createOrganization() is added to the DataManager class, a new method registerNewOrganization() is added to the UserInterface class, and a new function "app.use('/createOrg', (req, res)" is added to api.js. Other changes in the main() and start() method of the UserInterface class are all labeled with "// Task 3.1";
          *Note that in this task, after the user creates a new organization, the user still needs to login this organization with the newly-created credentials in order to access the organization data (e.g. funds, donations), and this feature aims to guarantee security for the App (which is common in many websites nowadays);
          All the test cases w.r.t. the createOrganization() method in the DataManager class are provided in DataManager_createOrganization_Test.java  

Task 3.2 (Lei Sun) : A new method changePassword() is added to the DataManager class, a new method changePassword() is added to the UserInterface class, and a new function "app.use('/changePassword', (req, res)" is added to api.js. Other changes in the main() and start() method of the UserInterface class are all labeled with "// Task 3.2";
          All the test cases w.r.t. the changePassword() method in the DataManager class are provided in DataManager_changePassword_Test.java  

Task 3.3 (Zhijian Wang) : A new method editAccountInfo() is added to the DataManager class, a new method editAccountInfo(cachedLogin) is added to the UserInterface class, and a new function "app.use('/editAccount', (req, res)" is added to api.js. Other changes in the main() and start() method of the UserInterface class are all labeled with "// Task 3.4";
All the test cases w.r.t. the editAccountInfo() method in the DataManager class are provided in DataManager_editAccountInfo_Test.java  

Task 3.4 (Steven Shi) : 
Made modifications to UserInterface (a new method createDonation() added) for donation addition functionality; 
Added New Method addDonation() to DataManager to handle request;  
Donations can now be made for a fund when viewing it in the Org app; 
Added new simple wrapper method refreshOrg() for DataManager, acting as a refresh mechanism; Forced recache upon new donations; 
Added a few simple setter and getter methods for class variables; 
Other changes in the main() and start() method of the UserInterface class are all labeled with "// Task 3.4"; 
All the test (defensive) cases w.r.t. the addDonation() method in the DataManager class are provided in DataManager_addDonation_Test.java, the test cases w.r.t. the refreshOrg() method in the DataManager class are provided in DataManager_refreshOrg_Test.java, and the test cases w.r.t. the setter and getter methods in the DataManager class are provided in DataManager_SetterGetter_Test.java 

