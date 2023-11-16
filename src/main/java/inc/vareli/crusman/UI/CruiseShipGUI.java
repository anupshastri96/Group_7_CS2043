/**
  GUI template/layout for Cruise ship application
 @author Mart Cesar Palamine        CS2043
 */

package inc.vareli.crusman.UI;

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
import java.text.NumberFormat;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ComboBox;


/**
 Rough GUI template/layout for Cruise ship application

 @author Mart Cesar Palamine
 CS2043 
*/

public class CruiseShipGUI extends Application {
    private Text shipInfoBox1;
    private Text shipInfoBox2;
    private Text shipInfoBox3;
    private Scene scene;
    private Scene scene2;
    private Stage stage;

    public void start (Stage primaryStage) {

        BorderPane root = new BorderPane();
        stage = primaryStage;
        VBox vbox = new VBox(85);
        VBox vbox2 = new VBox(95);
        HBox hbox = new HBox(350);

        Button butt1 = new Button("BOOK");
        butt1.setPrefWidth(75);
        butt1.setOnAction(this::bookAction);

        Button butt2 = new Button("BOOK");
        butt2.setPrefWidth(75);
        butt2.setOnAction(this::bookAction);

        Button butt3 = new Button("BOOK");
        butt3.setPrefWidth(75);
        butt3.setOnAction(this::bookAction);

        Button next = new Button("NEXT");
        next.setPrefWidth(75);
        next.setOnAction(this::nextAction);

        Button prev = new Button("PREV");
        prev.setPrefWidth(75);
        prev.setOnAction(this::prevAction);

        shipInfoBox1 = new Text("Ship info here");
        shipInfoBox2 = new Text("Ship info here");
        shipInfoBox3 = new Text("Ship info here");

        root.setCenter(vbox2);
        root.setRight(vbox);
        root.setBottom(hbox);
        
        hbox.getChildren().addAll(next, prev);
        vbox2.getChildren().addAll(shipInfoBox1, shipInfoBox2, shipInfoBox3);
        vbox.getChildren().addAll(butt1, butt2, butt3);

        scene = new Scene (root, 500, 500);

        stage.setScene(scene);
        stage.setTitle("Cruise Ship Application");
        stage.show(); 

    }
        public void bookAction (ActionEvent event) {

            Label mealLabel = new Label("Meal Plan");
            Label roomLabel = new Label("Room Plan");
            Label boardLabel = new Label("Boarding Port");
            Label destLabel = new Label("Destination Port");
            
            TextField customerName = new TextField("Input Customer Name");

             Button returnButton = new Button("RETURN");
            returnButton.setPrefWidth(300);
            returnButton.setOnAction(e -> switchScenes(scene));

            Button printTicket = new Button("PRINT TICKET");
            printTicket.setPrefWidth(300);

            HBox hbox3 = new HBox(30);
            HBox hbox4 = new HBox(70);
            VBox vbox3 = new VBox(20);
            vbox3.getChildren().addAll(customerName, printTicket, returnButton);

            ComboBox cb1 = new ComboBox();
            ComboBox cb2 = new ComboBox();
            ComboBox cb3 = new ComboBox();
            ComboBox cb4 = new ComboBox();

            ObservableList<String> mealList = cb1.getItems();
            mealList.add("single plan");
            mealList.add("double plan");
            mealList.add("family plan");

            ObservableList<String> roomList = cb2.getItems();
            roomList.add("balcony");
            roomList.add("Outside View");
            roomList.add("Suite");

            ObservableList<String> boardingList = cb3.getItems();
            boardingList.add("board port 1");
            boardingList.add("board port 2");

            ObservableList<String> destinationList = cb4.getItems();
            destinationList.add("Destination 2");
            destinationList.add("Destination 3");

            hbox3.getChildren().addAll(cb1, cb2, cb3, cb4 );
            hbox4.getChildren().addAll(mealLabel, roomLabel, boardLabel, destLabel);

            FlowPane pane = new FlowPane(hbox4, hbox3, vbox3);
            pane.setAlignment(Pos.CENTER);
				pane.setHgap(50);
				pane.setVgap(60);

            scene2 = new Scene (pane, 700, 500);
            stage.setScene(scene2);
            stage.setTitle("Print ticket");
            stage.show();

        }
        //switches scene
        public void switchScenes(Scene scene) {
                stage.setScene(scene);
        }
        
        public void nextAction (ActionEvent event) {}
        public void prevAction (ActionEvent event) {}
}