import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class DataManager_editAccountInfo_Test {

    @Test
    public void testEditAccountInfoSuccess() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/editAccount")) {
                    return "{\"status\":\"success\"}";
                }
                return null;
            }
        });

        boolean result = dm.editAccountInfo("orgId1", "newName", "newDescription");
        assertTrue(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEditAccountInfoIDNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        dm.editAccountInfo(null, "newName", "newDescription");
    }

    @Test(expected = IllegalStateException.class)
    public void testEditAccountInfoInvalidStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/editAccount")) {
                    return "{\"status\":\"some invalid status\"}";
                }
                return null;
            }
        });

        dm.editAccountInfo("orgId1", "newName", "newDescription");
    }

    @Test(expected = IllegalStateException.class)
    public void testEditAccountInfoWebClientNullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        dm.editAccountInfo("orgId1", "newName", "newDescription");
    }

    @Test(expected = IllegalStateException.class)
    public void testEditAccountInfoWebClientEmptyResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "";
            }
        });

        dm.editAccountInfo("orgId1", "newName", "newDescription");
    }

    @Test(expected = IllegalStateException.class)
    public void testEditAccountInfoWebClientEmptyResponse2() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{}";
            }
        });

        dm.editAccountInfo("orgId1", "newName", "newDescription");
    }

    @Test(expected = IllegalStateException.class)
    public void testEditAccountInfoWebClientError() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Error communicating with server");
            }
        });

        dm.editAccountInfo("orgId1", "newName", "newDescription");
    }

    @Test(expected = IllegalStateException.class)
    public void testEditAccountInfoClientNull() {
        DataManager dm = new DataManager(null);

        dm.editAccountInfo("orgId1", "newName", "newDescription");
    }

    @Test(expected = IllegalStateException.class)
    public void testEditAccountInfoUnknownInvalidStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"unknown status\"}";
            }
        });

        dm.editAccountInfo("orgId1", "newName", "newDescription");
    }

    @Test(expected = IllegalStateException.class)
    public void testEditAccountInfoInvalidJsonResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "invalid json";
            }
        });

        dm.editAccountInfo("orgId1", "newName", "newDescription");
    }
}
