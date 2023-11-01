package inc.vareli.crusman;

import inc.vareli.crusman.Ship.RoomType;

import java.util.Map;
import java.util.EnumMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.time.LocalDate;
import java.time.Period;

public class Trip {
	
	private long ID;
	private Ship ship;
	private List<Port> ports;
	private Map<RoomType,Double> roomCosts;
	private double mealCost;
	private double drinkCost;

	public Trip(long ID, Ship ship, double mealCost, double drinkCost,
			    LocalDate startArrival, LocalDate startDeparture,
				String startLocation, LocalDate destArrival,
				LocalDate destDeparture, String destLocation) {
		this.ID = ID;
		this.ship = ship;
		this.mealCost = mealCost;
		this.drinkCost = drinkCost;
		roomCosts = new EnumMap<RoomType,Double>(RoomType.class);
		Port[] tmpPorts = {new Port(startArrival, startDeparture, startLocation),
			               new Port(destArrival, destDeparture, destLocation)};
		ports = new ArrayList<Port>(Arrays.asList(tmpPorts));
	}

	public Trip addPort(LocalDate arrival, LocalDate departure, String location) {
		//always adds newest port just before the destination port
		ports.add(ports.size()-1, new Port(arrival, departure, location));
		return this;
	}

	public boolean addPerson(RoomType type) {
		int days = this.getDuration().getDays();
		int maxRoomCap = (days <= 2) ? 5 : 4;//so you get the idea
		return ship.addPerson(type, maxRoomCap);
	}

	public Period getDuration() {
		return Period.between(ports.get(0).arrival,
		                      ports.get(ports.size()).departure);
	}
	/*
	 * LocalDate cannot handle timezones. The way I propose we handle this
	 * is by having all the dates be normalized to the timezone of the HQ
	 * like where CrusMan is being ran.
	 * This may be unintuitize though, if we want to display to users when
	 * they will arrive at their destination
	 */
	private static class Port { 
		public LocalDate arrival;
		public LocalDate departure;
		public String location;

		public Port(LocalDate arrival, LocalDate departure, String location) {
			this.arrival = arrival;
			this.departure = departure;
			this.location = location;
		}
	}
}
