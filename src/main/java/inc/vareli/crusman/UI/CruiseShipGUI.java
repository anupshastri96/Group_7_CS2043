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
    private Stage stage;

    private TextField loginURLField;
    private TextField loginIDField;
    private TextField loginPassField;
    private Label loginError;

    private CMConnection conn;

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
		switchToBrowseScene(event);
    	} catch (IllegalArgumentException iae) {
		loginError.setText(iae.getMessage());
		return;
	}
    }
                         
    public void switchToBrowseScene(ActionEvent event) {

        Button next = new Button("NEXT");
        next.setPrefWidth(75);
        next.setOnAction(this::next);

        Button prev = new Button("PREV");
        prev.setPrefWidth(75);
        prev.setOnAction(this::prev);

	//TODO - make this get the trips from the database
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

        HBox arrangeNextAndPrev = new HBox(350);
        arrangeNextAndPrev.getChildren().addAll(next, prev);

        BorderPane root = new BorderPane();
        root.setCenter(arrangeTripListings);
        root.setRight(arrangeBookButtons);
        root.setBottom(arrangeNextAndPrev);

        browsingScene = new Scene(root, 500, 500);

        stage.setScene(browsingScene);
        stage.setTitle("Crus, Man!");
    }

    public void switchToBookingScene(ActionEvent event) {
            Label mealLabel = new Label("Meal Plan");
            Label roomLabel = new Label("Room Plan");
            Label testLabelForEvents = new Label("test");
            
	    //TODO - MAKE THIS BETTER
            TextField customerName = new TextField("Input Customer Name");

            Button returnButton = new Button("RETURN");
            returnButton.setPrefWidth(300);
            returnButton.setOnAction(this::switchToBrowseScene);

            Button printTicket = new Button("PRINT TICKET");
            printTicket.setPrefWidth(300);
            printTicket.setOnAction(this::printTicket);

            ComboBox mealSelection = new ComboBox();
            ComboBox roomSelection = new ComboBox();

	    //meals are opt in or opt out. meals have a single cost and that cost is zero if opt out
            ObservableList<String> mealList = mealSelection.getItems();
            mealList.add("Opt In");
            mealList.add("Opt Out");
        //add action event if opt in or out
           

	    //TODO - trip info will show the occupancy, only allow them to pick room types that arent fully occupied
            ObservableList<String> roomList = roomSelection.getItems();
            for (RoomType roomType : RoomType.values()) {
                roomList.add(roomType.toString());
            }

            HBox arrangeLabels = new HBox(70);
            arrangeLabels.getChildren().addAll(mealLabel, roomLabel);

            HBox arrangeSelections = new HBox(30);
            arrangeSelections.getChildren().addAll(mealSelection, roomSelection);

            VBox arrangeButtons = new VBox(20);
            arrangeButtons.getChildren().addAll(customerName, printTicket, returnButton);

            FlowPane pane = new FlowPane(arrangeLabels, arrangeSelections, arrangeButtons);
            pane.setAlignment(Pos.CENTER);
				pane.setHgap(50);
				pane.setVgap(60);

            bookingScene = new Scene (pane, 400, 500);
            stage.setScene(bookingScene);
            stage.setTitle("Print ticket");
        }

    	//TODO
        public void printTicket(ActionEvent event) {}
        public void next(ActionEvent event) {} //TODO - MART!!!!
        public void prev(ActionEvent event) {}
}
