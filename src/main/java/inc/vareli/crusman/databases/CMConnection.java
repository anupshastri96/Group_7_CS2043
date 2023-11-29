package inc.vareli.crusman.databases;

import inc.vareli.crusman.databases.Ship.*;
import inc.vareli.crusman.databases.Trip.*;

import java.util.List;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.sql.*;

/**
 * A wrapper around java.sql.Connection to encapsulate it for use in CrusMan
 * @author Arhaan Sami 3751940
 */
public class CMConnection {
	private Connection connector;
	/**
	 *Creates a database connection, and sets up the tables to be used in all database operations. If tables already exist, it does not create duplicates.
	 */
	public CMConnection(String url, String loginID, String loginPass) throws IllegalArgumentException { 
		try {
			connector = DriverManager.getConnection(url, loginID, loginPass);
			String shipCreator = "Create table CruiseShip (shipID int unsigned not null primary key, " +
							"interiorRooms int unsigned not null, outsideRooms int unsigned not null, " + 
							"balconyRooms int unsigned not null, suites int unsigned not null)";
			String tripCreator = "Create table Trip (tripID int unsigned not null primary key, " + 
							"shipID int unsigned not null, drinkFees float unsigned not null, " + 
							"mealFees float unsigned not null, " + 
							"foreign key(shipID) references CruiseShip(shipID))";
			String portCreator = "Create table Port (portName varchar(100) not null, " +
							"tripID int unsigned not null, startEndFlag int unsigned not null, " +
							"arrivalDate date not null, departureDate date not null, " + 
							"primary key(portName, tripID), " + 
							"foreign key(tripID) references Trip(tripID))";
			String ticketCreator = "Create table Ticket (ticketID int unsigned not null primary key, " + 
							"tripID int unsigned not null, customerName varchar(255) not null, " + 
							"mealPackageFlag int unsigned not null, drinkPackageFlag int unsigned not null, " + 
							"roomNumber int unsigned not null, foreign key(tripID) references Trip(tripID))";
			String roomCreator = "Create table RoomInfo (tripID int unsigned not null, " + 
							"roomType varchar(100) not null, fees double unsigned not null, " + 
							"occupancy int unsigned not null, " + 
							"primary key(roomType, tripID), foreign key(tripID) references Trip(tripID))";
			String[] createStatements = {shipCreator, tripCreator, portCreator, ticketCreator, roomCreator};
			String[] tableNames = {"CruiseShip", "Trip", "Port", "Ticket", "RoomInfo"};
			Statement stmt = connector.createStatement();
			DatabaseMetaData dbm = connector.getMetaData();
			for(int i = 0; i < createStatements.length; i++) {
				ResultSet tables = dbm.getTables(null, null, tableNames[i], null);
				if(!tables.next()) {
					stmt.executeUpdate(createStatements[i]);
				}
			}
		} catch (SQLException sqle) {
			throw new IllegalArgumentException(sqle.getMessage());
		}
	}

