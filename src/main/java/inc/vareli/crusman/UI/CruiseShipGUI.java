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
import java.text.SimpleDateFormat;
import java.text.ParseException;

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
	private Text[] tripListings;

	//creating
	private TextField dateArrivalField;
	private TextField dateDepartureField;
	private TextField locationField;
	private TextField zoneIdField;
	private TextField costTypeField;
	private TextField roomCostField;
    private TextField serviceCostField;
	private Button createATripButton;
	private Button createAShipButton;
	private Button confirmBookingButton;
	private TextField roomCountField;
    private Label warningLabel;
    //private Ship selectedShip;
    private String selectedShip;
    private RoomType roomTypeSelectedForTrip;
    private Service serviceSelectedForTrip;

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

		Label welcomeLabel = new Label("Enter Password");
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
		//TODO - maybe
		/*Button next = new Button("NEXT");
		next.setPrefWidth(75);
		next.setOnAction(this::next);

		Button prev = new Button("PREV");
		prev.setPrefWidth(75);
		prev.setOnAction(this::prev);*/

		/* 
		  List<Trip> trips = conn.queryTrip();

		  tripListings = new Text[trips.size()];
		  for (int i = 0; < tripListings.length; i++ \) {
		  tripListings[i] = new Text(trips.get(i).toString());
		  }
		 */

		tripListings = new Text[3];
		for (int i = 0; i < tripListings.length; i++) {
			tripListings[i] = new Text("Trip info here.");
		}

		Button[] bookingButtons = new Button[tripListings.length];
		for (int i = 0; i < bookingButtons.length; i++) {
			bookingButtons[i] = new Button("BOOK");
			bookingButtons[i].setPrefWidth(75);
			bookingButtons[i].setOnAction(this::switchToBookingScene);
		}

		VBox arrangeBookButtons = new VBox(85);
		arrangeBookButtons.getChildren().addAll(bookingButtons);

		VBox arrangeTripListings = new VBox(95);
		arrangeTripListings.getChildren().addAll(tripListings);

        /* 
		HBox arrangeNextAndPrev = new HBox(10);
		arrangeNextAndPrev.getChildren().addAll(prev, next);
        */

		BorderPane root = new BorderPane();
		root.setCenter(arrangeTripListings);
		root.setRight(arrangeBookButtons);
		//root.setBottom(arrangeNextAndPrev);

		Scene browsingScene = new Scene(root, 500, 550);
		stage.setScene(browsingScene);
		stage.setTitle("Crus, Man!");
	}

	public void switchToBookingScene(ActionEvent event) {
		Label mealLabel = new Label("Meal Plan");
        Label drinkLabel = new Label("Drink Plan");
		Label roomLabel = new Label("Room Plan");


		TextField customerName = new TextField("Input Customer Name");
		customerName.setOnMouseClicked(e -> customerName.clear());

		Button returnButton = new Button("Return");
		returnButton.setPrefWidth(300);
		returnButton.setOnAction(this::switchToBrowseScene);

		confirmBookingButton = new Button("Confirm");
		confirmBookingButton.setPrefWidth(300);
		confirmBookingButton.setOnAction(this::switchToAdminPassword);

		ComboBox<String> mealSelection = new ComboBox<String>();
		mealSelection.getItems().add("Opt In");
		mealSelection.getItems().add("Opt Out");
		/*mealSelection.setOnAction(e -> testLabelForEvents.setText(
							mealSelection.getValue().toString()
						)
					); because this is complicated, maybe delete*/

		ComboBox<String> drinkSelection = new ComboBox<String>();
		drinkSelection.getItems().add("Opt In");
		drinkSelection.getItems().add("Opt Out");

		ComboBox<RoomType> roomSelection = new ComboBox<RoomType>();

		//TODO - trip info will show the occupancy,
		//only allow them to pick room types that arent fully occupied
		
		for (RoomType roomType : RoomType.values()) {
			roomSelection.getItems().add(roomType);
		}

		HBox arrangeLabels = new HBox(70);
		arrangeLabels.getChildren().addAll(mealLabel, roomLabel, drinkLabel);

		HBox arrangeSelections = new HBox(30);
		arrangeSelections.getChildren().addAll(mealSelection, drinkSelection, roomSelection);

		VBox arrangeButtons = new VBox(20);
		arrangeButtons.getChildren().addAll(customerName, 
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
		Label portLabel = new Label("Create a port  -  A trip must have at least 2 ports");
        Label titleLabel = new Label("Arrival Date\t         \t" + "  \tDeparture Date");
		Label costLabel = new Label("Add cost");
        warningLabel = new Label();

		dateArrivalField = new TextField("dd-MM-yyyy");
        dateDepartureField = new TextField("dd-MM-yyyy");
        locationField = new TextField("location");
        zoneIdField = new TextField("zone id");
        roomCostField = new TextField("Room Cost");
        serviceCostField = new TextField("Service Cost");

        Button createTripButton = new Button("Create Trip");
        Button addPortButton = new Button("Create Port");
        Button addCostButton = new Button("Add Cost");

        createTripButton.setOnAction(this::finalizeTrip);
        addPortButton.setOnAction(this::createPort);
        //addCostButton.setOnAction(this::addCost);

		dateArrivalField.setOnMouseClicked(e -> dateArrivalField.clear());
        dateDepartureField.setOnMouseClicked(e -> dateDepartureField.clear());
        locationField.setOnMouseClicked(e -> locationField.clear());
        zoneIdField.setOnMouseClicked(e -> zoneIdField.clear());
        roomCostField.setOnMouseClicked(e -> roomCostField.clear());
        serviceCostField.setOnMouseClicked(e -> serviceCostField.clear());

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

	public void switchToCreateShipScene(ActionEvent event) {
		Label chooseRoomTypeLabel = new Label("Choose Room Type");

		roomCountField = new TextField("Room Count");
		roomCountField.setOnMouseClicked(e -> roomCountField.clear());
		roomCountField.setPrefWidth(80);

		Button createShipButton = new Button("Finalize");
		createShipButton.setOnAction(this::finalizeShip);

		ComboBox<RoomType> listRoom = new ComboBox<>();
		for (RoomType roomType : RoomType.values()) {
			listRoom.getItems().add(roomType);
		}

		//Map<RoomType,Integer> rooms = new EnumMap<RoomType,Integer>(RoomType.class);
		//RoomType selectedType = listRoom.getValue();
		//rooms.put(selectedType,Integer.parseInt(textfield...));
		//when they click finalize -> CMConnection.createShip(rooms);

		VBox arrangeShips = new VBox(30);
		arrangeShips.getChildren().addAll(chooseRoomTypeLabel, listRoom, 
							roomCountField, createShipButton);

		FlowPane pane = new FlowPane(arrangeShips);
		pane.setAlignment(Pos.CENTER);

		Scene createShipScene = new Scene(pane, 350, 300);
		stage.setScene(createShipScene);
		stage.setTitle("Create ship");
	}

	public void finalizeShip(ActionEvent event) {
		//..make ship functionality

		String roomCount = roomCountField.getText();

		//Map<roomType, Integer> rc = new Map<>();
		// rc.put(listRoom.getValue(), roomCount);

		// Ship ship = new conn.createShip();
	}

	public void createPort(ActionEvent event) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date arrivalDate = new Date();
        Date departureDate = new Date();

        try 
        {
            arrivalDate = sdf.parse(dateArrivalField.getText());
            departureDate = sdf.parse(dateDepartureField.getText());
        }
         catch(ParseException pe) {
            warningLabel.setText("Warning: Failed to input date(s), please check if format is correct");
        }

        tripBuilder.addPort(arrivalDate, departureDate, locationField.getText(), zoneIdField.getText());
	}

	public void finalizeTrip (ActionEvent event) {

		String costType = costTypeField.getText();
		String costAmount = roomCostField.getText();

		//in the ship select menu
		//shipSeelected = whatever they selected from a list

		/*
		   TripBuiler temp = new TripBuilder(shipSelected);
		   temp.addPort(....);

		   createTrip -> CMConnection.createTrip(temp);
		   */

		/*  
		  Trip t = new conn.createTrip();
		  trip1.addCost(costType, costAmount);
		 */
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
	}

	public void next(ActionEvent event) {
		//implement next and prev
		//can do in a single method with ActionEvent.getSource()
	}

	public void prev(ActionEvent event) {
		//implement next and prev
	}
}
