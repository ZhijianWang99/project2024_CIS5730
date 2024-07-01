import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_changePassword_Test {

    @Test
    public void testChangePasswordSuccess() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/changePassword")) {
                    return "{\"status\":\"success\"}";
                }
                return null;
            }
        });
        
        assertNull(dm.getCurrentPass());
        boolean result = dm.changePassword("orgId1", "currentPassword", "newPassword");
        assertTrue(result);
        assertTrue(dm.getCurrentPass().equals("newPassword"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangePasswordIncorrectPassword() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/changePassword")) {
                    return "{\"status\":\"incorrect password\"}";
                }
                return null;
            }
        });
        
        assertNull(dm.getCurrentPass());
        dm.changePassword("orgId1", "incorrectPassword", "newPassword");
        assertNull(dm.getCurrentPass());
    }

    @Test(expected = IllegalStateException.class)
    public void testChangePasswordInvalidStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/changePassword")) {
                    return "{\"status\":\"some invalid status\"}";
                }
                return null;
            }
        });
        
        assertNull(dm.getCurrentPass());
        dm.changePassword("orgId1", "currentPassword", "newPassword");
        assertNull(dm.getCurrentPass());
    }

    @Test(expected = IllegalStateException.class)
    public void testChangePasswordWebClientNullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });
        
        assertNull(dm.getCurrentPass());
        dm.changePassword("orgId1", "currentPassword", "newPassword");
        assertNull(dm.getCurrentPass());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testChangePasswordWebClientEmptyResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "";
            }
        });

        assertNull(dm.getCurrentPass());
        dm.changePassword("orgId1", "currentPassword", "newPassword");
        assertNull(dm.getCurrentPass());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testChangePasswordWebClientEmptyResponse2() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{}";
            }
        });

        assertNull(dm.getCurrentPass());
        dm.changePassword("orgId1", "currentPassword", "newPassword");
        assertNull(dm.getCurrentPass());
    }

    @Test(expected = IllegalStateException.class)
    public void testChangePasswordWebClientError() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Error communicating with server");
            }
        });

        assertNull(dm.getCurrentPass());
        dm.changePassword("orgId1", "currentPassword", "newPassword");
        assertNull(dm.getCurrentPass());
    }

    @Test(expected = IllegalStateException.class)
    public void testChangePasswordClientNull() {
        DataManager dm = new DataManager(null);

        assertNull(dm.getCurrentPass());
        dm.changePassword("orgId1", "currentPassword", "newPassword");
        assertNull(dm.getCurrentPass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangePasswordOrgIdNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));

        assertNull(dm.getCurrentPass());
        dm.changePassword(null, "currentPassword", "newPassword");
        assertNull(dm.getCurrentPass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangePasswordCurrentPasswordNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));

        assertNull(dm.getCurrentPass());
        dm.changePassword("orgId1", null, "newPassword");
        assertNull(dm.getCurrentPass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangePasswordNewPasswordNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));

        assertNull(dm.getCurrentPass());
        dm.changePassword("orgId1", "currentPassword", null);
        assertNull(dm.getCurrentPass());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testChangePasswordUnknownInvalidStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"unknown status\"}";
            }
        });

        assertNull(dm.getCurrentPass());
        dm.changePassword("orgId1", "currentPassword", "newPassword");
        assertNull(dm.getCurrentPass());
    }

    @Test(expected = IllegalStateException.class)
    public void testChangePasswordInvalidJsonResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "invalid json";
            }
        });

        assertNull(dm.getCurrentPass());
        dm.changePassword("orgId1", "currentPassword", "newPassword");
        assertNull(dm.getCurrentPass());
    }
}