	/*
	 * Creates a ship object and enters its data into the database.
	 * @param roomCounts The numbers of room of each type
	 * @return A ship object with the ID and
	 */
	public Ship createShip(Map<RoomType,Integer> roomCounts) throws IllegalArgumentException {
		String retrieveID = "select shipID from CruiseShip";
		long id = 1000;
		try {
			PreparedStatement retrieveStatement = connector.prepareStatement(retrieveID);
			ResultSet idSet = retrieveStatement.executeQuery();
			while(idSet.next()) {
				id = idSet.getInt("shipID") + 1;
			}
		} catch(SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		
		try {
			String insert = "insert into CruiseShip values (?,?,?,?,?)";
			PreparedStatement insertStatement = connector.prepareStatement(insert);
			insertStatement.setLong(1, id);
			insertStatement.setInt(2, roomCounts.get(RoomType.INTERIOR));
			insertStatement.setInt(3, roomCounts.get(RoomType.OUTSIDE));
			insertStatement.setInt(4, roomCounts.get(RoomType.BALCONY));
			insertStatement.setInt(5, roomCounts.get(RoomType.SUITE));
			int affectedRows = insertStatement.executeUpdate();
		} catch (SQLException sqle) {
			throw new IllegalArgumentException("Invalid inputs for createShip()");
		}
		return new Ship(id, roomCounts);
	}
	
	public List<Ship> queryShip() throws IllegalArgumentException{
		List<Ship> shipList = new ArrayList<Ship>();
		String queryStatement = "select * from CruiseShip";
		try {	
			PreparedStatement retrieveStatement = connector.prepareStatement(queryStatement);
			ResultSet shipSet = retrieveStatement.executeQuery();			
			while(shipSet.next()) {
				EnumMap<RoomType,Integer> roomCounts = new EnumMap<RoomType,Integer>(RoomType.class);
				int id = shipSet.getInt("shipID");
				roomCounts.put(RoomType.INTERIOR, shipSet.getInt("interiorRooms"));
				roomCounts.put(RoomType.OUTSIDE, shipSet.getInt("outsideRooms"));
				roomCounts.put(RoomType.BALCONY, shipSet.getInt("balconyRooms"));
				roomCounts.put(RoomType.SUITE, shipSet.getInt("suites"));
				shipList.add(new Ship(id, roomCounts));
			}
		} catch(SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		return shipList;
	}

	public Trip createTrip(TripBuilder temp) {
		long id = 3000;
		Trip toReturn = temp.build(id);
		String retrieveID = "select tripID from Trip";
		try {
			PreparedStatement retrieveStatement = connector.prepareStatement(retrieveID);
			ResultSet idSet = retrieveStatement.executeQuery();
			while(idSet.next()) {
				id = idSet.getInt("tripID") + 1;
			}
		} catch(SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		try {
			String insert = "insert into Trip values (?,?,?,?,?,?,?,?)";
			PreparedStatement insertStatement = connector.prepareStatement(insert);
			insertStatement.setLong(1, id);
			insertStatement.setLong(2, toReturn.SHIP.ID);
			insertStatement.setDouble(3, toReturn.COSTS.get(Service.DRINKS));
			insertStatement.setDouble(4, toReturn.COSTS.get(Service.MEALS));
			int affectedRows = insertStatement.executeUpdate();
	
			for(int i = 0; i < toReturn.PORTS.size(); i++) {
				insert = "insert into Port values (?,?,?,?)";
				insertStatement = connector.prepareStatement(insert);
				insertStatement.setString(1, toReturn.PORTS.get(i).location);
				insertStatement.setLong(2, id);
				if(i == 0) {
					insertStatement.setInt(3,  0); //startEndFlag: 0 = starting port, 1 = ending port, 2 = other
				}else if(i == toReturn.PORTS.size() - 1) {
					insertStatement.setInt(3, 1);
				}else {
					insertStatement.setInt(3, 2);
				}
				insertStatement.setDate(4, (Date)toReturn.PORTS.get(i).arrival);
				insertStatement.setDate(5, (Date)toReturn.PORTS.get(i).departure);
				affectedRows = insertStatement.executeUpdate();
			}
			for(int i = 0; i<toReturn.SHIP.rooms.length; i++) {
				RoomType currentType = toReturn.SHIP.rooms[i].type;
				insert = "insert into RoomInfo values (?,?,?,?)";
				insertStatement.setString(1, currentType.name());
				insertStatement.setLong(2, id);
				insertStatement.setDouble(3, toReturn.COSTS.get(currentType));
				insertStatement.setInt(4, toReturn.SHIP.getTotalOccupancy(currentType));
				affectedRows = insertStatement.executeUpdate();
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		return toReturn;
	}
	
	public List<Trip> queryTrip(){
		List<Trip> tripList = new ArrayList<Trip>();
		String[] queryStatements = {"select * from Trip", "select * from Port", "select * from RoomInfo"};
		ResultSet[] queryResults = new ResultSet[queryStatements.length];
		try {	
			for(int i = 0; i< queryStatements.length; i++) {
				PreparedStatement retrieveStatement = connector.prepareStatement(queryStatements[i]);
				queryResults[i] = retrieveStatement.executeQuery();			
			}
			while(queryResults[1].next()) {
				
			}
		} catch(SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		return tripList;
	}
	
}
