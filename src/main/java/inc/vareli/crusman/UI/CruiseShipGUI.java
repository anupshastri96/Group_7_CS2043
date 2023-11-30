package inc.vareli.crusman.UI;

import inc.vareli.crusman.databases.*;
import inc.vareli.crusman.databases.Ship.RoomType;
import inc.vareli.crusman.databases.Trip.Service;
import inc.vareli.crusman.databases.Trip.TripBuilder;

import javafx.collections.ObservableList;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ComboBox;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.EnumMap;
import java.util.TimeZone;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * The main entry point to CrusMan, where users will interact with the data and buy tickets etc
 *
 * @author Mart Cesar Palamine
 * Student ID: 3732933
 * Email: MartC.Palamine@unb.ca
 */
public class CruiseShipGUI extends Application {

	//global
	private Stage stage;
	private CMConnection conn;

	//loggin in
	private TextField loginURLField;
	private TextField loginIDField;
	private TextField loginPassField;
	private Label loginError;

	//browsing
	private Text text;
	private int tripIndex;
	private Button nextButton;
	private Button prevButton;
	private Button bookButton;
	//private Trip booked;
	//private List<Trip> trips;

	//temp - browsing
	private ArrayList<String> trips;
	private String booked;

	//booking
	private Button confirmBookingButton;
	private TextField customerNameField;
	ComboBox<String> mealSelection;
	ComboBox<String> drinkSelection;
	ComboBox<RoomType> bookingRoomSelection;

	//admin password
	private Label welcomeLabel;

	//main menu
	private Button goToTripButton;
	private Button goToShipButton;
	private Button updateTicketButton;

	//creating - trips
	private int numTrips;
	private TextField dateArrivalField;
	private TextField dateDepartureField;
	private TextField locationField;
	private TextField roomCostField;
    private TextField serviceCostField;
	private Button createTripButton;
	private Button addPortButton;
	private Button addCostButton;
	private TextField roomCountField;
    private Label warningLabelPort;
	private Label warningLabelCost;
	private Label tripLabel;
	ComboBox<String> timeZone;
	ComboBox<RoomType> roomSelection;
	ComboBox<Service> serviceSelection;
	private TripBuilder tripBeingBuilt;

	//temp
	private String selectedShip;

	//creating - Ships
	private Button addRoomCountButton;
	private Button addShipButton;
	private Label labelCreateShip;
	private Label costLabel; 
	private ComboBox<RoomType> listRoom;
	private ComboBox<Ship> listOfShipsToChoose;
	private Map<RoomType, Integer> roomCount;
	
	public void start(Stage stage) {
		this.stage = stage;
		VBox loginFieldsArrangement = new VBox(20);
							
		Button submit = new Button("Submit");
		submit.setPrefWidth(75);
		submit.setOnAction(this::submitLogin);

		loginError = new Label();
		loginURLField = new TextField("Enter URL");
		loginIDField = new TextField("Enter ID");
		loginPassField = new TextField("Enter Password");

		loginURLField.setOnMouseClicked(e -> loginURLField.clear());
		loginIDField.setOnMouseClicked(e -> loginIDField.clear());
		loginPassField.setOnMouseClicked(e -> loginPassField.clear());

		loginFieldsArrangement.getChildren().addAll(loginURLField, loginIDField,
			       			loginPassField, submit, loginError);

		FlowPane pane = new FlowPane(loginFieldsArrangement);
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(20);
		pane.setVgap(50);

		Scene loginScene = new Scene(pane, 300, 500);
		stage.setScene(loginScene);
		stage.setTitle("Enter Info");
		stage.setResizable(false);
		stage.show();
	}

	public void submitLogin(ActionEvent event) {
		String url = loginURLField.getText();
		String ID = loginIDField.getText();
		String pass = loginPassField.getText();
		try {
			//conn = new CMConnection(url, ID, pass);
			switchToMainMenuScene(event);
		} catch (IllegalArgumentException iae) {
			loginError.setText(iae.getMessage());
		}
	}

