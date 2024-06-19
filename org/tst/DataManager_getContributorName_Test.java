import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import org.junit.Test;

public class DataManager_getContributorName_Test {

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
    public void testStatusNull() {
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
    public void testRespEmpty() {
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
    public void testRespNull() {
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
    public void testException() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Exception occurred");
            }
        });

        String name = dm.getContributorName("AAA");

        assertNull(name);
    }

    @Test
    public void testSuccessfulGetContributorNameCheckCache() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":\"James Sun\"}";
            }
        });

        assertEquals(0, dm.getContributorDataCache().size());
        String name = dm.getContributorName("AAA");
        String consoleOutput = out.toString();
        assertTrue(consoleOutput.contains("<Trivial> Cache add name: James Sun"));
        assertFalse(consoleOutput.contains("<Trivial> Cache found:"));
        assertNotNull(name);
        assertEquals("James Sun", name);
        assertEquals(1, dm.getContributorDataCache().size());

        // second time it should be in the cache
        System.setIn(System.in);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        name = dm.getContributorName("AAA");
        assertNotNull(name);
        assertEquals("James Sun", name);
        consoleOutput = out.toString();
        assertFalse(consoleOutput.contains("<Trivial> Cache add name: James Sun"));
        assertTrue(consoleOutput.contains("<Trivial> Cache found: James Sun"));
    }
    
}
