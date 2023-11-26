package inc.vareli.crusman.UI;

import inc.vareli.crusman.databases.*;
import inc.vareli.crusman.databases.Ship.RoomType;

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

/**
 * The main entry point to CrusMan, where users will interact with the data and buy tickets etc
 *
 * @author Mart Palamine
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
	private Text[] tripListings;

	//creating
	private TextField addShipField;
	private TextField dateArrivalField;
	private TextField dateDepartureField;
	private TextField locationField;
	private TextField zoneIdField;
	private TextField costTypeField;
	private TextField costAmountField;
	private Button createATripButton;
	private Button createAShipButton;
	private Button confirmBookingButton;
	private TextField roomCountField;

	/* 
	   List<Trip> trips = conn.queryTrips();
	   user selection stuff -> Trip trip = trips.asfigoslfh 
   	*/

	public void start(Stage stage) {
		this.stage = stage;
		VBox arrangeLoginFields = new VBox(20);//change to noun phrase - 
							//loginFieldsArrangement
		Button submit = new Button("Submit");
		submit.setPrefWidth(75);
		submit.setOnAction(this::submitLogin);

		loginError = new Label("Error Label");
		loginURLField = new TextField("Enter URL");
		loginIDField = new TextField("Enter ID");
		loginPassField = new TextField("Enter Password");

		loginFields.getChildren().addAll(loginURLField, loginIDField,
			       			loginPassField, submit, loginError);

		FlowPane pane = new FlowPane(loginFields);
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
			adminPasswordField.setOnAction(this::switchToCreateTripsScene);
		} else if (event.getSource() == createAShipButton) {
			adminPasswordField.setOnAction(this::switchToCreateShipScene);
		} else if (event.getSource() == confirmBookingButton) {
			welcomeLabel.setText("Waiting for admin to confirm payment");
			adminPasswordField.setText("");
			adminButton.setVisible(true);
			//TODO - actually handle password !
		}

		VBox arrangement = new VBox(20);
		box.getChildren().addAll(welcomeLabel, adminPasswordField, adminButton);

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

		//TODO - make this get the trips from the database
		//in progress

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

		HBox arrangeNextAndPrev = new HBox(10);
		arrangeNextAndPrev.getChildren().addAll(prev, next);

		BorderPane root = new BorderPane();
		root.setCenter(arrangeTripListings);
		root.setRight(arrangeBookButtons);
		root.setBottom(arrangeNextAndPrev);

		Scene browsingScene = new Scene(root, 500, 550);
		stage.setScene(browsingScene);
		stage.setTitle("Crus, Man!");
	}

	public void switchToBookingScene(ActionEvent event) {
		Label mealLabel = new Label("Meal Plan");
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
		arrangeLabels.getChildren().addAll(mealLabel, roomLabel);

		HBox arrangeSelections = new HBox(30);
		arrangeSelections.getChildren().addAll(mealSelection, roomSelection);

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

		Label addShipLabel = new Label("Add a ship");
		Label portLabel = new Label("Create a port");
		Label costLabel = new Label("Add cost");

		//combo box to store ships from db
		/* 
		   List<Ship> shipList = conn.queryShip();
		   ComboBox<Ship> cbShip = new ComboBox<>();
		   for (Ship ship : shipList) {
		   cbShip.getItems().add(ship);
		   }
		   */

		addShipField = new TextField("add a ship");
		addShipField.setOnMouseClicked(e -> addShipField.clear());

		dateArrivalField = new TextField("date arrival");
		dateArrivalField.setOnMouseClicked(e -> dateArrivalField.clear());

		dateDepartureField = new TextField("date departure");
		dateDepartureField.setOnMouseClicked(e -> dateDepartureField.clear());

		locationField = new TextField("location");
		locationField.setOnMouseClicked(e -> locationField.clear());

		zoneIdField = new TextField("zone id");
		zoneIdField.setOnMouseClicked(e -> zoneIdField.clear());

		costTypeField = new TextField("Cost Type");
		costTypeField.setOnMouseClicked(e -> costTypeField.clear());

		costAmountField = new TextField("amount");
		costAmountField.setOnMouseClicked(e -> costAmountField.clear());

		Button createTripButton = new Button("Create Trip");
		createTripButton.setPrefWidth(80);
		createTripButton.setOnAction(this::finalizeTrip);

		Button addPortButton = new Button("Create Port");
		createTripButton.setPrefWidth(80);
		createTripButton.setOnAction(this::createPort);

		HBox arrangeTripShip = new HBox(10);
		HBox arrangeTripPortInfo = new HBox(20);
		HBox arrangeTripCosts = new HBox(20);
		VBox arrangeTripVertical = new VBox(20);

		arrangeTripShip.getChildren().addAll(addShipField);

		arrangeTripPorts.getChildren().addAll(dateArrivalField, dateDepartureField, 
							locationField, zoneIdField);

		arrangeTripCosts.getChildren().addAll(costTypeField, costAmountField);

		arrangeTripVertical.getChildren().addAll(addShipLabel, tripShipArrange, portLabel,
			tripPortArrange, costLabel, tripCostArrange, createTripButton, addPortButton);   

		FlowPane pane = new FlowPane(tripVerticalArrange);
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

	}

	public void finalizeTrip (ActionEvent event) {
		//..make trip functionality

		String ship = addShipField.getText();
		String dateArrival = dateArrivalField.getText();
		String dateDeparture = dateDepartureField.getText();
		String location = locationField.getText();
		String zoneID = zoneIdField.getText();
		String costType = costTypeField.getText();
		String costAmount = costAmountField.getText();

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
