package inc.vareli.crusman.databases;

import inc.vareli.crusman.databases.Ship.RoomType;
import inc.vareli.crusman.databases.Trip.TripBuilder;

import java.util.Map;
import java.sql.*;

public class CMConnection {
	private Connection connector;
	
	public CMConnection(String URL, String loginID, String loginPass) {//throws IllegalArgumentException 
		//try {
		//	this.connector = DriverManager.getConnection(URL, loginID, loginPass);
		//catch (SQLException sqle) {
		//	throw new IllegalArgumentException(sqle, sqle.getMessage());
		//}
	}

	//these methods are what will be used to both create
	//new ships and trips and to write them to the db
	//this ensures all data will be saved in the db and not lost
	//they are not implemented because that is arhaan's job
	public Ship createShip(Map<RoomType,Integer> roomCounts) { 
		long ID = 0;//this should come from some database operation
		//try {
		//	some database operation storing this Ship to the db
		//} catch (SQLException sqle) {
		//	throw new IllegalArgumentException(sqle, "Invalid inputs for createShip()");
		//}
		return new Ship(ID, roomCounts);
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
	
	private void addShip(long shipID, int interior, int outside, int balcony, int suites) throws SQLException{
		String insert = "insert into Ship values (?,?,?,?,?)";
		PreparedStatement insertStatement = connector.prepareStatement(insert);
		insertStatement.setLong(1, shipID);
		insertStatement.setInt(2, interior);
		insertStatement.setInt(3, outside);
		insertStatement.setInt(4, balcony);
		insertStatement.setInt(5, suites);
		
		int affectedRows = insertStatement.executeUpdate();
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
