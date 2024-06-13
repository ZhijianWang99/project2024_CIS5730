import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.Assert.*;

public class UserInterfaceTest {
    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @Before
    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    @After
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    @Test
    public void startTest() {

        final String testString = "0\nq\n";
        provideInput(testString);

        Fund testFund = new Fund("Test fund id", "testFund", "description",2000);
        Organization testOrg = new Organization("TestId", "TestName", "TestDescription") {
            @Override
            public List<Fund> getFunds() {
                return Collections.singletonList(testFund);
            }
        };

        UserInterface ui = new UserInterface(new DataManager(new WebClient("testHost", 31000)), testOrg) {
            @Override
            public void createFund() {
                // Just print a message to simulate fund creation
                System.out.println("Fund created");
            }
        };
        ui.start();

        String consoleOutput = testOut.toString();

        assertTrue(consoleOutput.contains("Fund created"));
        assertTrue(consoleOutput.contains("Good bye!"));
    }

    private static Map<String, Integer> countFreq(String[] array, int n)
    {
        Map<String, Integer> ret = new HashMap<>();

        // Traverse through array elements and
        // count frequencies
        for (int i = 0; i < n; i++)
        {
            if (ret.containsKey(array[i]))
            {
                ret.put(array[i], ret.get(array[i]) + 1);
            }
            else
            {
                ret.put(array[i], 1);
            }
        }
        return ret;
    }

    @Test
    public void displayFundTest() {
        provideInput("aa\n");
        // Mocked data
        List<Donation> donations = Arrays.asList(
                new Donation("1","Donor1", 500,"2020-05-20T04:20:05.200Z"),
                new Donation("2","Donor2", 1000,"2021-01-10T02:10:01.100Z")
        );

        Fund testFund = new Fund("Test fund id", "testFund", "description",2000) {
            @Override
            public List<Donation> getDonations() {
                return donations;
            }
        };

        Organization testOrg = new Organization("TestId", "TestName", "TestDescription") {
            @Override
            public List<Fund> getFunds() {
                return Collections.singletonList(testFund);
            }
        };
        UserInterface ui = new UserInterface(new DataManager(new WebClient("testHost", 31000)), testOrg);

        // Mock System.out
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        ui.displayFund(1);

        // Contains assertion for validations
        String consoleOutput = out.toString();
        assertTrue(consoleOutput.contains("Name: " + testFund.getName()));
        assertTrue(consoleOutput.contains("Description: " + testFund.getDescription()));
        assertTrue(consoleOutput.contains("Target: $" + testFund.getTarget()));
        assertTrue(consoleOutput.contains("Total donation amount: $1500 (75.00% of target)"));
    }

    @Test
    public void testFormatDateNullDonationInput() {
        assertEquals("--/--/----", UserInterface.formatDate(null)) ;
    }

    @Test
    public void testFormatDateNullGetDate() {
        assertEquals("--/--/----",
                UserInterface.formatDate(new Donation("69",
                        "funk", 69, null)));
    }

    @Test
    public void testFormatDate() {
        assertEquals("06/18/2021",
                UserInterface.formatDate(new Donation("69",
                        "funk", 69, "2021-06-18T04:21:04.807Z")));
    }
}