	public void switchToMainMenuScene(ActionEvent event) {
		Label menuLabel = new Label("Welcome!");
		Button browseButton = new Button("Book Trips");
		goToTripButton = new Button("Create a trip");
		goToShipButton = new Button("Create a ship");
		updateTicketButton = new Button("Update Ticket Fees & Info");

		Button[] arrButtons = {browseButton, goToShipButton, goToTripButton,
													updateTicketButton};
		for(int i = 0; i < arrButtons.length; i++) {
			arrButtons[i].setPrefWidth(200);
		}

		browseButton.setOnAction(this::switchToBrowseScene);
		goToTripButton.setOnAction(this::switchToAdminPassword);
		goToShipButton.setOnAction(this::switchToAdminPassword);
		updateTicketButton.setOnAction(this::switchToAdminPassword);

		VBox arrangeMenu =  new VBox(10);
		arrangeMenu.getChildren().addAll(menuLabel, 
		browseButton, goToTripButton, goToShipButton, updateTicketButton);

		FlowPane pane = new FlowPane(arrangeMenu);
		pane.setAlignment(Pos.CENTER);

		Scene mainMenuScene = new Scene(pane, 300, 350);
		stage.setScene(mainMenuScene);
		stage.setTitle("Cruise ship booking system & admin manager");
	}

	public void switchToAdminPassword(ActionEvent event) {
		Button adminButton = new Button("Confirm and Print Ticket");
		adminButton.setVisible(false);
		adminButton.setPrefWidth(300);
		adminButton.setOnAction(this::printTicketToFile);

		Button returnButton = new Button("Return");
		returnButton.setOnAction(this::switchToMainMenuScene);
		returnButton.setPrefWidth(300);

		welcomeLabel = new Label("Enter Password");
		TextField adminPasswordField = new TextField("Hit enter to submit");
		adminPasswordField.setPrefWidth(300);
		adminPasswordField.setOnMouseClicked(m -> adminPasswordField.clear());

		if (event.getSource() == goToTripButton) {
			adminPasswordField.setOnAction(this::switchToChooseShipScene);
		} else if (event.getSource() == goToShipButton) {
			adminPasswordField.setOnAction(this::switchToCreateShipScene);
		}else if (event.getSource() == updateTicketButton) {
			adminPasswordField.setOnAction((this::switchToUpdateScene));
		} else if (event.getSource() == confirmBookingButton) {
			welcomeLabel.setText("Waiting for admin to confirm payment");
			adminPasswordField.setText("");
			adminButton.setVisible(true);
		}

		VBox arrangement = new VBox(20);
		arrangement.getChildren().addAll(welcomeLabel, adminPasswordField,
			       				adminButton, returnButton);

		FlowPane pane = new FlowPane(arrangement);
		pane.setAlignment(Pos.CENTER);
		pane.setVgap(10);

		Scene adminPasswordScene = new Scene(pane, 400, 400);
		stage.setScene(adminPasswordScene);
		stage.setTitle("Admin Login Panel");
	}

	public void switchToBrowseScene(ActionEvent event) {
		//List<Trip> trips = conn.queryTrip();
		trips = new ArrayList<>();
		trips.add("trip1");
		trips.add("trip2");

		tripIndex = 0;

		//text = new Text(trips.get(tripIndex).toString());
		text = new Text(trips.get(tripIndex));

		bookButton = new Button("Book");
		nextButton = new Button("Next");
		prevButton = new Button("Prev");
		Button returnButton = new Button("Return");
		returnButton.setOnAction(this::switchToMainMenuScene);

		Button[] setBrowseButtons = {bookButton, nextButton, prevButton};
		for(int i = 0; i < setBrowseButtons.length; i++) {
			setBrowseButtons[i].setPrefWidth(120);
			setBrowseButtons[i].setOnAction(this::browseAction);
		}

		HBox arrangeButt = new HBox(10, prevButton, bookButton, nextButton);
		VBox arrange = new VBox(40, text, arrangeButt, returnButton);

		FlowPane pane = new FlowPane(arrange);
		pane.setAlignment(Pos.CENTER);
		Scene browsingScene = new Scene(pane, 500, 550);
		stage.setScene(browsingScene);
		stage.setTitle("Crus, Man!");
	}

