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
 * @author Mart Palamine
 */
public class CruiseShipGUI extends Application {

	//global
	private Stage stage;
	private CMConnection conn;
    private TripBuilder tripBuilder;

	//loggin in
	private TextField loginURLField;
	private TextField loginIDField;
	private TextField loginPassField;
	private Label loginError;

	//browsing
	private ArrayList<String> demo;
	private Text text;
	private int i;
	private Button nextButton;
	private Button prevButton;
	private Button bookButton;
	private String booked;
	private List<Trip> trips;

	//booking
	private Button confirmBookingButton;
	private TextField customerNameField;
	ComboBox<String> mealSelection;
	ComboBox<String> drinkSelection;

	//admin password
	private Label welcomeLabel;

	//creating - trips
	private int numTrips;
	private TextField dateArrivalField;
	private TextField dateDepartureField;
	private TextField locationField;
	private TextField roomCostField;
    private TextField serviceCostField;
	private Button createATripButton;
	private Button createAShipButton;
	private Button createTripButton;
	private Button addPortButton;
	private Button addCostButton;
	private TextField roomCountField;
    private Label warningLabelPort;
	private Label warningLabelCost;
	private Label tripLabel;
	ComboBox<String> timeZone;

	private Button addRoomCountButton;
	private Button addShipButton;
	private Label labelCreateShip;
	private Label costLabel;
    //private Ship selectedShip; --use this for db
    private String selectedShip;
    private RoomType roomTypeSelectedForTrip;
    private RoomType roomTypeSelectedForShip;
    private Service serviceSelectedForTrip;
	private ComboBox<RoomType> listRoom;
	private Map<RoomType, Integer> rooms;
	

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

	public void switchToMainMenuScene (ActionEvent event) {
		Label menuLabel = new Label("Welcome!");

		Button browseButton = new Button("Book Trips");
		browseButton.setPrefWidth(200);
		browseButton.setOnAction(this::switchToBrowseScene);

		createATripButton = new Button("Create a trip");
		createATripButton.setPrefWidth(200);
		createATripButton.setOnAction(this::switchToAdminPassword);

		createAShipButton = new Button("Create a ship");
		createAShipButton.setPrefWidth(200);
		createAShipButton.setOnAction(this::switchToAdminPassword);

		VBox arrangeMenu =  new VBox(10);
		arrangeMenu.getChildren().addAll(menuLabel, 
				browseButton, createATripButton, createAShipButton);

		FlowPane pane = new FlowPane(arrangeMenu);
		pane.setAlignment(Pos.CENTER);

		Scene mainMenuScene = new Scene(pane, 300, 350);
		stage.setScene(mainMenuScene);
		stage.setTitle("Crusman cruise ship application");
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

		if (event.getSource() == createATripButton) {
			adminPasswordField.setOnAction(this::switchToChooseShipScene);

		} else if (event.getSource() == createAShipButton) {
			adminPasswordField.setOnAction(this::switchToCreateShipScene);

		} else if (event.getSource() == confirmBookingButton) {
			welcomeLabel.setText("Waiting for admin to confirm payment");
			adminPasswordField.setText("");
			adminButton.setVisible(true);
			
		}

		VBox arrangement = new VBox(20);
		arrangement.getChildren().addAll(welcomeLabel, adminPasswordField, adminButton,
										returnButton);

		FlowPane pane = new FlowPane(arrangement);
		pane.setAlignment(Pos.CENTER);
		pane.setVgap(10);

		Scene adminPasswordScene = new Scene(pane, 400, 400);
		stage.setScene(adminPasswordScene);
		stage.setTitle("Admin Login Panel");
	}

