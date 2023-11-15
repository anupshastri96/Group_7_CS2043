/**
 Rough GUI template/layout for Cruise ship application

 @author Mart Cesar Palamine
 CS2043 

 cruiseShipGUI.java
 
 */

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.layout.FlowPane;
import javafx.geometry.Pos;
import javafx.event.ActionEvent; 
import java.text.NumberFormat;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

public class cruiseShipGUI extends Application {
    private Text shipInfoBox1;
    private Text shipInfoBox2;
    private Text shipInfoBox3;

    public void start (Stage primaryStage) {

        BorderPane root = new BorderPane();
        VBox vbox = new VBox(85);
        VBox vbox2 = new VBox(50);

        

        //vbox.setAlignment(Pos.BASELINE_RIGHT);
       // vbox2.setAlignment(Pos.CENTER);

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

        vbox2.getChildren().addAll(shipInfoBox1, shipInfoBox2, shipInfoBox3);
        vbox.getChildren().addAll(butt1, butt2, butt3);

        /** 
        FlowPane pane = new FlowPane
            (appName, shipInfoBox1, shipInfoBox2, shipInfoBox3,butt1, butt2,
            butt3, next, prev);

            pane.setAlignment(Pos.CENTER);
            pane.setHgap(50);
            pane.setVgap(50);
        */

        Scene scene = new Scene (root, 500, 500);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Cruise Ship Application");
        primaryStage.show(); 

        

    }

        public void bookAction (ActionEvent event) {
        }

        public void nextAction (ActionEvent event) {
        }

        public void prevAction (ActionEvent event) {
        }
}