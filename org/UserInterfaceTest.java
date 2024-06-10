import static org.junit.Assert.*;
import java.util.Map;
import org.junit.Test;

public class UserInterfaceTest {
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