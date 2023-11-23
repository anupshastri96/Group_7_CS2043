package inc.vareli.crusman.databases;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EnumMap;



import org.junit.jupiter.api.Test;

import inc.vareli.crusman.databases.Ship.RoomType;
import inc.vareli.crusman.databases.Trip.Service;
import inc.vareli.crusman.databases.Trip.TripBuilder;

class CMConnectionTest {

	@Test
	void testCMConnection() {
		//create a database connection in my database
		CMConnection testConnection = new CMConnection("jdbc:mysql://cs1103.cs.unb.ca:3306/j3zh5", "j3zh5", "rGR45WHX");
	}
	
	@Test
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
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	void testQueryShip() {
		try{
			CMConnection testConnection = new CMConnection("jdbc:mysql://cs1103.cs.unb.ca:3306/j3zh5", "j3zh5", "rGR45WHX");
			ArrayList<Ship> shipList = testConnection.queryShip();
			for(int i = 0; i < shipList.size(); i++) {
				System.out.println(shipList.get(i).toString());
		}
		}catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	void testCreateTrip() {
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
			tb.addPort(dateFormat.parse("2023-11-24"), dateFormat.parse("2023-11-25"), "Pisa", "GMT");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Trip trip1 = testConnection.createTrip(tb);
	}

	@Test
	void testQueryTrip() {
		try{
			CMConnection testConnection = new CMConnection("jdbc:mysql://cs1103.cs.unb.ca:3306/j3zh5", "j3zh5", "rGR45WHX");
			ArrayList<Trip> tripList = testConnection.queryTrip();
			for(int i = 0; i < tripList.size(); i++) {
				System.out.println(tripList.get(i).toString());
		}
		}catch(IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
	}
}