	public void browseAction(ActionEvent event) {
		try {
			if (event.getSource() == nextButton) {
				//text.setText(trips.get(++tripIndex).toString());
				text.setText(trips.get(++tripIndex));
			}
			else if (event.getSource() == prevButton) {
				//text.setText(trips.get(--tripIndex).toString());
				text.setText(trips.get(--tripIndex));
			}
			else if (event.getSource() == bookButton) {
				//booked = trips.get(tripIndex);
				switchToBookingScene(event);
			}
		} catch(IndexOutOfBoundsException e) {
			text.setText("None");
		} catch(NullPointerException e) {
			text.setText("There is no trip to book, browse again");
		}

	}

	public void switchToBookingScene(ActionEvent event) {
		Label mealLabel = new Label("Meal Plan");
		Label drinkLabel = new Label("Drink Plan");
		Label roomLabel = new Label("Room Plan");

		customerNameField = new TextField("Input Customer Name");
		customerNameField.setOnMouseClicked(e -> customerNameField.clear());

		Button returnButton = new Button("Return");
		returnButton.setPrefWidth(300);
		returnButton.setOnAction(this::switchToBrowseScene);

		confirmBookingButton = new Button("Confirm");
		confirmBookingButton.setPrefWidth(300);
		confirmBookingButton.setOnAction(this::switchToAdminPassword);

		mealSelection = new ComboBox<String>();
		mealSelection.getItems().add("Opt In");
		mealSelection.getItems().add("Opt Out");

		drinkSelection = new ComboBox<String>();
		drinkSelection.getItems().add("Opt In");
		drinkSelection.getItems().add("Opt Out");

		bookingRoomSelection = new ComboBox<RoomType>();		
		for(RoomType roomType : RoomType.values()) {
			roomSelection.getItems().add(roomType);
		}

		HBox arrangeLabels = new HBox(70);
		arrangeLabels.getChildren().addAll(mealLabel, drinkLabel, roomLabel);

		HBox arrangeSelections = new HBox(30);
		arrangeSelections.getChildren().addAll(mealSelection, drinkSelection, bookingRoomSelection);

		VBox arrangeButtons = new VBox(20);
		arrangeButtons.getChildren().addAll(customerNameField, 
				confirmBookingButton, returnButton);

		FlowPane pane = new FlowPane(arrangeLabels, arrangeSelections, arrangeButtons);
		pane.setAlignment(Pos.CENTER);
		pane.setHgap(50);
		pane.setVgap(60);

		Scene bookingScene = new Scene(pane, 400, 500);
		stage.setScene(bookingScene);
		stage.setTitle("Print ticket");
	}

