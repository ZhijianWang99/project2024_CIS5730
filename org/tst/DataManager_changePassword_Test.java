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

        boolean result = dm.changePassword("orgId1", "currentPassword", "newPassword");
        assertTrue(result);
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

        dm.changePassword("orgId1", "incorrectPassword", "newPassword");
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

        dm.changePassword("orgId1", "currentPassword", "newPassword");
    }

    @Test(expected = IllegalStateException.class)
    public void testChangePasswordWebClientNullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        dm.changePassword("orgId1", "currentPassword", "newPassword");
    }
    
    @Test(expected = IllegalStateException.class)
    public void testChangePasswordWebClientEmptyResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "";
            }
        });

        dm.changePassword("orgId1", "currentPassword", "newPassword");
    }
    
    @Test(expected = IllegalStateException.class)
    public void testChangePasswordWebClientEmptyResponse2() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{}";
            }
        });

        dm.changePassword("orgId1", "currentPassword", "newPassword");
    }

    @Test(expected = IllegalStateException.class)
    public void testChangePasswordWebClientError() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Error communicating with server");
            }
        });

        dm.changePassword("orgId1", "currentPassword", "newPassword");
    }

    @Test(expected = IllegalStateException.class)
    public void testChangePasswordClientNull() {
        DataManager dm = new DataManager(null);

        dm.changePassword("orgId1", "currentPassword", "newPassword");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangePasswordOrgIdNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));

        dm.changePassword(null, "currentPassword", "newPassword");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangePasswordCurrentPasswordNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));

        dm.changePassword("orgId1", null, "newPassword");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChangePasswordNewPasswordNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));

        dm.changePassword("orgId1", "currentPassword", null);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testChangePasswordUnknownInvalidStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"unknown status\"}";
            }
        });

        dm.changePassword("orgId1", "currentPassword", "newPassword");
    }

    @Test(expected = IllegalStateException.class)
    public void testChangePasswordInvalidJsonResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "invalid json";
            }
        });

        dm.changePassword("orgId1", "currentPassword", "newPassword");
    }
}
