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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ComboBox;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.EnumMap;
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

	//admin password
	private Label welcomeLabel;

	//creating - trips
	private int numTrips;
	private int forArrayInTrip;
	private TextField dateArrivalField;
	private TextField dateDepartureField;
	private TextField locationField;
	private TextField zoneIdField;
	private TextField roomCostField;
    private TextField serviceCostField;
	private Button createATripButton;
	private Button createAShipButton;
	private Button createTripButton;
	private TextField roomCountField;
    private Label warningLabel;

	private Label labelCreateShip;
	private Label costLabel;
    //private Ship selectedShip;
    private String selectedShip;
    private RoomType roomTypeSelectedForTrip;
    private RoomType roomTypeSelectedForShip;
    private Service serviceSelectedForTrip;
	private ComboBox<RoomType> listRoom;
	private ComboBox<String> drinkSelection;
	private Map<RoomType, Integer> rooms;


	public void start(Stage stage) {
		this.stage = stage;
		VBox loginFieldsArrangement = new VBox(20);
							
		Button submit = new Button("Submit");
		submit.setPrefWidth(75);
		submit.setOnAction(this::submitLogin);

		loginError = new Label("Error Label");
		loginURLField = new TextField("Enter URL");
		loginIDField = new TextField("Enter ID");
		loginPassField = new TextField("Enter Password");

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
		browseButton.setPrefWidth(90);
		browseButton.setOnAction(this::switchToBrowseScene);

		createATripButton = new Button("Create a trip");
		createATripButton.setPrefWidth(90);
		createATripButton.setOnAction(this::switchToAdminPassword);

		createAShipButton = new Button("Create a ship");
		createAShipButton.setPrefWidth(90);
		createAShipButton.setOnAction(this::switchToAdminPassword);

		VBox arrangeMenu =  new VBox(10);
		arrangeMenu.getChildren().addAll(menuLabel, 
				browseButton, createATripButton, createAShipButton);

		FlowPane pane = new FlowPane(arrangeMenu);
		pane.setAlignment(Pos.CENTER);

		Scene mainMenuScene = new Scene(pane, 250, 300);
		stage.setScene(mainMenuScene);
		stage.setTitle("Crusman cruise ship application");
	}

	public void switchToAdminPassword(ActionEvent event) {
		Button adminButton = new Button("Confirm and Print Ticket");
		adminButton.setVisible(false);
		adminButton.setOnAction(this::printTicketToFile);

		welcomeLabel = new Label("Enter Password");
		TextField adminPasswordField = new TextField("Hit enter to submit");
		adminPasswordField.setOnMouseClicked(m -> adminPasswordField.clear());

		if (event.getSource() == createATripButton) {
			adminPasswordField.setOnAction(this::switchToChooseShipScene);

		} else if (event.getSource() == createAShipButton) {
			adminPasswordField.setOnAction(this::switchToCreateShipScene);

		} else if (event.getSource() == confirmBookingButton) {
			welcomeLabel.setText("Waiting for admin to confirm payment");
			adminPasswordField.setText("");
			adminButton.setVisible(true);
			//TODO - actually handle password !
		}

		VBox arrangement = new VBox(20);
		arrangement.getChildren().addAll(welcomeLabel, adminPasswordField, adminButton);

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

		Button[] arr = {bookButton, nextButton, prevButton};
		for (int i = 0; i < 3; i++) {
			arr[i].setPrefWidth(120);
			arr[i].setOnAction(this::browseAction);
		}

		HBox arrangeButt = new HBox(10, prevButton, bookButton, nextButton);
		VBox arrange = new VBox(40, text, arrangeButt);

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

		ComboBox<String> mealSelection = new ComboBox<String>();
		mealSelection.getItems().add("Opt In");
		mealSelection.getItems().add("Opt Out");

		ComboBox<String> drinkSelection = new ComboBox<String>();
		drinkSelection.getItems().add("Opt In");
		drinkSelection.getItems().add("Opt Out");

		ComboBox<RoomType> roomSelection = new ComboBox<RoomType>();		
		for (RoomType roomType : RoomType.values()) {
			roomSelection.getItems().add(roomType);
		}

		HBox arrangeLabels = new HBox(70);
		arrangeLabels.getChildren().addAll(mealLabel, roomLabel, drinkLabel);

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
        //Label addShipLabel = new Label("Your selected ship: " + selectedShip.ToString());
		Label portLabel = new Label("Add a port  -  A trip must have at least 2 ports");
        Label titleLabel = new Label("Arrival Date\t         \t" + "  \tDeparture Date");
		costLabel = new Label("Add cost for each available room type" +
										" and services in your trip");
        warningLabel = new Label();

		dateArrivalField = new TextField("dd-MM-yyyy");
        dateDepartureField = new TextField("dd-MM-yyyy");
        locationField = new TextField("location");
        zoneIdField = new TextField("zone id");
        roomCostField = new TextField("Room Cost");
        serviceCostField = new TextField("Service Cost");

        createTripButton = new Button("Create Trip");
		createTripButton.setVisible(false);
        Button addPortButton = new Button("Add Port");
        Button addCostButton = new Button("Add Cost");

		createTripButton.setOnAction(e -> conn.createTrip(tripBuilder));

        addPortButton.setOnAction(this::addPort);
        addCostButton.setOnAction(this::addCost);

		/* 
		dateArrivalField.setOnMouseClicked(e -> dateArrivalField.clear());
        dateDepartureField.setOnMouseClicked(e -> dateDepartureField.clear());
        locationField.setOnMouseClicked(e -> locationField.clear());
        zoneIdField.setOnMouseClicked(e -> zoneIdField.clear());
        roomCostField.setOnMouseClicked(e -> roomCostField.clear());
        serviceCostField.setOnMouseClicked(e -> serviceCostField.clear());
        */

		TextField[] arrTextFields = {dateArrivalField, dateDepartureField, locationField,
											zoneIdField, roomCostField, serviceCostField};

		for (forArrayInTrip = 0; forArrayInTrip < arrTextFields.length; forArrayInTrip++) {
			arrTextFields[forArrayInTrip].setOnMouseClicked(e -> arrTextFields[forArrayInTrip].clear());
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

		HBox arrangeTripButton = new HBox(10, addPortButton, warningLabel);
		HBox arrangeTripPortInfo = new HBox(20, dateArrivalField, dateDepartureField, locationField,
                                            zoneIdField);
		HBox arrangeTripRoomCosts = new HBox(20, roomSelection, roomCostField);
        HBox arrangeTripServiceCosts = new HBox(20, serviceSelection, serviceCostField);
		VBox arrangeTripVertical = new VBox(20);

		arrangeTripVertical.getChildren().addAll(addShipLabel, portLabel, titleLabel,
		            arrangeTripPortInfo, arrangeTripButton, costLabel, arrangeTripRoomCosts,
                 arrangeTripServiceCosts, addCostButton, createTripButton);   

		FlowPane pane = new FlowPane(arrangeTripVertical);
		pane.setAlignment(Pos.CENTER);

		Scene createTripsScene = new Scene(pane, 800, 500);
		stage.setScene(createTripsScene);
		stage.setTitle("Create Trip");

	}

	public void switchToCreateShipScene(ActionEvent event) throws NumberFormatException, NullPointerException { 

		labelCreateShip = new Label("Choose the number of rooms available for"+
									 " each room type, finalize once complete");
		labelCreateShip.setMaxSize(500, 500);

		roomCountField = new TextField("Number Of Rooms");
		//roomCountField.setMaxSize(80, 20);
		roomCountField.setOnMouseClicked(e -> roomCountField.clear());
	
		Button createShipButton = new Button("Finalize");
		//createShipButton.setMaxSize(80, 20);
		createShipButton.setOnAction(this::finalizeShip);

		
		listRoom = new ComboBox<>();
		for (RoomType roomType : RoomType.values()) {
			listRoom.getItems().add(roomType);
		}
		
		VBox arrangeText = new VBox(20, labelCreateShip, listRoom, roomCountField, createShipButton);

		FlowPane pane = new FlowPane(arrangeText);
		pane.setAlignment(Pos.CENTER);

		Scene createShipScene = new Scene(pane, 400, 400);
		stage.setScene(createShipScene);
		stage.setTitle("Create ship");
	}

	public void finalizeShip(ActionEvent event) {
		int roomCount = 0;
		roomTypeSelectedForShip = listRoom.getValue();

        try {

            roomCount = Integer.parseInt(roomCountField.getText());
			rooms = new EnumMap<RoomType, Integer>(RoomType.class);
        	rooms.put(roomTypeSelectedForShip, roomCount);
			//conn.createShip(rooms);
		    labelCreateShip.setText("Ship Creation successful");
        }
        catch(NumberFormatException nfe) {
            labelCreateShip.setText("Please only input integer values");
        }
		catch(NullPointerException ne) {
			labelCreateShip.setText("Denied, please add both Room Type and Count");
			
		}

	}

	public void addPort(ActionEvent event) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date arrivalDate = new Date();
        Date departureDate = new Date();

        try 
        {
            arrivalDate = sdf.parse(dateArrivalField.getText());
            departureDate = sdf.parse(dateDepartureField.getText());
			tripBuilder.addPort(arrivalDate, departureDate, locationField.getText(), zoneIdField.getText());
			warningLabel.setText("Port Added Succesfully");
			numTrips++;
        }
         catch(ParseException pe) {
            warningLabel.setText("Failed to input date(s), please check if format is correct");
        }
		catch(NullPointerException npe) {
			warningLabel.setText("Failed to add a port, please fill all boxes before adding.");
		}

		if (numTrips >= 2)
		createTripButton.setVisible(true);
	}

    public void addCost (ActionEvent event) {
        double roomCost = 0;
        double serviceCost = 0;

        try {
            roomCost = Double.parseDouble(roomCostField.getText());
            serviceCost = Double.parseDouble(serviceCostField.getText());
			tripBuilder.addCost(roomTypeSelectedForTrip, roomCost);
        	tripBuilder.addCost(serviceSelectedForTrip,serviceCost);
			//message to let them know of success;
        }
        catch(NumberFormatException nfe) {
            costLabel.setText("Try again, no alphabetic characters  for cost");
         } 
		catch(NullPointerException npe) {
			System.out.println("Try again, fill each boxes");
		}   
    }

    public void switchToChooseShipScene (ActionEvent event) {

        Button button = new Button("Done");
        Label label = new Label("Choose a ship");

        //combo box to store ships from db
		/* 
		   List<Ship> shipList = conn.queryShip();
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
        Scene scene = new Scene(pane, 300, 300);
        stage.setScene(scene);
        stage.setTitle("Choose a ship for the trip");
    }

	public void printTicketToFile (ActionEvent event){
		//..print ticket out to file

		String ticketContents = booked + "\n" + customerNameField;
		Path filePath = Path.of("ticket.txt");

		try {
			Files.writeString(filePath, ticketContents, StandardOpenOption.CREATE);
			welcomeLabel.setText("Successfully created ticket");
		}catch(IOException e) {
			welcomeLabel.setText("Could not create ticket");
		}
	}

	
}
