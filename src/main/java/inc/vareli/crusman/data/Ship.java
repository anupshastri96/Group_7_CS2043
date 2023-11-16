package inc.vareli.crusman.data;

import java.sql.SQLException;

import inc.vareli.crusman.databases.CMConnection;

public class Ship {
	private long ID;
	private Room[] rooms;

	public Ship(long ID, int inRooms, int outRooms, int balRooms, int suites) throws SQLException {
		this.ID = ID;
		rooms = new Room[inRooms+outRooms+balRooms+suites];
		for (int i = 0; i < rooms.length; i++) {
			if (i <= inRooms) {// TODO: this can definitely be cleaned up
							   // with some kinda map or something. This is 
							   // CRINGE
				rooms[i] = new Room(RoomType.INTERIOR);
			} else if (i <= outRooms) {
				rooms[i] = new Room(RoomType.OUTSIDE);
			} else if (i <= balRooms) {
				rooms[i] = new Room(RoomType.BALCONY);
			} else if (i <= suites) {
				rooms[i] = new Room(RoomType.SUITE);
			}
		}
		
		CMConnection newShip = new CMConnection("exampleID", "examplePWD");
		newShip.addShip(ID, inRooms, outRooms, balRooms, suites);
	}

	public boolean addPerson(RoomType roomType, int maxOccupancy) {
		for (Room r : rooms) {
			if (r.type == roomType && r.count < maxOccupancy) {
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
	
	public long getID() {
		return ID;
	}
	
	public int getNumRooms() {
		return rooms.length;
	}
}
