package inc.vareli.crusman.databases;

import java.util.Map;
import java.util.Iterator;

public class Ship {

	private final long ID;
	private Room[] rooms;

	/**
	 * A constructor. Creates a Ship with the specified ID and room counts
	 * Protected so that user can ONLY create a Ship through CMConnection, 
	 * ensuring that everything is written to DB
	 * @param ID - a unique identifier
	 * @param roomCounts - a Map from RoomType to Integer, specifies how many
	 * of each type of room there is
	 */
	protected Ship(long ID, Map<RoomType,Integer> roomCounts) {

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

	/**
	 * Attempts to add a person to a room of the specified type, given that we
	 * can allow up to maxOccupancy people per room
	 * @param roomType - the type of room that we want
	 * @param maxOccupancy - the maximum people we can have in a room
	 * @return if this operation succeeded or not
	 */
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

	/**
	 * An enum of the possible types of rooms. Rooms are a type of cost as well.
	 */
	public enum RoomType implements CostType {
		INTERIOR,
		OUTSIDE,
		BALCONY,
		SUITE;
	}
}
