import static org.junit.Assert.*;

import org.junit.Test;

public class DataManager_SetterGetter_Test {
	
	private DataManager dm = new DataManager(new WebClient("localhost", 3001));
	

    @Test
    public void testSetAndGetCurrentUser() {
        // Set the currentUser
        String expectedUser = "test1";
        dm.setCurrentUser(expectedUser);

        // Get the currentUser
        String actualUser = dm.getCurrentUser();

        // Assert the currentUser is as expected
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testSetAndGetCurrentPass() {
        // Set the currentPass
        String expectedPass = "test2";
        dm.setCurrentPass(expectedPass);

        // Get the currentPass
        String actualPass = dm.getCurrentPass();

        // Assert the currentPass is as expected
        assertEquals(expectedPass, actualPass);
    }

    @Test
    public void testSetAndGetCurrentUserWithNull() {

        // Assert the currentUser is null
        assertNull(dm.getCurrentUser());
    }

    @Test
    public void testSetAndGetCurrentPassWithNull() {

        // Assert the currentPass is null
        assertNull(dm.getCurrentPass());
    }

}