	public void switchToCreateTripsScene(ActionEvent event) {
		//Label addShipLabel = new Label("Your selected ship: " +
			       			//listOfShipsToChoose.getValue().toString());
		Label addShipLabel = new Label("Selected ship: " + selectedShip);
		Label portLabel = new Label("Add a port  -  A trip must have at least 2 ports");
		Label titleLabel = new Label("Arrival Date\t\t\tDeparture Date\tLocation\t\tTime Zone ID");
		costLabel = new Label("Add cost for available room types and services in your trip");
		warningLabelPort = new Label();
		warningLabelCost = new Label();
		tripLabel = new Label();
		dateArrivalField = new TextField("dd-MM-yyyy hh:mm");
		dateDepartureField = new TextField("dd-MM-yyyy hh:mm");
		locationField = new TextField("location");
		roomCostField = new TextField("Room Cost");
		serviceCostField = new TextField("Service Cost");
		createTripButton = new Button("Add trip");
		createTripButton.setVisible(false);
		addPortButton = new Button("Add Port");
		addCostButton = new Button("Add Cost");
		Button returnButton = new Button("Return");
		returnButton.setOnAction(this::switchToMainMenuScene);
		returnButton.setPrefWidth(200);

		Button[] setTripButtons = {createTripButton, addPortButton, addCostButton};
		for(int i = 0; i < setTripButtons.length; i++) {
			setTripButtons[i].setPrefWidth(200);
			setTripButtons[i].setOnAction(this::addTrip);
		}

		TextField[] setTextFields = {dateArrivalField, dateDepartureField, locationField,
						 roomCostField, serviceCostField};
		for(int i = 0; i < setTextFields.length; i++) {
			TextField field = setTextFields[i];
			setTextFields[i].setOnMouseClicked(e -> field.clear());
		}

		timeZone = new ComboBox<>();
		for(String timeZoneIDs : TimeZone.getAvailableIDs()) {
			timeZone.getItems().add(timeZoneIDs);
		}

		roomSelection = new ComboBox<RoomType>();
		for(RoomType roomType : RoomType.values()) {
			roomSelection.getItems().add(roomType);
		}

		serviceSelection = new ComboBox<Service>();
		for(Service service : Service.values()) {
		    serviceSelection.getItems().add(service);
		}
		HBox arrangeTripPortButton = new HBox(10, addPortButton, warningLabelPort);
		HBox arrangeTripPortInfo = new HBox(20, dateArrivalField, dateDepartureField, locationField,
						    timeZone);
		HBox arrangeTripRoomCosts = new HBox(20, roomSelection, roomCostField);
		HBox arrangeTripServiceCosts = new HBox(20, serviceSelection, serviceCostField);
		HBox arrangeTripCostButton = new HBox(10, addCostButton, warningLabelCost);
		HBox arrangeAddTripButton = new HBox(10, createTripButton, tripLabel);
		VBox arrangeTripVertical = new VBox(20);

		arrangeTripVertical.getChildren().addAll(addShipLabel, portLabel, titleLabel,
				arrangeTripPortInfo, arrangeTripPortButton, costLabel, arrangeTripRoomCosts,
				arrangeTripServiceCosts, arrangeTripCostButton,
			       	arrangeAddTripButton, returnButton);   

		FlowPane pane = new FlowPane(arrangeTripVertical);
		pane.setAlignment(Pos.CENTER);

		Scene createTripsScene = new Scene(pane, 800, 600);
		stage.setScene(createTripsScene);
		stage.setTitle("Add a trip");
	}

	public void switchToCreateShipScene(ActionEvent event) { 
		labelCreateShip = new Label("Choose the number of rooms available for"+
						 "\neach room type, finalize ship once complete");
		labelCreateShip.setMaxSize(500, 500);

		roomCountField = new TextField("Number Of Rooms");
		roomCountField.setMaxSize(300, 20);
		roomCountField.setOnMouseClicked(e -> roomCountField.clear());

		addRoomCountButton = new Button("Add room count");
		addShipButton = new Button("Finalize Ship Info");
		Button returnButton = new Button("Return");

		addRoomCountButton.setPrefWidth(300);
		addShipButton.setPrefWidth(300);
		returnButton.setPrefWidth(300);

		addRoomCountButton.setOnAction(this::addShip);
		addShipButton.setOnAction(this::addShip);
		returnButton.setOnAction(this::switchToMainMenuScene);

		listRoom = new ComboBox<>();
		for (RoomType roomType : RoomType.values()) {
			listRoom.getItems().add(roomType);
		}

		VBox arrangeText = new VBox(20, labelCreateShip, listRoom, roomCountField,
			 addRoomCountButton, addShipButton, returnButton);

		FlowPane pane = new FlowPane(arrangeText);
		pane.setAlignment(Pos.CENTER);

		Scene createShipScene = new Scene(pane, 500, 400);
		stage.setScene(createShipScene);
		stage.setTitle("Create ship");
	}