	public void switchToBrowseScene(ActionEvent event) {
		/* 
		  List<Trip> trips = conn.queryTrip();
		 */
		demo = new ArrayList<>();
		demo.add("Trip1"); 
		demo.add("Trip2"); 
		demo.add("Trip3"); 
		
		//stores index for trips
		i = 0;

		text = new Text(demo.get(i));
		//text = new Text(trips.get(i).toString());

		bookButton = new Button("Book");
		nextButton = new Button("Next");
		prevButton = new Button("Prev");
		Button returnButton = new Button("Return");
		returnButton.setOnAction(this::switchToMainMenuScene);

		Button[] arr = {bookButton, nextButton, prevButton};
		for (int i = 0; i < 3; i++) {
			arr[i].setPrefWidth(120);
			arr[i].setOnAction(this::browseAction);
		}

		HBox arrangeButt = new HBox(10, prevButton, bookButton, nextButton);
		VBox arrange = new VBox(40, text, arrangeButt, returnButton);

		FlowPane pane = new FlowPane(arrange);
		pane.setAlignment(Pos.CENTER);
		Scene browsingScene = new Scene(pane, 500, 550);
		stage.setScene(browsingScene);
		stage.setTitle("Crus, Man!");
	}

	public void browseAction (ActionEvent event)  {
		try {
		if (event.getSource() == nextButton) {
			text.setText(demo.get(++i));
			//text.setText(trips.get(++i).toString());
		}
		else if (event.getSource() == prevButton) {
			text.setText(demo.get(--i));
			//text.setText(trips.get(--1).toString());
		}
		else if (event.getSource() == bookButton) {
			booked = demo.get(i);
			switchToBookingScene(event);
			//booked = trips.get(i).toString();
			//Trip trip = trip.get(i);
		}
		} catch(IndexOutOfBoundsException e) {
			text.setText("None");
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

		ComboBox<RoomType> roomSelection = new ComboBox<RoomType>();		
		for (RoomType roomType : RoomType.values()) {
			roomSelection.getItems().add(roomType);
		}

		HBox arrangeLabels = new HBox(70);
		arrangeLabels.getChildren().addAll(mealLabel, drinkLabel, roomLabel);

		HBox arrangeSelections = new HBox(30);
		arrangeSelections.getChildren().addAll(mealSelection, drinkSelection, roomSelection);

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

	public void switchToCreateTripsScene (ActionEvent event) {
		Label addShipLabel = new Label("Your selected ship:  " + selectedShip);
        //Label addShipLabel = new Label("Your selected ship: " + conn.selectedShip.toString());
		Label portLabel = new Label("Add a port  -  A trip must have at least 2 ports");
        Label titleLabel = new Label("Arrival Date\t         \t" + "    \tDeparture Date"
		+"\t                       Location" +"\t                          \tTime Zone ID");
		costLabel = new Label("Add cost for available room types" +
										" and services in your trip");
        warningLabelPort = new Label();
		warningLabelCost = new Label();
		tripLabel    = new Label();

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

		Button[] arrButt = {createTripButton, addPortButton, addCostButton};
		for(int i = 0; i < arrButt.length; i++) {
			arrButt[i].setPrefWidth(200);
			arrButt[i].setOnAction(this::addTrip);
		}

		TextField[] arr = {dateArrivalField, dateDepartureField, locationField,
						 roomCostField, serviceCostField};
		for (int i = 0; i < arr.length; i++) {
			TextField field = arr[i];
			field.setOnMouseClicked(e -> field.clear());
		}

		timeZone = new ComboBox<>();
		for (String timeZoneIDs : TimeZone.getAvailableIDs()) {
			timeZone.getItems().add(timeZoneIDs);
		}

        ComboBox<RoomType> roomSelection = new ComboBox<RoomType>();
		for (RoomType roomType : RoomType.values()) {
			roomSelection.getItems().add(roomType);
		}
        roomTypeSelectedForTrip = roomSelection.getValue();

        ComboBox<Service> serviceSelection = new ComboBox<Service>();
        for (Service service : Service.values()) {
            serviceSelection.getItems().add(service);
        }
        serviceSelectedForTrip =  serviceSelection.getValue();

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
                 arrangeTripServiceCosts, arrangeTripCostButton, arrangeAddTripButton, returnButton);   

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
		int roomCount = 0;
		roomTypeSelectedForShip = listRoom.getValue();
        try {
			if (event.getSource() == addRoomCountButton) {
				roomCount = Integer.parseInt(roomCountField.getText());
				if (roomCount < 0) {
					throw new IllegalArgumentException("msg");
				}
				rooms = new EnumMap<RoomType, Integer>(RoomType.class);
				rooms.put(roomTypeSelectedForShip, roomCount);
				labelCreateShip.setText("Succesfully added room count");
			}
			else if (event.getSource() == addShipButton) {
				//conn.createShip(rooms);
				labelCreateShip.setText("Successfully added a ship");
			}

        }
        catch(NumberFormatException nfe) {
            labelCreateShip.setText("Please only input integer values");
        }
		catch(NullPointerException ne) {
			labelCreateShip.setText("Invalid, please fill both room type " 
			                + " and count\n or add at least one room count"  
			 			    + " before finalizing ship");
		}
		catch (IllegalArgumentException iae) {
			labelCreateShip.setText("No negative numbers allowed");
		}

	}

	public void addTrip(ActionEvent event) {
		
		if(event.getSource() == addPortButton) 	{
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
			Date arrivalDate = new Date();
			Date departureDate = new Date();

			try 
			{
				arrivalDate = sdf.parse(dateArrivalField.getText());
				departureDate = sdf.parse(dateDepartureField.getText());
				//tripBuilder.addPort(arrivalDate, departureDate, locationField.getText(), timeZone.getValue());
				warningLabelPort.setText("Port Added Succesfully");
				numTrips++;
			}
			catch(ParseException pe) {
				warningLabelPort.setText("Failed to input date(s), please check if format is correct");
			}
			catch(NullPointerException npe) {
				warningLabelPort.setText("Failed to add a port, please fill all boxes before adding.");
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
				//tripBuilder.addCost(roomTypeSelectedForTrip, roomCost);
				//tripBuilder.addCost(serviceSelectedForTrip, serviceCost);
				warningLabelCost.setText("Succesfully added cost");
			}
			catch(NumberFormatException nfe) {
				warningLabelCost.setText("Try again, no alphabetic characters for cost");
			} 
			catch(NullPointerException npe) {
				warningLabelCost.setText("Try again, fill each boxes");
			}   

		}
		else if (event.getSource() == createTripButton) {
			try {
			//conn.createTrip(tripBuilder);
			tripLabel.setText("Succesfully created Trip");
			} catch(NullPointerException e) {
				tripLabel.setText("Invalid, add at least one cost");
			}

		}
	}

    public void switchToChooseShipScene (ActionEvent event) {

        Button button = new Button("Done");
		button.setPrefWidth(200);
        Label label = new Label("Choose a ship");

        //combo box to store ships from db
		/* 
		//List<Ship> shipList = conn.queryShip();
		ComboBox<Ship> cbShip = new ComboBox<>();
		for (Ship ship : shipList) {
		cbShip.getItems().add(ship);
		   }
		   */
		
        ComboBox<String> placeHolder = new ComboBox<>();
        placeHolder.getItems().add("Ship1");
        placeHolder.getItems().add("Ship2");

        //cbShip.setOnAction(e -> selectedShip = cbShip.getValue());
        placeHolder.setOnAction(e -> selectedShip = placeHolder.getValue());

        //tripBuilder = new TripBuilder(selectedShip);

        button.setOnAction(this::switchToCreateTripsScene);
        
        VBox arrange = new VBox(30, label, placeHolder, button);
        FlowPane pane = new FlowPane(arrange);
        pane.setAlignment(Pos.CENTER);
        Scene scene = new Scene(pane, 450, 350);
        stage.setScene(scene);
        stage.setTitle("Choose a ship for the trip");
    }

	public void printTicketToFile (ActionEvent event){

		String ticketContents = "Trip booked:\t" + booked 
							+"\nCustomer Name: " + customerNameField.getText() 
							+"\nMeal Plan: " + mealSelection.getValue()
							+ "\nDrink plan: " + drinkSelection.getValue();

		Path filePath = Path.of("ticket.txt");

		try {
			Files.writeString(filePath, ticketContents, StandardOpenOption.CREATE);
			welcomeLabel.setText("Successfully created ticket");
		}catch(IOException e) {
			welcomeLabel.setText("Could not create ticket");
		}
	}

}
