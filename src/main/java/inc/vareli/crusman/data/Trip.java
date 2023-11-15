package inc.vareli.crusman.data;

import inc.vareli.crusman.data.Ship.RoomType;

import java.sql.Connection;

import java.util.Map;
import java.util.EnumMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Date;
import java.time.Duration;
import java.util.TimeZone;

public class Trip {
	
	private long ID;
	private Ship ship;
	private List<Port> ports;
	private Map<RoomType,Double> roomCosts;
	private double mealCost;
	private double drinkCost;

	public Trip(Connection conn, long ID) {
		//does some stuff with prepared statements to fetch the data from the DB
		//and construct the Trip with the private constructor
	}

	public static Trip fetch(Connection conn, long ID) {
		//same as the public constructor but just kinda looks different, may be
		//some reasons to use this approach?
		return null;
	}

	/*
	 * REALLY good place to use builder pattern
	 */
	private Trip(long ID, Ship ship, double mealCost, double drinkCost,
				 Date startArrival, Date startDeparture, String startLocation,
				 Date destArrival, Date destDeparture, String destLocation,
				 String startZoneId, String destZoneId) {
		this.ID = ID;
		this.ship = ship;
		this.mealCost = mealCost;
		this.drinkCost = drinkCost;
		roomCosts = new EnumMap<RoomType,Double>(RoomType.class);
		ports = new ArrayList<Port>(
						Arrays.asList(new Port[] {
							new Port(startArrival, startDeparture, startLocation, startZoneId),
							new Port(destArrival, destDeparture, destLocation, destZoneId)
						})
					);
	}

	public Trip addPort(Date arrival, Date departure, String location, String zoneId) {
		//always adds newest port just before the destination port
		ports.add(ports.size()-1, new Port(arrival, departure, location, zoneId));
		return this;
	}

	public boolean addPerson(RoomType type) {
		long days = this.getDuration();
		return ship.addPerson(type, (days <= 2) ? 5 : 4);
	}

	public long getDuration() {
		int z = ports.size();
		return Duration.between(
							ports.get(0).arrival.toInstant(),
							ports.get(z).departure.toInstant()
						).toDays();
	}

	/*
	 * Solution to the timezone issue: the timezone is stored with the port
	 * and will be interpreted at the display level (GUI stuff) to show the
	 * correct timezone for the situation.
	 * Calculations and stuff use UTC-0 (unix time)
	 */
	private static class Port { 
		public String location;
		public TimeZone zone;
		public Date arrival;
		public Date departure;

		public Port(Date arrival, Date departure, String location, String zoneId) {
			this.arrival = arrival;
			this.departure = departure;
			this.location = location;
			this.zone = TimeZone.getTimeZone(zoneId);
		}
	}
}