	public void addShip(ActionEvent event) {
		int parsedRoomCount = 0;
		try {
			if (event.getSource() == addRoomCountButton) {
				parsedRoomCount = Integer.parseInt(roomCountField.getText());
				if (parsedRoomCount < 0) {
					throw new IllegalArgumentException();
				}
				roomCount = new EnumMap<RoomType, Integer>(RoomType.class);
				roomCount.put(listRoom.getValue(), parsedRoomCount);
				labelCreateShip.setText("Succesfully added room count");
			} else if (event.getSource() == addShipButton) {
				//conn.createShip(roomCount);
				labelCreateShip.setText("Successfully added a ship");
			}
		} catch (NumberFormatException nfe) {
			    labelCreateShip.setText("Please only input integer values");
		} catch (NullPointerException ne) {
			labelCreateShip.setText("Invalid, please fill both room type " 
					+ " and count\n or add at least one room count"  
						    + " before finalizing ship");
		} catch (IllegalArgumentException iae) {
			labelCreateShip.setText("No negative numbers allowed");
		}
	}

	public void addTrip(ActionEvent event) {
		if (event.getSource() == addPortButton) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
			Date arrivalDate = new Date();
			Date departureDate = new Date();
			try {
				arrivalDate = sdf.parse(dateArrivalField.getText());
				departureDate = sdf.parse(dateDepartureField.getText());
				//tripBeingBuilt.addPort(arrivalDate, departureDate, 
						//locationField.getText(), timeZone.getValue());
				warningLabelPort.setText("Port Added Succesfully");
				numTrips++;
			} catch(ParseException pe) {
				warningLabelPort.setText(
						"Failed to input date(s), please check if format is correct");
			} catch(NullPointerException npe) {
				warningLabelPort.setText(
						"Failed to add a port, please fill all boxes before adding.");
			}
			if (numTrips >= 2)
				createTripButton.setVisible(true);
		}

