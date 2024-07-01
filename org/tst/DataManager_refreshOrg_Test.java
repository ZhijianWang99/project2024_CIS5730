import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_refreshOrg_Test {

    @Test
    public void testSuccessfulRefreshOrg() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"_id\":\"org1\",\"name\":\"name1\",\"description\":\"description1\",\"funds\":[]}}";
            }
        });

        // Simulate a logged-in user
        dm.setCurrentUser("user");
        dm.setCurrentPass("password");

        Organization org = dm.refreshOrg();

        assertNotNull(org);
        assertEquals("org1", org.getId());
        assertEquals("name1", org.getName());
        assertEquals("description1", org.getDescription());
        
        assertEquals("user", dm.getCurrentUser());
        assertEquals("password", dm.getCurrentPass());
    }

    @Test(expected = IllegalStateException.class)
    public void testRefreshOrgUnsuccessfulLogin() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"}";
            }
        });

        // Simulate a logged-in user
        dm.setCurrentUser("user");
        dm.setCurrentPass("password");

        dm.refreshOrg();
        
        assertEquals("user", dm.getCurrentUser());
        assertEquals("password", dm.getCurrentPass());
    }

    @Test(expected = IllegalStateException.class)
    public void testRefreshOrgCommunicationError() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new IllegalArgumentException("Something bad!");
            }
        });

        // Simulate a logged-in user
        dm.setCurrentUser("user");
        dm.setCurrentPass("password");

        dm.refreshOrg();
    }

    @Test(expected = IllegalStateException.class)
    public void testRefreshOrgStatusNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{}";
            }
        });

        // Simulate a logged-in user
        dm.setCurrentUser("user");
        dm.setCurrentPass("password");

        dm.refreshOrg();
        
        assertEquals("user", dm.getCurrentUser());
        assertEquals("password", dm.getCurrentPass());
    }

    @Test(expected = IllegalStateException.class)
    public void testRefreshOrgRespEmpty() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "";
            }
        });

        // Simulate a logged-in user
        dm.setCurrentUser("user");
        dm.setCurrentPass("password");

        dm.refreshOrg();
    }

    @Test(expected = IllegalStateException.class)
    public void testRefreshOrgRespNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        // Simulate a logged-in user
        dm.setCurrentUser("user");
        dm.setCurrentPass("password");

        dm.refreshOrg();
        
        assertEquals("user", dm.getCurrentUser());
        assertEquals("password", dm.getCurrentPass());
    }

    @Test(expected = IllegalStateException.class)
    public void testRefreshOrgException() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Exception occurred");
            }
        });

        // Simulate a logged-in user
        dm.setCurrentUser("user");
        dm.setCurrentPass("password");

        dm.refreshOrg();
        
        assertEquals("user", dm.getCurrentUser());
        assertEquals("password", dm.getCurrentPass());
    }
}
