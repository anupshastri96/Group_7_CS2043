package inc.vareli.crusman.databases;

import java.util.Scanner;
import java.sql.*;

public class CMConnection{
	private Connection connector;
	
	public CMConnection(String loginID, String loginPass) throws SQLException {
		connector = DriverManager.getConnection
				("jdbc:mysql://cs1103.cs.unb.ca:3306/dbname",
				 loginID,
				 loginPass);
	}
	
	public void addShip(long shipID, int interior, int outside, int balcony, int suites) throws SQLException{
		String insert = "insert into Ship values (?,?,?,?,?)";
		PreparedStatement insertStatement = connector.prepareStatement(insert);
		insertStatement.setLong(1, shipID);
		insertStatement.setInt(2, interior);
		insertStatement.setInt(3, outside);
		insertStatement.setInt(4, balcony);
		insertStatement.setInt(5, suites);
		
		int affectedRows = insertStatement.executeUpdate();
	}
	
	public void addTrip(long tripID, long shipID, String startPort, String endPort, String visitingPorts,
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
