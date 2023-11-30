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
	 Creates a database connection, and sets up the tables to be used in all database operations. 
	 If tables already exist, it does not create duplicates.
	 */
	public CMConnection(String url, String loginID, String loginPass) throws IllegalArgumentException { 
		try {
			connector = DriverManager.getConnection(url, loginID, loginPass);
			String shipCreator = "Create table CruiseShip (" +
							"shipID int unsigned not null primary key, " +
							"interiorRooms int unsigned not null, " + 
							"outsideRooms int unsigned not null, " + 
							"balconyRooms int unsigned not null, " + 
							"suites int unsigned not null)";
			String tripCreator = "Create table Trip (" + 
							"tripID int unsigned not null primary key, " + 
							"shipID int unsigned not null, " + 
							"drinkFees float unsigned not null, " + 
							"mealFees float unsigned not null, " + 
							"foreign key(shipID) references CruiseShip(shipID))";
			String portCreator = "Create table Port (" +
							"portName varchar(100) not null, " +
							"tripID int unsigned not null, " + 
							"startEndFlag int unsigned not null, " +
							"arrivalDate date, departureDate date, " + 
							"primary key(portName, tripID), " + 
							"foreign key(tripID) references Trip(tripID))";
			String ticketCreator = "Create table Ticket (" + 
							"ticketID int unsigned not null primary key, " + 
							"tripID int unsigned not null, " + 
							"customerName varchar(255) not null, " + 
							"mealPackageFlag int unsigned not null, " + 
							"drinkPackageFlag int unsigned not null, " + 
							"roomNumber int unsigned not null, " + 
							"foreign key(tripID) references Trip(tripID))";
			String roomCreator = "Create table RoomInfo (" + 
							"roomType varchar(100) not null, " + 
							"tripID int unsigned not null, " + 
							"fees double unsigned not null, " + 
							"occupancy int unsigned not null, " + 
							"primary key(roomType, tripID), " + 
							"foreign key(tripID) references Trip(tripID))";
			String[] createStatements = {shipCreator, tripCreator, 
										portCreator, ticketCreator, roomCreator};
			String[] tableNames = {"CruiseShip", "Trip", 
										"Port", "Ticket", "RoomInfo"};
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
			PreparedStatement retrieveStatement = 
								connector.prepareStatement(retrieveID);
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
			PreparedStatement retrieveStatement = 
								connector.prepareStatement(queryStatement);
			ResultSet shipSet = retrieveStatement.executeQuery();			
			while(shipSet.next()) {
				EnumMap<RoomType,Integer> roomCounts = 
									new EnumMap<RoomType,Integer>(RoomType.class);
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
		int tripID = 3000;
		String retrieveID = "select MAX(tripID) as maxTripID from Trip";
		try {
			PreparedStatement retrieveStatement = 
								connector.prepareStatement(retrieveID);
			ResultSet idSet = retrieveStatement.executeQuery();
			if(idSet.next()) {
				if(idSet.getInt("maxTripID") != 0){
					tripID = idSet.getInt("maxTripID") + 1;
				}
			}
		} catch(SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		Trip toReturn = temp.build(tripID);
		try {
			String insert = "insert into Trip values (?,?,?,?)";
			PreparedStatement insertStatement = 
								connector.prepareStatement(insert);
			insertStatement.setInt(1, tripID);
			insertStatement.setInt(2, toReturn.SHIP.ID);
			insertStatement.setDouble(3, toReturn.COSTS.get(Service.DRINKS));
			insertStatement.setDouble(4, toReturn.COSTS.get(Service.MEALS));
			int affectedRows = insertStatement.executeUpdate();
			
			for(int i = 0; i < toReturn.PORTS.size(); i++) {
				insert = "insert into Port values (?,?,?,?,?)";
				insertStatement = connector.prepareStatement(insert);
				insertStatement.setString(1, toReturn.PORTS.get(i).location);
				insertStatement.setInt(2, tripID);
				if(i == 0) { 
					//startEndFlag: 0 = starting port, 1 = ending port, 2 = other
					insertStatement.setInt(3,  0); 
				}else if(i == toReturn.PORTS.size() - 1) {
					insertStatement.setInt(3, 1);
				}else {
					insertStatement.setInt(3, 2);
				}
				insertStatement.setDate(4, new java.sql.Date(
									toReturn.PORTS.get(i).arrival.getTime()));
				insertStatement.setDate(5, new java.sql.Date(
									toReturn.PORTS.get(i).departure.getTime()));
				affectedRows = insertStatement.executeUpdate();
			}

			for(RoomType currentType : RoomType.values()){
				insert = "insert into RoomInfo values (?,?,?,?)";
				insertStatement = connector.prepareStatement(insert);
				insertStatement.setString(1, currentType.name());
				insertStatement.setInt(2, tripID);
				insertStatement.setDouble(3, toReturn.COSTS.get(currentType));
				insertStatement.setInt(4, toReturn.SHIP.getTotalOccupancy(currentType));
				affectedRows = insertStatement.executeUpdate();
			}
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		return toReturn;
	}
	

	public List<Trip> queryTrip(){
		List<Trip> tripList = new ArrayList<Trip>();
		int tripID;
		try {
			String retrieveTripDetails = "select * from Trip";	
			PreparedStatement tripStatement = 
								connector.prepareStatement(retrieveTripDetails);
			ResultSet tripSet = tripStatement.executeQuery();
			while(tripSet.next()) {
				tripID = tripSet.getInt("tripID");
				int shipId = tripSet.getInt("shipID");
				double drinkFee = tripSet.getDouble("drinkFees");
				double mealFee = tripSet.getDouble("mealFees");

				String retrieveShipDetails = "select CruiseShip.*, TripID from " + 
										"CruiseShip natural join Trip " + 
										"where TripID = " + tripID;
				PreparedStatement shipStatement = 
										connector.prepareStatement(retrieveShipDetails);
				ResultSet shipSet = shipStatement.executeQuery();
				EnumMap<RoomType,Integer> roomCounts = 
										new EnumMap<RoomType,Integer>(RoomType.class);
				while(shipSet.next()){
					roomCounts.put(RoomType.INTERIOR, shipSet.getInt("interiorRooms"));
					roomCounts.put(RoomType.OUTSIDE, shipSet.getInt("outsideRooms"));
					roomCounts.put(RoomType.BALCONY, shipSet.getInt("balconyRooms"));
					roomCounts.put(RoomType.SUITE, shipSet.getInt("suites"));
				}

				TripBuilder builder = new TripBuilder(new Ship(shipId, roomCounts));
				String retrieveRoomDetails = "select * from RoomInfo " + 
										"where TripID = " + tripID;
				PreparedStatement roomStatement = 
										connector.prepareStatement(retrieveRoomDetails);
				ResultSet roomSet = roomStatement.executeQuery();
				while(roomSet.next()){
					builder.addCost(RoomType.valueOf(roomSet.getString("RoomType")),
									roomSet.getInt("fees"));
				}

				String retrievePortDetails = "select * from Port where TripID = " +
										tripID + " order by departureDate";
				PreparedStatement portStatement = 
										connector.prepareStatement(retrievePortDetails);
				ResultSet portSet = portStatement.executeQuery();
				while(portSet.next()){
					builder.addPort(new java.util.Date(
								portSet.getDate("arrivalDate").getTime()),
								new java.util.Date(
								portSet.getDate("departureDate").getTime()), 
								portSet.getString("portName"), 
								portSet.getString("portName"));
				}
				builder.addCost(Service.MEALS, mealFee);
				builder.addCost(Service.DRINKS, drinkFee);
				
				tripList.add(builder.build(tripID));
			}
		} catch(SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		return tripList;
	}

	public String bookTrip(Trip tripIn, String customerName, boolean mealSelect, 
						boolean drinkSelect, RoomType roomSelect){
		String toReturn;
		int ticketID = 5000;
		int roomNumber = tripIn.addPerson(roomSelect);
		String retrieveID = "select MAX(TicketID) as maxTicketID from Ticket";
		try {
			PreparedStatement retrieveStatement = 
											connector.prepareStatement(retrieveID);
			ResultSet idSet = retrieveStatement.executeQuery();
			if(idSet.next()) {
				if(idSet.getInt("maxTicketID") != 0){
					ticketID = idSet.getInt("maxTicketID") + 1;
				}
			}
		} catch(SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		try{
			String createTicket = "insert into Ticket values(?, ?, ?, ?, ?, ?)";
			PreparedStatement insertStatement = 
										connector.prepareStatement(createTicket);
			insertStatement.setInt(1, ticketID);
			insertStatement.setInt(2, tripIn.ID);
			insertStatement.setString(3, customerName);
			if(mealSelect){insertStatement.setInt(4, 1);}
			else{insertStatement.setInt(4, 0);}
			if(drinkSelect){insertStatement.setInt(5, 1);}
			else{insertStatement.setInt(5, 0);}
			insertStatement.setInt(6, roomNumber);
			toReturn = "Ticket ID: " + ticketID + "\nTrip ID: " + tripIn.ID +
						"\nRoom Number: " + roomNumber +
						"\nCustomer Name: " + customerName + 
						"\nMeal Package Selected: " + ((mealSelect) ? "Yes" : "No") +
						"\nDrink Package Selected: " + ((drinkSelect) ? "Yes" : "No");
			int affectedRows = insertStatement.executeUpdate();
		}catch(SQLException e){
			throw new IllegalArgumentException(e.getMessage());
		}
      return toReturn;
      }


	public void updateFees(int tripID, CostType type, double amount) {
		try {
			// Update the fees in the Trip table
			String updateStatement = "UPDATE Trip SET " +
					((type == Service.DRINKS) ? "drinkFees" : "mealFees") + " = ? " +
					"WHERE tripID = ?";

			PreparedStatement updateFeesStatement = connector.prepareStatement(updateStatement);
			updateFeesStatement.setDouble(1, amount);
			updateFeesStatement.setInt(2, tripID);

			int affectedRows = updateFeesStatement.executeUpdate();

			if (affectedRows == 0) {
				// Handle the case where no rows were updated (perhaps tripID does not exist)
				System.out.println("No trip found with ID: " + tripID);
			} else {
				System.out.println("Fees updated successfully for Trip ID: " + tripID);
			}
			}
			catch (SQLException e) {
			// Handle SQL exceptions
			throw new IllegalArgumentException(e.getMessage());
		}
	
   
	}

       public void updateCustomerName(int ticketID, String name) {
        try {
            String updateStatement = "UPDATE Ticket SET customerName = ? WHERE ticketID = ?";

            PreparedStatement updateCustomerNameStatement = connector.prepareStatement(updateStatement);
            updateCustomerNameStatement.setString(1, name);
            updateCustomerNameStatement.setInt(2, ticketID);

            int affectedRows = updateCustomerNameStatement.executeUpdate();

            System.out.println((affectedRows > 0) ? "Customer name updated successfully for Ticket ID: " + ticketID :
                                                    "No ticket found with ID: " + ticketID);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    
       public void updatePackageSelection(int ticketID, Service chosenService, boolean selection) {
        try {
            String columnName = (chosenService == Service.DRINKS) ? "drinkPackageFlag" : "mealPackageFlag";
            String updateStatement = "UPDATE Ticket SET " + columnName + " = ? WHERE ticketID = ?";

            PreparedStatement updatePackageSelectionStatement = connector.prepareStatement(updateStatement);
            updatePackageSelectionStatement.setInt(1, (selection ? 1 : 0));
            updatePackageSelectionStatement.setInt(2, ticketID);

            int affectedRows = updatePackageSelectionStatement.executeUpdate();

            System.out.println((affectedRows > 0) ? chosenService + " package selection updated successfully for Ticket ID: " + ticketID :
                                                    "No ticket found with ID: " + ticketID);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}

