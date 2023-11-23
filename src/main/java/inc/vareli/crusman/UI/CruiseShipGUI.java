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
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
        BorderPane root = new BorderPane();
        VBox vbox = new VBox(85);
        VBox vbox2 = new VBox(95);
        HBox hbox = new HBox(350);

	Button[] bookingButtons = new Button[3];
	for (Button butt : bookingButtons) {
		butt = new Button("BOOK");
		butt.setPrefWidth(75);
		butt.setOnAction(this::switchToBookingScene);
	}

        Button next = new Button("NEXT");
        next.setPrefWidth(75);
        next.setOnAction(this::next);

        Button prev = new Button("PREV");
        prev.setPrefWidth(75);
        prev.setOnAction(this::prev);

	tripListings = new Text[3];

	//TODO - make this get the trips from the database
	for (Text tripInfo : tripListings) {
		tripInfo = new Text("Trip info here.");
	}

	//HOMEWORK: name this better ffs
        root.setCenter(vbox2);
        root.setRight(vbox);
        root.setBottom(hbox);

	tripListings[0] = new Text("Trip info here.");
	bookingButtons[0] = new Button("Fart");
        
	//this too
        hbox.getChildren().addAll(next, prev);
        vbox2.getChildren().addAll(tripListings[0]);
        vbox.getChildren().addAll(bookingButtons[0]);

        browsingScene = new Scene(root, 500, 500);

        stage.setScene(browsingScene);
        stage.setTitle("Crus, Man!");
    }

    public void switchToBookingScene(ActionEvent event) {
            Label mealLabel = new Label("Meal Plan");
            Label roomLabel = new Label("Room Plan");
            
	    //TODO - MAKE THIS BETTER
            TextField customerName = new TextField("Input Customer Name");

            Button returnButton = new Button("RETURN");
            returnButton.setPrefWidth(300);
            returnButton.setOnAction(this::switchToBrowseScene);

            Button printTicket = new Button("PRINT TICKET");
            printTicket.setPrefWidth(300);
            printTicket.setOnAction(this::printTicket);

	    //if this is temporary then fix it so help me god
            HBox hbox3 = new HBox(30);
            HBox hbox4 = new HBox(70);
            VBox vbox3 = new VBox(20);
            vbox3.getChildren().addAll(customerName, printTicket, returnButton);

	    //what the fuck is this
            ComboBox cb1 = new ComboBox();
            ComboBox cb2 = new ComboBox();
            ComboBox cb3 = new ComboBox();
            ComboBox cb4 = new ComboBox();

	    //meals are opt in or opt out. meals have a single cost and that cost is zero if opt out
            ObservableList<String> mealList = cb1.getItems();
            mealList.add("single plan");
            mealList.add("double plan");
            mealList.add("family plan");

	    //TODO - trip info will show the occupancy, only allow them to pick room types that arent fully occupied
            ObservableList<String> roomList = cb2.getItems();
            for (RoomType roomType : RoomType.values()) {
                roomList.add(roomType.toString());
            }
            hbox3.getChildren().addAll(cb1, cb2);
            hbox4.getChildren().addAll(mealLabel, roomLabel);

            FlowPane pane = new FlowPane(hbox4, hbox3, vbox3);
            pane.setAlignment(Pos.CENTER);
				pane.setHgap(50);
				pane.setVgap(60);

            bookingScene = new Scene (pane, 700, 500);
            stage.setScene(bookingScene);
            stage.setTitle("Print ticket");
        }

    	//TODO
        public void printTicket(ActionEvent event) {}
        public void next(ActionEvent event) {} //TODO - MART!!!!
        public void prev(ActionEvent event) {}
}
