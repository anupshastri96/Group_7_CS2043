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
    private Text[] tripListings;
    private Scene loginScene;
    private Scene browsingScene;
    private Scene bookingScene;
    private Scene createShipScene;
    private Scene createTripsScene;
    private Scene adminPasswordScene;
    private Scene mainMenuScene;
    private Stage stage;

    private TextField loginURLField;
    private TextField loginIDField;
    private TextField loginPassField;
    private Label loginError;

    //create trips
    private TextField addShipField;
    private TextField dateArrivalField;
    private TextField dateDepartureField;
    private TextField locationField;
    private TextField zoneIdField;
    private TextField costTypeField;
    private TextField costAmountField;

    //create ships
    private TextField roomCountField;
    private ComboBox roomListShip;

    private CMConnection conn;
    private int numberOfTrips; 
    private int s;

    /* 
    List<Trip> trips = conn.queryTrips();
    user selection stuff -> Trip trip = trips.asfigoslfh 
    */

    public void start(Stage stage) {
	this.stage = stage;
        VBox loginFields = new VBox(20);
        Button submit = new Button("Submit");
        submit.setPrefWidth(75);
        submit.setOnAction(this::submitLogin);

        loginError = new Label("Error Label");
        loginURLField = new TextField("Enter URL");
        loginIDField = new TextField("Enter ID");
        loginPassField = new TextField("Enter Password");

        loginFields.getChildren().addAll(loginURLField, loginIDField, loginPassField, submit, loginError);
    
        FlowPane fpanePopup = new FlowPane(loginFields);
        fpanePopup.setAlignment(Pos.CENTER);
        fpanePopup.setHgap(20);
	    fpanePopup.setVgap(50);

        loginScene = new Scene(fpanePopup, 300, 500);
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
		return;
	}
    }

    public void  switchToMainMenuScene (ActionEvent event) {

        Label menuLabel = new Label("Welcome!");

        Button goToBrowseScene = new Button("Book Trips");
        goToBrowseScene.setPrefWidth(90);
        goToBrowseScene.setOnAction(this::switchToBrowseScene);

        Button createATripButton = new Button("Create a trip");
        createATripButton.setPrefWidth(90);
        createATripButton.setOnAction(this::createTrip);
        

        Button createAShipButton = new Button("Create a ship");
        createAShipButton.setPrefWidth(90);
        createAShipButton.setOnAction(this::createShip);

        VBox arrangeMenu =  new VBox(10);
        arrangeMenu.getChildren().addAll(menuLabel, goToBrowseScene, createATripButton, createAShipButton);

        FlowPane fpaneMenu = new FlowPane(arrangeMenu);
        fpaneMenu.setAlignment(Pos.CENTER);

        mainMenuScene = new Scene(fpaneMenu, 250, 300);
        stage.setScene(mainMenuScene);
        stage.setTitle("Crusman cruise ship application");
    }

    public void createTrip (ActionEvent event) {
        s = 2;
        switchToAdminPassword(event);
    }
    public void createShip (ActionEvent event) {
        s = 1;
        switchToAdminPassword(event);
    }

    public void confirmTrip (ActionEvent event) {
        s = 0;
        switchToAdminPassword(event);
    }
                         
    public void switchToBrowseScene(ActionEvent event) {

        Button next = new Button("NEXT");
        next.setPrefWidth(75);
        next.setOnAction(this::next);

        Button prev = new Button("PREV");
        prev.setPrefWidth(75);
        prev.setOnAction(this::prev);



	//TODO - make this get the trips from the database
    //in progress

    /** 
    List<Trip> trips = conn.queryTrip();
    numberOfTrips = trips.size();

    tripListings = new Text[numberOfTrips];
    for (int i = 0; < tripListings.length; i++ \) {
        tripListings[i] = new Text(trips.get(i).toString());
    }
    */

	tripListings = new Text[3];
	for (int i = 0; i < tripListings.length; i++) {
		tripListings[i] = new Text("Trip info here.");
	}

	Button[] bookingButtons = new Button[tripListings.length];//this needs to be same length as tripListings
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

        browsingScene = new Scene(root, 500, 550);
        stage.setScene(browsingScene);
        stage.setTitle("Crus, Man!");
    }

    public void switchToBookingScene(ActionEvent event) {
            Label mealLabel = new Label("Meal Plan");
            Label roomLabel = new Label("Room Plan");
            Label testLabelForEvents = new Label("test");
            
	    //TODO - MAKE THIS BETTER
            TextField customerName = new TextField("Input Customer Name");
            customerName.setOnMouseClicked(m -> customerName.clear());

            Button returnButton = new Button("Return");
            returnButton.setPrefWidth(300);
            returnButton.setOnAction(this::switchToBrowseScene);

            Button confirmButton = new Button("Confirm");
            confirmButton.setPrefWidth(300);
            confirmButton.setOnAction(this::switchToAdminPassword);

            ComboBox mealSelection = new ComboBox();
            ComboBox roomSelection = new ComboBox();

            ObservableList<String> mealList = mealSelection.getItems();
            mealList.add("Opt In");
            mealList.add("Opt Out");

            //Event handler for mealSelection
            EventHandler<ActionEvent> chooseMealSelection = new EventHandler<ActionEvent>() {
                public void handle (ActionEvent e) {
                    testLabelForEvents.setText(mealSelection.getValue().toString());
                }
        };
        mealSelection.setOnAction(chooseMealSelection);
           

	    //TODO - trip info will show the occupancy, only allow them to pick room types that arent fully occupied
            ObservableList<String> roomList = roomSelection.getItems();
            for (RoomType roomType : RoomType.values()) {
                roomList.add(roomType.toString());
            }

            //This is the event handler for roomSelection choicebox
            EventHandler<ActionEvent> chooseRoomType = new EventHandler<ActionEvent>() {
                public void handle (ActionEvent e) {
                    testLabelForEvents.setText(roomSelection.getValue().toString());
                    
                }
        };
        roomSelection.setOnAction(chooseRoomType);

            HBox arrangeLabels = new HBox(70);
            arrangeLabels.getChildren().addAll(mealLabel, roomLabel);

            HBox arrangeSelections = new HBox(30);
            arrangeSelections.getChildren().addAll(mealSelection, roomSelection);

            VBox arrangeButtons = new VBox(20);
            arrangeButtons.getChildren().addAll(customerName, confirmButton, returnButton, testLabelForEvents);

            FlowPane fpaneBooking = new FlowPane(arrangeLabels, arrangeSelections, arrangeButtons);
            fpaneBooking.setAlignment(Pos.CENTER);
			fpaneBooking.setHgap(50);
			fpaneBooking.setVgap(60);

            bookingScene = new Scene (fpaneBooking, 400, 500);
            stage.setScene(bookingScene);
            stage.setTitle("Print ticket");
        }

        public void switchToAdminPassword (ActionEvent event) {

            Button adminButton = new Button("Confirm / Print Ticket");
            adminButton.setVisible(false);
            adminButton.setOnAction(this::printTicketToFile);

            Label welcomeLabel = new Label("Enter Password");
            TextField adminPasswordField = new TextField("hit enter to submit");
            adminPasswordField.setOnMouseClicked(m -> adminPasswordField.clear());
            if (s == 2) 
                adminPasswordField.setOnAction(this::switchToCreateTripsScene);
            if (s == 1)
                adminPasswordField.setOnAction(this::switchToCreateShipScene);
            if (s == 0) {
                welcomeLabel.setText("Waiting for admin to confirm payment");
                adminPasswordField.setText("");
                adminButton.setVisible(true);
            }


            VBox box = new VBox(20);
            box.getChildren().addAll(welcomeLabel, adminPasswordField, adminButton);

            FlowPane fpaneAdminPass = new FlowPane(box);
            fpaneAdminPass.setAlignment(Pos.CENTER);
            fpaneAdminPass.setVgap(10);

            adminPasswordScene = new Scene (fpaneAdminPass, 400, 400);
            stage.setScene(adminPasswordScene);
            stage.setTitle("Admin Login Panel");

        }

        public void switchToCreateTripsScene (ActionEvent event) {

            Label addShipLabel = new Label("Add a ship");
            Label portLabel = new Label("Create a port");
            Label costLabel = new Label("Add cost");

            //combo box to store ships from db
            List<Ship> shipList = conn.queryShip();
            ComboBox<Ship> cbShip = new ComboBox<>();
            for (Ship ship : shipList) {
                cbShip.getItems().add(ship);
            }

            addShipField = new TextField("add a ship");
            addShipField.setOnMouseClicked(m -> addShipField.clear());
            
            dateArrivalField = new TextField("date arrival");
            dateArrivalField.setOnMouseClicked(m -> dateArrivalField.clear());

            dateDepartureField = new TextField("date departure");
            dateDepartureField.setOnMouseClicked(m -> dateDepartureField.clear());
           
            locationField = new TextField("location");
            locationField.setOnMouseClicked(m -> locationField.clear());
            
            zoneIdField = new TextField("zone id");
            zoneIdField.setOnMouseClicked(m -> zoneIdField.clear());

            costTypeField = new TextField("Cost Type");
            costTypeField.setOnMouseClicked(m -> costTypeField.clear());
            
            costAmountField = new TextField("amount");
            costAmountField.setOnMouseClicked(m -> costAmountField.clear());
           
            Button createTripButton = new Button("Create Trip");
            createTripButton.setPrefWidth(80);
            createTripButton.setOnAction(this::makeATrip);

            HBox tripShipArrange = new HBox(10);
            HBox tripPortArrange = new HBox(20);
            HBox tripCostArrange = new HBox(20);
            VBox tripVerticalArrange = new VBox(20);

            tripShipArrange.getChildren().addAll(addShipField); //seems redundant but size messess up if not

            tripPortArrange.getChildren().addAll(dateArrivalField, dateDepartureField, 
                                                    locationField, zoneIdField);
            
            tripCostArrange.getChildren().addAll(costTypeField, costAmountField);

            tripVerticalArrange.getChildren().addAll(addShipLabel, tripShipArrange, portLabel,
                                                    tripPortArrange, costLabel, tripCostArrange, createTripButton);   

            FlowPane fpaneTrip = new FlowPane(tripVerticalArrange);
            fpaneTrip.setAlignment(Pos.CENTER);

            createTripsScene = new Scene(fpaneTrip, 800, 500);
            stage.setScene(createTripsScene);
            stage.setTitle("Create Trip");

        }

        public void switchToCreateShipScene (ActionEvent event) {

            Label chooseRoomTypeLabel = new Label("Choose Room Type");

            roomCountField = new TextField("Room Count");
            roomCountField.setOnMouseClicked(m -> roomCountField.clear());
            roomCountField.setPrefWidth(80);
            
            Button createShipButton = new Button("Finalize");
            createShipButton.setOnAction(this::makeAShip);

            //** old combox box
            roomListShip = new ComboBox();

            //*** new combo box
            ComboBox<RoomType> listRoom = new ComboBox<>();
            for (RoomType roomType : RoomType.values()) {
                listRoom.getItems().add(roomType);
            }
            
            ObservableList<String> roomList = roomListShip.getItems();
            for (RoomType roomType : RoomType.values()) {
                roomList.add(roomType.toString());
            }

            VBox arrangeShips = new VBox(30);
            arrangeShips.getChildren().addAll(chooseRoomTypeLabel, listRoom, roomCountField, createShipButton);

            FlowPane fpaneShip = new FlowPane(arrangeShips);
            fpaneShip.setAlignment(Pos.CENTER);

            createShipScene = new Scene(fpaneShip, 350, 300);
            stage.setScene(createShipScene);
            stage.setTitle("Create ship");

        }

        public void makeAShip (ActionEvent event) {
            //..make ship functionality

            String roomCount = roomCountField.getText();

            //Map<roomType, Integer> rc = new Map<>();
           // rc.put(listRoom.getValue(), roomCount);

           // Ship ship = new conn.createShip();

        }
        public void makeATrip (ActionEvent event) {
            //..make trip functionality

            String ship = addShipField.getText();
            String dateArrival = dateArrivalField.getText();
            String dateDeparture = dateDepartureField.getText();
            String location = locationField.getText();
            String zoneID = zoneIdField.getText();
            String costType = costTypeField.getText();
            String costAmount = costAmountField.getText();

        /**  
           Trip t = new conn.createTrip();
        
           trip1.addCost(costType, costAmount);
         **/

        }

        public void printTicketToFile (ActionEvent event){
            //..print ticket out to file
        }
        public void next(ActionEvent event) {} //TODO - MART!!!!
        public void prev(ActionEvent event) {}
        
}
