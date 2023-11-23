package inc.vareli.crusman.databases;

import inc.vareli.crusman.databases.Ship.RoomType;

import org.junit.Test;
import java.util.List;
import java.util.EnumMap;

import static org.junit.Assert.assertTrue;

public class CMConnectionTest {

	@Test
	public void testCMConnection() {
		CMConnection testConnection = new CMConnection(
										"jdbc:mysql://cs1103.cs.unb.ca:3306/j3zh5",
										"j3zh5", "rGR45WHX");
		assertTrue(testConnection != null);
	}
	
	@Test
	public void testCreateShip() {
		CMConnection testConnection = new CMConnection(
										"jdbc:mysql://cs1103.cs.unb.ca:3306/j3zh5",
										"j3zh5", "rGR45WHX");

		EnumMap<RoomType,Integer> roomCounts = 
								new EnumMap<RoomType,Integer>(RoomType.class);
		roomCounts.put(RoomType.INTERIOR, 1);
		roomCounts.put(RoomType.OUTSIDE, 1);
		roomCounts.put(RoomType.BALCONY, 1);
		roomCounts.put(RoomType.SUITE, 2);
		
		EnumMap<RoomType,Integer> roomCounts2 =
								new EnumMap<RoomType,Integer>(RoomType.class);
		roomCounts2.put(RoomType.INTERIOR, 2);
		roomCounts2.put(RoomType.OUTSIDE, 1);
		roomCounts2.put(RoomType.BALCONY, 1);
		roomCounts2.put(RoomType.SUITE, 2);

		try {
			Ship s = testConnection.createShip(roomCounts);
			Ship s2 = testConnection.createShip(roomCounts2);
			assertTrue(s != null && s2 != null);
		} catch(IllegalArgumentException e) {
			System.out.println(e);
		}
	}

	@Test
	public void testQueryShip() {
		try{
			CMConnection testConnection = new CMConnection("jdbc:mysql://cs1103.cs.unb.ca:3306/j3zh5", "j3zh5", "rGR45WHX");
			List<Ship> shipList = testConnection.queryShip();
			for(int i = 0; i < shipList.size(); i++) {
				System.out.println(shipList.get(i).toString());
			}
		} catch(IllegalArgumentException e) {
			System.out.println("error retrieving ships");
		}
	}
}
