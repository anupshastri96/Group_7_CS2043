/**
 Rough GUI template/layout for Cruise ship application
 @author Mart Cesar Palamine        CS2043
 */
package inc.vareli.crusman.UI;

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

/**
 Rough GUI template/layout for Cruise ship application

 @author Mart Cesar Palamine
 CS2043 
*/
public class CruiseShipGUI extends Application {
    private Text shipInfoBox1;
    private Text shipInfoBox2;
    private Text shipInfoBox3;

    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        AnchorPane anchorPane = new AnchorPane();
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

        Scene scene = new Scene (root, 500, 500);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Cruise Ship Application");
        primaryStage.show(); 

    }
        public void bookAction (ActionEvent event) {}
        public void nextAction (ActionEvent event) {}
        public void prevAction (ActionEvent event) {}
}