		else if (event.getSource() == addCostButton) {
			double roomCost = 0;
			double serviceCost = 0;
			try {
				roomCost = Double.parseDouble(roomCostField.getText());
				serviceCost = Double.parseDouble(serviceCostField.getText());
				//tripBeingBuilt.addCost(roomSelection.getValue(), roomCost);
				//tripBeingBuilt.addCost(serviceSelection.getValue(), serviceCost);
				warningLabelCost.setText("Succesfully added cost!");
			}
			catch(NumberFormatException nfe) {
				warningLabelCost.setText("Try again, no alphabetic characters for cost.");
			} 
			catch(NullPointerException npe) {
				warningLabelCost.setText("Try again, fill each box.");
			}   
		}
		else if (event.getSource() == createTripButton) {
			try {
				//conn.createTrip(tripBeingBuilt);
				tripLabel.setText("Succesfully created Trip");
			} catch(NullPointerException e) {
				tripLabel.setText("Invalid, add at least one cost");
			}
		}
	}

    public void switchToChooseShipScene(ActionEvent event) {
        Button button = new Button("Done");
		button.setPrefWidth(200);
        Label label = new Label("Choose a ship");
		/* 
		List<Ship> shipList = conn.queryShip();
		listOfShipsToChoose = new ComboBox<>();
		for(Ship ship : shipList) {
		listOfShipsToChoose.getItems().add(ship);
	}
		*/

        //listOfShipsToChoose.setOnAction(e -> tripBeingBuilt = new TripBuilder(listOfShipsToChoose.getValue()));

		ComboBox<String> listOfShipsToChoose = new ComboBox<>();
        listOfShipsToChoose.getItems().add("Ship1");
        listOfShipsToChoose.getItems().add("Ship2");

		listOfShipsToChoose.setOnAction(e -> selectedShip = listOfShipsToChoose.getValue());
													

        button.setOnAction(this::switchToCreateTripsScene);
        
        VBox arrange = new VBox(30, label, listOfShipsToChoose, button);
        FlowPane pane = new FlowPane(arrange);
        pane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(pane, 450, 350);
        stage.setScene(scene);
        stage.setTitle("Choose a ship for the trip");
    }

	public void printTicketToFile(ActionEvent event) {
		try {
			String ticketContents = "Trip booked:\t" + booked 
							+"\nCustomer Name: " + customerNameField.getText() 
							+"\nMeal Plan: " + mealSelection.getValue()
							+ "\nDrink plan: " + drinkSelection.getValue();
			/* 				
			String ticketContents = conn.bookTrip(booked, customerNameField.getText(),
							mealSelection.getText().equals("Opt In"),
							drinkSelection.getText().equals("Opt In"),
							bookingRoomSelection.getvalue());
			*/
			Path filePath = Path.of("ticket.txt");
			Files.writeString(filePath, ticketContents, StandardOpenOption.CREATE);
			welcomeLabel.setText("Successfully created ticket");
		}catch (Exception e) {
			welcomeLabel.setText("Error: Please fill all boxes in booking");
		}
	}

    public void switchToUpdateScene(ActionEvent Event) {

        Label updateTicketFeeLabel = new Label("Update the ticket fee for a trip");
        Label updateTicketLabel = new Label ("Update a customer ticket");

        //updating ticket fees for trips
        TextField tripIDField = new TextField("Input Trip ID"); //global
        TextField serviceCostUpdateField = new TextField("Input new cost for service"); //global    
        TextField roomTypeCostUpdateField = new TextField("Input new cost for room type");      //global
        Button ticketFeeButton = new Button("Update ticket fee");
        Label statusLabelTicketFee = new Label();//needs to be global

        ComboBox<Service> serviceSelectionUpdate = new ComboBox<>(); //global
        for (Service serviceAdd : Service.values()) {
            serviceSelectionUpdate.getItems().add(serviceAdd);
        }

        ComboBox<RoomType> roomTypeSelectionUpdate = new ComboBox<>(); //global
        for (RoomType roomTypeAdd : RoomType.values()) {
            roomTypeSelectionUpdate.getItems().add(roomTypeAdd);
            }

        //update customer name - update ticket
        TextField updateCustomerNameField = new TextField("Input name here"); //global
        TextField ticketIDField = new TextField("Ticket ID required");  //global

        //update package - update ticket
        ComboBox<Service> updatePackageServiceSelection = new ComboBox<>(); //global
        for (Service serviceAdd : Service.values()) {
            updatePackageServiceSelection.getItems().add(serviceAdd);
        }
        
        ComboBox<String> updateServiceSelection = new ComboBox<>(); //global
        updateServiceSelection.getItems().add("Opt In");
        updateServiceSelection.getItems().add("Opt Out");

        Button updateTicketButton = new Button("Finalize update for customer ticket");
        updateTicketButton.setPrefWidth(200);

        Label customerTicketLabel = new Label(); //global

        //update ticket fee arrangement
        HBox arrangeServiceUpdates = new HBox(10, serviceSelectionUpdate, serviceCostUpdateField);
        HBox arrangeRoomUpdates = new HBox(10, roomTypeSelectionUpdate, roomTypeCostUpdateField);
        HBox arrangeTicketFeeButt = new HBox(10, ticketFeeButton, statusLabelTicketFee);
        VBox arrangeVerticalTicketFee = new VBox(20, updateTicketFeeLabel, tripIDField, 
								arrangeServiceUpdates, arrangeRoomUpdates, arrangeTicketFeeButt);

        //update customer ticket arrangement
        HBox arrangeServiceCustomer = new HBox(10, updatePackageServiceSelection, updateServiceSelection);
        HBox arrangeCustomerButton = new HBox(updateTicketButton, customerTicketLabel);
        VBox arrangeCustomer = new VBox(20, arrangeVerticalTicketFee, updateTicketLabel, ticketIDField, 
											arrangeServiceCustomer, arrangeCustomerButton);

        FlowPane pane = new FlowPane(arrangeCustomer);
        pane.setAlignment(Pos.CENTER);

        Scene scene = new Scene(pane, 500, 500);
        stage.setScene(scene);
        stage.setTitle("Ticket management page");
        
    }
		
}
