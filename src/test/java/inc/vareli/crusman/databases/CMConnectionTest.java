package inc.vareli.crusman.databases;

import inc.vareli.crusman.databases.Ship.RoomType;
import inc.vareli.crusman.databases.Trip.Service;
import inc.vareli.crusman.databases.Trip.TripBuilder;

import org.junit.Test;
import java.util.List;
import java.util.EnumMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertTrue;

public class CMConnectionTest {
	CMConnection testConnection = new CMConnection(
								"jdbc:mysql://cs1103.cs.unb.ca:3306/j3zh5",
								"j3zh5", "rGR45WHX");
	@Test
	public void testCMConnection() {
		testConnection = new CMConnection(
						"jdbc:mysql://cs1103.cs.unb.ca:3306/j3zh5",
						"j3zh5", "rGR45WHX");
		assertTrue(testConnection != null);
	}
	
	@Test
	public void testCreateShip() {
		EnumMap<RoomType,Integer> roomCounts = 
								new EnumMap<RoomType,Integer>(RoomType.class);
		roomCounts.put(RoomType.INTERIOR, 1);
		roomCounts.put(RoomType.OUTSIDE, 1);
		roomCounts.put(RoomType.BALCONY, 1);
		roomCounts.put(RoomType.SUITE, 3);
		
		EnumMap<RoomType,Integer> roomCounts2 =
								new EnumMap<RoomType,Integer>(RoomType.class);
		roomCounts2.put(RoomType.INTERIOR, 2);
		roomCounts2.put(RoomType.OUTSIDE, 3);
		roomCounts2.put(RoomType.BALCONY, 4);
		roomCounts2.put(RoomType.SUITE, 2);

		try {
			Ship s = testConnection.createShip(roomCounts);
			Ship s2 = testConnection.createShip(roomCounts2);
			assertTrue(s != null && s2 != null);
		} catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void testQueryShip() {
		try{
			CMConnection testConnection = new CMConnection("jdbc:mysql://cs1103.cs.unb.ca:3306/j3zh5", "j3zh5", "rGR45WHX");
			List<Ship> shipList = testConnection.queryShip();
			for(int i = 0; i<shipList.size(); i++){
				System.out.println(shipList.get(i).toString());
			}
		}catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	public void testCreateTrip() {
		CMConnection testConnection = new CMConnection("jdbc:mysql://cs1103.cs.unb.ca:3306/j3zh5", "j3zh5", "rGR45WHX");
		TripBuilder tb = new TripBuilder(testConnection.queryShip().get(0));
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		tb.addCost(Service.MEALS, 120);
		tb.addCost(Service.DRINKS, 130);
		tb.addCost(RoomType.BALCONY, 130);
		tb.addCost(RoomType.INTERIOR, 140);
		tb.addCost(RoomType.OUTSIDE, 130);
		tb.addCost(RoomType.SUITE, 110);
		try {
			tb.addPort(dateFormat.parse("2023-11-23"), dateFormat.parse("2023-11-24"), "Rome", "GMT");
			tb.addPort(dateFormat.parse("2023-11-25"), dateFormat.parse("2023-11-26"), "Pisa", "GMT");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Trip trip1 = testConnection.createTrip(tb);

		TripBuilder tb2 = new TripBuilder(testConnection.queryShip().get(1));
		tb2.addCost(Service.MEALS, 150);
		tb2.addCost(Service.DRINKS, 160);
		tb2.addCost(RoomType.BALCONY, 170);
		tb2.addCost(RoomType.INTERIOR, 180);
		tb2.addCost(RoomType.OUTSIDE, 190);
		tb2.addCost(RoomType.SUITE, 200);
		try {
			tb2.addPort(dateFormat.parse("2023-10-20"), dateFormat.parse("2023-10-23"), "Cannes", "GMT");
			tb2.addPort(dateFormat.parse("2023-10-25"), dateFormat.parse("2023-10-26"), "Pisa", "GMT");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Trip trip2 = testConnection.createTrip(tb2);
	}

	@Test
	public void testQueryTrip() {
		try{
			CMConnection testConnection = new CMConnection("jdbc:mysql://cs1103.cs.unb.ca:3306/j3zh5", "j3zh5", "rGR45WHX");
			List<Trip> tripList = testConnection.queryTrip();
			for(int i = 0; i<tripList.size(); i++ ){
				System.out.println(tripList.get(i).toString());
			}
		}catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void testBookTrip(){
		try{
			Trip ticketTrip = testConnection.queryTrip().get(0);
			System.out.println(testConnection.bookTrip(ticketTrip, "John Doe", true, false, RoomType.INTERIOR));
		}catch(IllegalArgumentException e){
			System.out.println(e.getMessage());
		}
	}
   
   @Test
   public void testUpdateFees() {
        String url = "jdbc:mysql://localhost:3306/testdb";
        String loginID = "testuser";
        String loginPass = "testpassword";

        try {
            CMConnection cmConnection = new CMConnection(url, loginID, loginPass);

            int testTripID = 1;

            // Initial fees
            double initialDrinkFees = getFees(cmConnection, testTripID, Service.DRINKS);

            // Update the drink fees
            cmConnection.updateFees(testTripID, Service.DRINKS, 50.0);

            // Check if the fees have been updated
            double updatedDrinkFees = getFees(cmConnection, testTripID, Service.DRINKS);

            assertEquals(50.0, updatedDrinkFees, 0.01);
            assertNotEquals(initialDrinkFees, updatedDrinkFees, 0.01);

        } catch (IllegalArgumentException e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
    }

    private double getFees(CMConnection cmConnection, int tripID, CostType type) {
        try {
            List<Trip> trips = cmConnection.queryTrip();
            for (Trip trip : trips) {
                if (trip.ID == tripID) {
                    return trip.COSTS.get(type);
                }
            }
            fail("Trip with ID " + tripID + " not found.");
        } catch (IllegalArgumentException e) {
            fail("Exception thrown during test: " + e.getMessage());
        }
        return 0.0;
    }
}
