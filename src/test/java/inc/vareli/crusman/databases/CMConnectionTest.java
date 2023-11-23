package inc.vareli.crusman.databases;

//import static org.junit.jupiter.api.Assertions.*;

import java.util.EnumMap;

//import org.junit.jupiter.api.Test;

import inc.vareli.crusman.databases.Ship.RoomType;

class CMConnectionTest {

	//@Test
	void testCMConnection() {
		//create a database connection in my database
		CMConnection testConnection = new CMConnection("jdbc:mysql://cs1103.cs.unb.ca:3306/j3zh5", "j3zh5", "rGR45WHX");
	}
	
	//@Test
	void testCreateShip() {
		CMConnection testConnection = new CMConnection("jdbc:mysql://cs1103.cs.unb.ca:3306/j3zh5", "j3zh5", "rGR45WHX");
		EnumMap<RoomType,Integer> roomCounts = new EnumMap<RoomType,Integer>(RoomType.class);
		roomCounts.put(RoomType.INTERIOR, 1);
		roomCounts.put(RoomType.OUTSIDE, 1);
		roomCounts.put(RoomType.BALCONY, 1);
		roomCounts.put(RoomType.SUITE, 2);
		
		EnumMap<RoomType,Integer> roomCounts2 = new EnumMap<RoomType,Integer>(RoomType.class);
		roomCounts2.put(RoomType.INTERIOR, 2);
		roomCounts2.put(RoomType.OUTSIDE, 1);
		roomCounts2.put(RoomType.BALCONY, 1);
		roomCounts2.put(RoomType.SUITE, 2);
		try {
		Ship s = testConnection.createShip(roomCounts);
		Ship s2 = testConnection.createShip(roomCounts2);
		} catch(IllegalArgumentException e) {
			System.out.println(e);
		}
	}

	//@Test
	void testQueryShip() {
		try {
			CMConnection testConnection = new CMConnection("jdbc:mysql://cs1103.cs.unb.ca:3306/j3zh5", "j3zh5", "rGR45WHX");
			ArrayList<Ship> shipList = testConnection.queryShip();
			for (int i = 0; i < shipList.size(); i++) {
				System.out.println(shipList.get(i).toString());
			}
		} catch(IllegalArgumentException e) {
			System.out.println("error retrieving ships");
		}
	}
}
