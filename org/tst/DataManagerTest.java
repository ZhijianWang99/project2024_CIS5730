import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManagerTest {
    /*
     * These are tests for deleteFund. Note, exception robustness cases
     * can be found inside the DataManagerRobustnessTest for this method.
     */
    @Test(expected = IllegalStateException.class)
    public void testDeleteFundNullClient() {
        DataManager dm = new DataManager(null);
        dm.deleteFund("orgId");
    }

    @Test
    public void testDeleteClientSuccessfulResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":\"James Sun\"}";
            }
        });
        dm.deleteFund("orgId");
        // if passes, yay! exception => fail
    }


    /*
     * These are tests for getContributorName
     */
    @Test
    public void testSuccessfulGetContributorName() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":\"James Sun\"}";
            }
        });

        String name = dm.getContributorName("AAA");

        assertNotNull(name);
        assertEquals("James Sun", name);
    }

    @Test(expected = IllegalStateException.class)
    public void testUnsuccessfulGetContributorName() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"not found\"}";
            }
        });

        String name = dm.getContributorName("AAA");

        assertNull(name);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContributorStatusNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{}";
            }
        });

        String name = dm.getContributorName("AAA");

        assertNull(name);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContributorRespEmpty() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "";
            }
        });

        String name = dm.getContributorName("AAA");

        assertNull(name);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContributorRespNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        String name = dm.getContributorName("AAA");

        assertNull(name);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContributorException() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Exception occurred");
            }
        });

        String name = dm.getContributorName("AAA");

        assertNull(name);
    }
    /*
     * These are tests for the DataManager.attemptLogin method
     */

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

    @Test(expected = IllegalStateException.class)
    public void testSuccessfulLoginAndFundPlusDonationWithNullContributorName() {
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
                return "{\"status\":\"login failed\"}";
            }
        });

        Organization org = dm.attemptLogin("user", "password");

        assertNull(org);
    }

    @Test(expected = IllegalStateException.class)
    public void testCommunicationError() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new IllegalArgumentException("Something bad!");
            }
        });

        Organization org = dm.attemptLogin("user", "password");
    }

    @Test(expected = IllegalStateException.class)
    public void testAttemptLoginStatusNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{}";
            }
        });

        Organization org = dm.attemptLogin("user", "password");

    }

    @Test(expected = IllegalStateException.class)
    public void testAttemptLoginRespEmpty() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "";
            }
        });

        Organization org = dm.attemptLogin("user", "password");
    }

    @Test(expected = IllegalStateException.class)
    public void testAttemptLoginRespNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        Organization org = dm.attemptLogin("user", "password");
    }

    @Test(expected = IllegalStateException.class)
    public void testAttemptLoginException() {
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

    @Test(expected = IllegalStateException.class)
    public void testAttemptLoginServerError() {
        WebClient client = new DataManager_attemptLogin_Test.TestWebClient("localhost", 3001);
        DataManager dataManager = new DataManager(client);
        dataManager.attemptLogin("testLogin", "testPassword");
    }

    /*
     * These are tests for the DataManager.createFund method.
     */

    @Test
    public void testSuccessfulCreation() {

        DataManager dm = new DataManager(new WebClient("localhost", 3001) {

            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,\"org\":\"5678\",\"donations\":[],\"__v\":0}}";

            }

        });


        Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);

        assertNotNull(f);
        assertEquals("this is the new fund", f.getDescription());
        assertEquals("12345", f.getId());
        assertEquals("new fund", f.getName());
        assertEquals(10000, f.getTarget());

    }

    @Test(expected = IllegalStateException.class)
    public void testUnsuccessfulCreation() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"not found\"}";
            }
        });

        Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);

        assertNull(f);
    }


    @Test(expected = IllegalStateException.class)
    public void testStatusNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{}";
            }
        });

        Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);

    }

    @Test(expected = IllegalStateException.class)
    public void testRespEmpty() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "";
            }
        });

        Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);

    }

    @Test(expected = IllegalStateException.class)
    public void testRespNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);

    }

    @Test(expected = IllegalStateException.class)
    public void testException() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Exception occurred");
            }
        });

        Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);

    }


}

