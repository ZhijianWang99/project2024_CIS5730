import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    public void createFundSuccessTest() {
        final String testString = "test+\ntest desc+\n10000\n";
        provideInput(testString);

        UserInterface ui = new UserInterface(new DataManager(new WebClient("testHost", 31000)), new Organization("testId", "testName", "testDescription"));
        ui.createFund();

        assertTrue(testOut.toString().contains("Fund created successfully")); // Assuming that your createFund method outputs this text after successful fund creation.
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
    public void createFundBlankTest() {
        final String testString = "   \n  \n finalfundName\ntest desc+\n10000\n";
        provideInput(testString);
        UserInterface ui = new UserInterface(new DataManager(new WebClient("testHost", 31000)), new Organization("testId", "testName", "testDescription"));
        ui.createFund();
        String[] arrOfStr = testOut.toString().split("\r\n");
        Map<String, Integer> countMap = countFreq(arrOfStr, Arrays.asList(arrOfStr).size());
        assertEquals((Integer) 3, countMap.get("Enter fund name: "));
        assertTrue(testOut.toString().contains("Fund created successfully"));
    }

    @Test
    public void createFundDescriptionBlankTest() {
        final String testString = "finalfundName\n   \n   \ntest desc+\n100000\n";
        provideInput(testString);
        UserInterface ui = new UserInterface(new DataManager(new WebClient("testHost", 31000)), new Organization("testId", "testName", "testDescription"));
        ui.createFund();
        String[] arrOfStr = testOut.toString().split("\r\n");
        Map<String, Integer> countMap = countFreq(arrOfStr, Arrays.asList(arrOfStr).size());
        assertEquals((Integer) 3, countMap.get("Enter fund description: "));
        assertTrue(testOut.toString().contains("Fund created successfully"));
    }

    @Test
    public void createFundNumberTest() {
        final String testString = "finalfundName\ntest desc+\n \n 10.0\n -5\n100000\n";
        provideInput(testString);
        UserInterface ui = new UserInterface(new DataManager(new WebClient("testHost", 31000)), new Organization("testId", "testName", "testDescription"));
        ui.createFund();
        String[] arrOfStr = testOut.toString().split("\r\n");
        Map<String, Integer> countMap = countFreq(arrOfStr, Arrays.asList(arrOfStr).size());
        assertEquals((Integer) 4, countMap.get("Enter fund target: "));
        assertTrue(testOut.toString().contains("Fund created successfully"));
    }
}