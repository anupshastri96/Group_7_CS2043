package inc.vareli.crusman.data;

import java.sql.Connection;
import java.util.Map;
import java.util.Iterator;

public class Ship {
	private long ID;
	private  Room[] rooms;

	public Ship(Connection conn, long ID) {
		//sql stuff
		//
	}

	public static Ship fetch(Connection conn, long ID) {
		//see: comments in Trip.java
		//
		return null;
	}

	public Ship(long ID, Map<RoomType,Integer> roomCounts) {

		int size = roomCounts.values().stream().mapToInt(t->t).sum();
		this.ID = ID;
		this.rooms = new Room[size];

		Iterator<RoomType> iter = roomCounts.keySet().iterator();
		int prev = 0;
		do {
			RoomType currentType = iter.next();
			int i;
			for (i = prev; i <  prev + roomCounts.get(currentType); i++) {
				rooms[i] = new Room(currentType);
			}
			prev = i;
		} while (iter.hasNext());
	}

	public boolean addPerson(RoomType roomType, int maxOccupancy) {
		for (Room r : rooms) {
			if (r != null && r.type == roomType && r.count < maxOccupancy) {
				r.count++;
				return true;
			}
		}
		return false;
	}

	private static class Room {
		public RoomType type;
		public int count;

		public Room(RoomType type) {
			this.type = type;
			count = 0;
		}

	}

	public enum RoomType {
		INTERIOR,
		OUTSIDE,
		BALCONY,
		SUITE;
	}
}
