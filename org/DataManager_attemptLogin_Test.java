import static org.junit.Assert.*;
import java.util.Map;
import org.junit.Test;

public class DataManager_attemptLogin_Test {

    @Test
    public void testSuccessfulLoginBasic() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"_id\":\"org1\",\"name\":\"name1\",\"description\":\"description1\",\"funds\":[]}}";
            }
        });

        Organization org = dm.attemptLogin("user", "password");

        assertNotNull(org);
        assertEquals("org1", org.getId());
        assertEquals("name1", org.getName());
        assertEquals("description1", org.getDescription());
    }
    
    @Test
    public void testSuccessfulLoginAndFund() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"_id\":\"org1\",\"name\":\"name1\",\"description\":\"description1\",\"funds\":[{\"_id\":\"fund1\",\"name\":\"fund name1\",\"description\":\"fund description1\",\"target\":1200,\"donations\":[]}]}}";
            }
        });

        Organization org = dm.attemptLogin("user", "password");

        assertNotNull(org);
        assertEquals("org1", org.getId());
        assertEquals("name1", org.getName());
        assertEquals("description1", org.getDescription());
        assertFalse(org.getFunds().isEmpty());

        Fund fund = org.getFunds().get(0);
        assertEquals("fund1", fund.getId());
        assertEquals("fund name1", fund.getName());
        assertEquals("fund description1", fund.getDescription());
        assertEquals(1200, fund.getTarget());
        assertTrue(fund.getDonations().isEmpty());
    }
    
    @Test
    public void testSuccessfulLoginAndFundPlusDonation() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"_id\":\"org1\",\"name\":\"name1\",\"description\":\"description1\",\"funds\":[{\"_id\":\"fund1\",\"name\":\"fund name1\",\"description\":\"fund description1\",\"target\":1500,\"donations\":[{\"amount\":1000,\"date\":\"2024-06-07\"}]}]}}";
            }
        });

        Organization org = dm.attemptLogin("user", "password");

        assertNotNull(org);
        assertEquals("org1", org.getId());
        assertEquals("name1", org.getName());
        assertEquals("description1", org.getDescription());
        assertFalse(org.getFunds().isEmpty());

        Fund fund = org.getFunds().get(0);
        assertEquals("fund1", fund.getId());
        assertEquals("fund name1", fund.getName());
        assertEquals("fund description1", fund.getDescription());
        assertEquals(1500, fund.getTarget());
        assertFalse(fund.getDonations().isEmpty());

        Donation donation = fund.getDonations().get(0);
        assertEquals("fund1", donation.getFundId());
        assertNull(donation.getContributorName());
        assertEquals(1000, donation.getAmount());
        assertEquals("2024-06-07", donation.getDate());
    }

    @Test
    public void testUnsuccessfulLogin() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"}";
            }
        });

        Organization org = dm.attemptLogin("user", "password");

        assertNull(org);
    }

    @Test(expected = IllegalStateException.class)
    public void testStatusNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{}";
            }
        });

        Organization org = dm.attemptLogin("user", "password");

    }

    @Test(expected = IllegalStateException.class)
    public void testRespEmpty() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "";
            }
        });

        Organization org = dm.attemptLogin("user", "password");
    }

    @Test(expected = IllegalStateException.class)
    public void testRespNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        Organization org = dm.attemptLogin("user", "password");
    }

    @Test(expected = IllegalStateException.class)
    public void testException() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Exception occurred");
            }
        });

        Organization org = dm.attemptLogin("user", "password");
    }

    public static class TestWebClient extends WebClient {

        public TestWebClient(String host, int port) {
            super(host, port);
        }

        @Override
        public String makeRequest(String endpoint, Map<String, Object> map) {
            throw new RuntimeException("Server communication error");
        }
    }

    @Test
    public void testAttemptLoginServerError() {
        WebClient client = new TestWebClient("localhost", 3001);
        DataManager dataManager = new DataManager(client);

        assertThrows(IllegalStateException.class, () -> {
            dataManager.attemptLogin("testLogin", "testPassword");
        });
    }
}
