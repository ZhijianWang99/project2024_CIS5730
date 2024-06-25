import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_createOrganization_Test {

    @Test
    public void testSuccessfulOrganizationCreation() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"_id\":\"org1\",\"name\":\"name1\",\"description\":\"description1\"}}";
            }
        });

        Organization org = dm.createOrganization("newlogin", "password", "name1", "description1");

        assertNotNull(org);
        assertEquals("org1", org.getId());
        assertEquals("name1", org.getName());
        assertEquals("description1", org.getDescription());
    }

    @Test(expected = IllegalStateException.class)
    public void testOrganizationCreationWithExistingLogin() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"login exists\"}";
            }
        });

        dm.createOrganization("existedlogin", "password", "name1", "description1");
    }

    @Test(expected = IllegalStateException.class)
    public void testNullClient() {
        DataManager dm = new DataManager(null);
        dm.createOrganization("newlogin", "password", "name1", "description1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullLogin() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization(null, "password", "name1", "description1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPassword() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("newlogin", null, "name1", "description1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullName() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("newlogin", "password", null, "description1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDescription() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("newlogin", "password", "name1", null);
    }

    @Test(expected = IllegalStateException.class)
    public void testCommunicationError() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new IllegalArgumentException("Some error!");
            }
        });

        dm.createOrganization("newLogin", "password", "name1", "description1");
    }

    @Test(expected = IllegalStateException.class)
    public void testStatusNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{}";
            }
        });

        dm.createOrganization("newlogin", "password", "name1", "description1");
    }

    @Test(expected = IllegalStateException.class)
    public void testRespEmpty() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "";
            }
        });

        dm.createOrganization("newlogin", "password", "name1", "description1");
    }

    @Test(expected = IllegalStateException.class)
    public void testRespNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        dm.createOrganization("newlogin", "password", "name1", "description1");
    }

    @Test(expected = IllegalStateException.class)
    public void testException() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Some exception!");
            }
        });

        dm.createOrganization("newlogin", "password", "name1", "description1");
    }

    public static class TestWebClient extends WebClient {

        public TestWebClient(String host, int port) {
            super(host, port);
        }

        @Override
        public String makeRequest(String endpoint, Map<String, Object> map) {
            throw new RuntimeException("Error while communicating with the server!");
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateOrganizationServerError() {
        WebClient client = new TestWebClient("localhost", 3001);
        DataManager dataManager = new DataManager(client);
        dataManager.createOrganization("newlogin", "password", "name1", "description1");
    }
}
