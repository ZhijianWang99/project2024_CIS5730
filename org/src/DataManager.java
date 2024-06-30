
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DataManager {

	private final WebClient client;
	
	// Task 2.1: Cache for storing contributor data
	private final Map<String, String> contributorDataCache;
	private String currentUser = null;
	private String currentPass = null;

	public DataManager(WebClient client) {
		this.client = client;
		this.contributorDataCache = new HashMap<>() ;
	}

	/**
	 * Attempt to log the user into an Organization account using the login and password.
	 * This method uses the /findOrgByLoginAndPassword endpoint in the API
	 * @return an Organization object if successful; null if unsuccessful
	 */
	public Organization attemptLogin(String login, String password) {
		
		// Task 2.2: Throw exception
		if (client == null) {
            throw new IllegalStateException("Client is null!");
        }
		if (password == null) {
            throw new IllegalArgumentException("Password is null!");
        }
        if (login == null) {
            throw new IllegalArgumentException("Login is null!");
        }

		try {
			Map<String, Object> map = new HashMap<>();
			map.put("login", login);
			map.put("password", password);
			String response = client.makeRequest("/findOrgByLoginAndPassword", map);
			
			// Task 2.2: Throw exception
			if (response == null) {
                throw new IllegalStateException("WebClient response is null!");
            }

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String)json.get("status");

			if (status.equals("success")) {
				JSONObject data = (JSONObject)json.get("data");
				String fundId = (String)data.get("_id");
				String name = (String)data.get("name");
				String description = (String)data.get("description");  // error1: descrption -> description
				Organization org = new Organization(fundId, name, description);

				JSONArray funds = (JSONArray)data.get("funds");
				Iterator it = funds.iterator();
				while(it.hasNext()){
					JSONObject fund = (JSONObject) it.next(); 
					fundId = (String)fund.get("_id");
					name = (String)fund.get("name");
					description = (String)fund.get("description");
					long target = (Long)fund.get("target");

					Fund newFund = new Fund(fundId, name, description, target);

					JSONArray donations = (JSONArray)fund.get("donations");
					List<Donation> donationList = new LinkedList<>();
					Iterator it2 = donations.iterator();
					while(it2.hasNext()){
						JSONObject donation = (JSONObject) it2.next();
						String contributorId = (String)donation.get("contributor");
						String contributorName = this.getContributorName(contributorId);
						long amount = (Long)donation.get("amount");
						String date = (String)donation.get("date");
						donationList.add(new Donation(fundId, contributorName, amount, date));
					}

					newFund.setDonations(donationList);

					org.addFund(newFund);

					this.currentUser = login ;
					this.currentPass = password ;
				}

				return org;
			} else if (status.equals("login failed")) {
				return null ;
			}
			// Task 2.2: Throw exception rather than returning null
			else {
                throw new IllegalStateException("WebClient respond with invalid status!");
            }
			//else return null;
		}
		catch (Exception e) {
			// Communication error with the server
			throw new IllegalStateException("Error while communicating with the server", e);
		}
	}

	// wrapper on attemptLogin to refresh organization data
	public Organization refreshOrg() {
		return this.attemptLogin(this.currentUser, this.currentPass) ;
	}

	/**
	 * Look up the name of the contributor with the specified ID.
	 * This method uses the /findContributorNameById endpoint in the API.
	 * @return the name of the contributor on success; null if no contributor is found
	 */
	public String getContributorName(String id) {
		
		// Task 2.2: Throw exception
		if (client == null) {
            throw new IllegalStateException("Client is null!");
        }
        if (id == null) {
            throw new IllegalArgumentException("ID is null!");
        }
		
		// Task 2.1: Check with the cache first
        if (contributorDataCache.containsKey(id)) {
        	String outputCache=contributorDataCache.get(id);System.out.println("<Trivial> Cache found: "+outputCache);
            return outputCache;
        }

		try {

			Map<String, Object> map = new HashMap<>();
			map.put("id", id); // error2: "_id" -> "id"
			String response = client.makeRequest("/findContributorNameById", map); // error3: /findContributrNameById -> /findContributorNameById

			// Task 2.2: Throw exception
			if (response == null) {
                throw new IllegalStateException("WebClient response is null!");
            }
			
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String)json.get("status");

			if (status.equals("success")) {
				String name = (String)json.get("data");
				
				// Task 2.1: Add contributor name to cache
				contributorDataCache.put(id, name);System.out.println("<Trivial> Cache add name: "+name);
				return name;
				
			}
			// Task 2.2: Throw exception rather than returning null
			else {
                throw new IllegalStateException("WebClient respond with invalid status!");
            }
			//else return null;


		}
		catch (Exception e) {
			// Task 2.2: Throw exception rather than returning null
			throw new IllegalStateException("Error while communicating with the server", e);
			//return null;
		}	
	}

	/**
	 * For Task 3.4
	 * This method requests to add a donation to a fund using the /makeDonation endpoint
	 */
	public void addDonation(String contributorId, String fundId, int amount) {
		if (client == null) {
			throw new IllegalStateException("Client is null!") ;
		}
		if (contributorId == null) {
			throw new IllegalArgumentException("ContributorId is null") ;
		}
		if (fundId == null) {
			throw new IllegalArgumentException("fundId is null") ;
		}
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount value is invalid") ;
		}
		Map<String, Object> param = new HashMap<>() ;
		param.put("contributor", contributorId) ;
		param.put("fund", fundId) ;
		param.put("amount", String.valueOf(amount)) ;
		try {
			String response = client.makeRequest("/makeDonation", param) ;

			if (response == null) {
				throw new IllegalStateException("WebClient response is null");
			}

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String)json.get("status");

			if (!status.equals("success")) {
				throw new IllegalStateException("WebClient respond with invalid status!");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage()) ;
			throw new IllegalStateException("Error while communicating with the server", e);
		}
	}

	/**
	 * For Task 2.7
	 * This method requests to delete a fund using the /deleteFund endpoint the API
	 * @param orgId - String for organization ID to be deleted
	 * @return void
	 */
	public void deleteFund(String orgId) {
		if (client == null) {
			throw new IllegalStateException("Client is null!") ;
		}
		if (orgId == null) {
			throw new IllegalArgumentException("Org ID is null") ;
		}

		Map<String, Object> param = new HashMap<>() ;
		param.put("id", orgId) ;
		try {
			String response = client.makeRequest("/deleteFund", param) ;

			if (response == null) {
				throw new IllegalStateException("WebClient response is null");
			}

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String)json.get("status");

			if (!status.equals("success")) {
				throw new IllegalStateException("WebClient respond with invalid status!");
			}
		} catch (Exception e) {
			throw new IllegalStateException("Error while communicating with the server", e);
		}
		return ;
	}

	/**
	 * This method creates a new fund in the database using the /createFund endpoint in the API
	 * @return a new Fund object if successful; null if unsuccessful
	 */
	public Fund createFund(String orgId, String name, String description, long target) {
		
		// Task 2.2: Throw exception 
		if (client == null) {
            throw new IllegalStateException("Client is null!");
        }
        if (orgId == null) {
            throw new IllegalArgumentException("Organization ID is null!");
        }
        if (name == null) {
            throw new IllegalArgumentException("Name is null!");
        }
        if (description == null) {
            throw new IllegalArgumentException("Description is null!");
        }

		try {

			Map<String, Object> map = new HashMap<>();
			map.put("orgId", orgId);
			map.put("name", name);
			map.put("description", description);
			map.put("target", target);
			String response = client.makeRequest("/createFund", map);
			
			// Task 2.2: Throw exception
			if (response == null) {
                throw new IllegalStateException("WebClient response is null!");
            }

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String)json.get("status");

			if (status.equals("success")) {
				JSONObject fund = (JSONObject)json.get("data");
				String fundId = (String)fund.get("_id");
				return new Fund(fundId, name, description, target);
			}
			// Task 2.2: Throw exception rather than returning null
			else {
                throw new IllegalStateException("WebClient respond with invalid status!");
            }
			//else return null;

		}
		catch (Exception e) {
			// Task 2.2: Throw exception rather than returning null
			throw new IllegalStateException("Error while communicating with the server", e);
			//return null;
		}	
	}
	
	/**
     * Task 3.1
     * Method to create a new organization
     * This method employs the /createOrg endpoint in the API
     * @return an Organization object if successful; null if unsuccessful
     */
    public Organization createOrganization(String login, String password, String name, String description) {

        // Throw exception
        if (client == null) {
            throw new IllegalStateException("Client is null!");
        }
        if (login == null) {
            throw new IllegalArgumentException("Login is null!");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password is null!");
        }
        if (name == null) {
            throw new IllegalArgumentException("Name is null!");
        }
        if (description == null) {
            throw new IllegalArgumentException("Description is null!");
        }

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("login", login);
            map.put("password", password);
            map.put("name", name);
            map.put("description", description);
            String response = client.makeRequest("/createOrg", map);

            // Throw exception
            if (response == null) {
                throw new IllegalStateException("WebClient response is null!");
            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            if (status.equals("success")) {
                JSONObject data = (JSONObject) json.get("data");
                String orgId = (String) data.get("_id");
                Organization org = new Organization(orgId, name, description);
                return org;
            } else if (status.equals("login exists")) {
				System.out.println("Login name already exists");
                throw new IllegalStateException("Login name already exists!");
            } else {
                throw new IllegalStateException("WebClient responded with an invalid status!");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error while communicating with the server", e);
        }
    }

    /**
     * Task 3.2
     * Method to change password
     * This method employs the /changePassword endpoint in the API
     * @return true if successful
     */
    public boolean changePassword(String orgId, String currentPassword, String newPassword) {
    	
    	// Throw exception
        if (client == null) {
            throw new IllegalStateException("Client is null!");
        }
        if (orgId == null) {
            throw new IllegalArgumentException("ID is null!");
        }
        if (currentPassword == null) {
            throw new IllegalArgumentException("Current password is null!");
        }
        if (newPassword == null) {
            throw new IllegalArgumentException("New password is null!");
        }

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("orgId", orgId);
            map.put("currentPassword", currentPassword);
            map.put("newPassword", newPassword);

            String response = client.makeRequest("/changePassword", map);

            // Throw exception
            if (response == null) {
                throw new IllegalStateException("WebClient response is null!");
            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            if (status.equals("success")) {
                return true;
            } else if (status.equals("incorrect password")) {
                throw new IllegalArgumentException("Current password is incorrect!");
            } else {
                throw new IllegalStateException("WebClient responded with an invalid status!");
            }
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Error while communicating with the server", e);
        }
    }
	/**
	 * Task 3.3
	 * Method to edit the name or description
	 * This method employs the /editAccount endpoint in the API
	 * @return true if successful
	 */
	public boolean editAccountInfo(String id, String newName, String newDescription) {

		// Throw exception
		if (client == null) {
			throw new IllegalStateException("Client is null!");
		}
		if (id == null) {
			throw new IllegalArgumentException("ID is null!");
		}
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("id", id);
			if (newName != null) {
				map.put("name", newName);
			}
			if (newDescription != null) {
				map.put("description", newDescription);
			}
			String response = client.makeRequest("/editAccount", map);
			// Throw exception
			if (response == null) {
				throw new IllegalStateException("WebClient response is null!");
			}

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String) json.get("status");
			System.out.println(status);
			if (status.equals("success")) {
				return true;
			} else {
				throw new IllegalStateException("WebClient responded with an invalid status!");
			}
		} catch (Exception e) {
			throw new IllegalStateException("Error while communicating with the server", e);
		}
	}


}
