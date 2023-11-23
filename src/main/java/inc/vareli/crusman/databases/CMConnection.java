package inc.vareli.crusman.databases;

import inc.vareli.crusman.databases.Ship.RoomType;
import inc.vareli.crusman.databases.Trip.TripBuilder;

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
							"shipID int unsigned not null, startDate date not null, " + 
							"endDate date not null, roomFees float unsigned not null, " + 
							"drinkFees float unsigned not null, mealFees float unsigned not null, " + 
							"roomOccupancy int unsigned not null, " + 
							"foreign key(shipID) references CruiseShip(shipID))";
			String portCreator = "Create table Port (portName varchar(100) not null, " +
							"tripID int unsigned not null, startPortFlag int unsigned not null, " + 
							"endPortFlag int unsigned not null, primary key(portName, tripID), " + 
							"foreign key(tripID) references Trip(tripID))";
			String ticketCreator = "Create table Ticket (ticketID int unsigned not null primary key, " + 
							"tripID int unsigned not null, customerName varchar(255) not null, " + 
							"mealPackageFlag int unsigned not null, drinkPackageFlag int unsigned not null, " + 
							"roomNumber int unsigned not null, foreign key(tripID) references Trip(tripID))";
			String[] createStatements = {shipCreator, tripCreator, portCreator, ticketCreator};
			String[] tableNames = {"CruiseShip", "Trip", "Port", "Ticket"};
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

	/*Creates a ship object and enters its data into the database.
	 * @param roomCounts The numbers of room of each type
	 * @return A ship object with the ID and */
	public Ship createShip(Map<RoomType,Integer> roomCounts) throws IllegalArgumentException {
		String retrieveID = "select shipID from CruiseShip";
		int id = 1000;
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
			insertStatement.setInt(1, id);
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
		Trip toReturn = temp.build();
		//try {
		// some database operation using toReturn's data
		//} catch (SQLException sqle) {
		// throw new 
		//	IllegalStateException(sqle, "TripBuilder in createTrip() is not complete.");
		//}
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
	
	private void addTrip(long tripID, long shipID, String startPort, String endPort, String visitingPorts,
						Date startDate, Date endDate, double roomCost, double drinkCost, double mealCost, int totalRooms) throws SQLException{
		String insert = "insert into Trip values (?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement insertStatement = connector.prepareStatement(insert);
		insertStatement.setLong(1, tripID);
		insertStatement.setLong(2, shipID);
		insertStatement.setString(3, startPort);
		insertStatement.setString(4, endPort);
		insertStatement.setString(5, visitingPorts);
		insertStatement.setDate(6, startDate);
		insertStatement.setDate(7, endDate);
		insertStatement.setDouble(8, roomCost);
		insertStatement.setDouble(9, drinkCost);
		insertStatement.setDouble(10, mealCost);
		insertStatement.setInt(11, totalRooms);
	}
}
