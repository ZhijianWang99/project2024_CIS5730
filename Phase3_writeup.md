Project Structure Updates:  
Split files in org folder to respective source (src) and test (tst) folders. Build target can be found in out/. 

Tasks:

Task 3.1 (Lei Sun) : A new method createOrganization() is added to the DataManager class, a new method registerNewOrganization() is added to the UserInterface class, and a new function "app.use('/createOrg', (req, res)" is added to api.js. Other changes in the main() and start() method of the UserInterface class are all labeled with "// Task 3.1";
          *Note that in this task, after the user creates a new organization, the user still needs to login this organization with the newly-created credentials in order to access the organization data (e.g. funds, donations), and this feature aims to guarantee security for the App (which is common in many websites nowadays);
          All the test cases w.r.t. the createOrganization() method in the DataManager class are provided in DataManager_createOrganization_Test.java

Task 3.2 (Lei Sun) : A new method changePassword() is added to the DataManager class, a new method changePassword() is added to the UserInterface class, and a new function "app.use('/changePassword', (req, res)" is added to api.js. Other changes in the main() and start() method of the UserInterface class are all labeled with "// Task 3.2";
          All the test cases w.r.t. the changePassword() method in the DataManager class are provided in DataManager_changePassword_Test.java


