import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_addDonation_Test {
	
	private DataManager dm;
	
	@Test(expected=IllegalStateException.class)
	public void testAddDonation_WebClientIsNull() {

		dm = new DataManager(null);
		dm.addDonation("contributorId", "orgId", 33);
		fail("DataManager.addDonation does not throw IllegalStateException when WebClient is null");

	}

	@Test(expected=IllegalArgumentException.class)
	public void testAddDonation_ContributorIdIsNull() {

		dm = new DataManager(new WebClient("localhost", 3001));
		dm.addDonation(null, "orgId", 33);
		fail("DataManager.addDonation does not throw IllegalArgumentException when contributorId is null");

	}

	@Test(expected=IllegalArgumentException.class)
	public void testAddDonation_FundIdIsNull() {

		dm = new DataManager(new WebClient("localhost", 3001));
		dm.addDonation("cID", null, 33);
		fail("DataManager.addDonation does not throw IllegalArgumentException when fundId is null");

	}

	@Test(expected=IllegalArgumentException.class)
	public void testAddDonation_AmountIsInvalidZero() {

		dm = new DataManager(new WebClient("localhost", 3001));
		dm.addDonation("cID", "fId", 0);
		fail("DataManager.addDonation does not throw IllegalArgumentException when Amount is zero");

	}

	@Test(expected=IllegalArgumentException.class)
	public void testAddDonation_AmountIsInvalidNegative() {

		dm = new DataManager(new WebClient("localhost", 3001));
		dm.addDonation("cID", "fId", -1);
		fail("DataManager.addDonation  does not throw IllegalArgumentException when Amount is Negative");

	}

	@Test(expected=IllegalStateException.class)
	public void testAddDonation_WebClientCannotConnectToServer() {

		// this assumes no server is running on port 3002
		dm = new DataManager(new WebClient("localhost", 3002));
		dm.addDonation("cID", "fId", 33);
		fail("DataManager.addDonation does not throw IllegalStateException when WebClient cannot connect to server");

	}

	@Test(expected=IllegalStateException.class)
	public void testAddDonation_WebClientReturnsNull() {

		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}
		});
		dm.addDonation("cID", "fId", 33);
		fail("DataManager.addDonation does not throw IllegalStateException when WebClient returns null");
	}


	@Test(expected=IllegalStateException.class)
	public void testAddDonation_WebClientReturnsError() {
		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"error\",\"error\":\"An unexpected database error occurred\"}";
			}
		});
		dm.addDonation("cID", "fId", 33);
		fail("DataManager.addDonation does not throw IllegalStateException when WebClient returns error");
	}

	@Test(expected=IllegalStateException.class)
	public void testAddDonation_WebClientReturnsMalformedJSON() {

		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "I AM NOT JSON!";
			}
		});
		dm.addDonation("cID", "fId", 33);
		fail("DataManager.addDonation does not throw IllegalStateException when WebClient returns malformed JSON");

	}
	
	@Test
	public void testAddDonation_SuccessfulDonation() {
		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\"}";
			}
		});
		dm.addDonation("cID", "fId", 1);
	}
	
	@Test
	public void testAddDonation_SuccessfulDonation2() {
		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\"}";
			}
		});
		dm.addDonation("cID", "fId", 1000);
	}


}
