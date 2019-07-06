package src.gui.managerGUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import src.*;

import java.math.BigDecimal;
import java.util.Map;

public class ManagerBillStatusFX extends Application {

    private Stage window;
    private final BankMachine machine;
    private final Scene main;

    public ManagerBillStatusFX(BankMachine machine, Scene main){
        this.machine = machine;
        this.main = main;
    }
    /**
     * The scene for getting the status of the bills in the bank machine.
     */
    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        Map<BigDecimal, Bills> bills = machine.getCashMap();
        Text billStatusHeader = new Text("Bill Status of the ATM:");
        Text five = new Text("$5 Bills: ");
        Text fiveStatus = new Text("N.A.");
        Text ten = new Text("$10 Bills: ");
        Text tenStatus = new Text("N.A.");
        Text twenty = new Text("$20 Bills: ");
        Text twentyStatus = new Text("N.A.");
        Text fifty = new Text("$50 Bills: ");
        Text fiftyStatus = new Text("N.A.");
        fiveStatus.setText(String.valueOf(bills.get(new BigDecimal(5)).getQuantity()));
        tenStatus.setText(String.valueOf(bills.get(new BigDecimal(10)).getQuantity()));
        twentyStatus.setText(String.valueOf(bills.get(new BigDecimal(20)).getQuantity()));
        fiftyStatus.setText(String.valueOf(bills.get(new BigDecimal(50)).getQuantity()));

        Button backButton = new Button("Back");

        GridPane gridPane = new GridPane();

        gridPane.setMinSize(1024, 768);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);
//        gridPane.gridLinesVisibleProperty().setValue(true);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(billStatusHeader, 0,0);

        gridPane.add(five, 0, 1);
        gridPane.add(fiveStatus,1,1);
        gridPane.add(ten, 0, 2);
        gridPane.add(tenStatus,1,2);
        gridPane.add(twenty, 0, 3);
        gridPane.add(twentyStatus,1,3);
        gridPane.add(fifty, 0, 4);
        gridPane.add(fiftyStatus,1,4);
        gridPane.add(backButton, 0, 5);


        Scene billStatusScene = new Scene(gridPane);
        window.setScene(billStatusScene);
        backButton.setOnAction(e -> window.setScene(main));


    }
}
