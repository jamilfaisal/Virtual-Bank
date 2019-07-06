package src.gui.managerGUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import src.BankMachine;
import src.BankManager;
import src.Bills;
import src.Employee;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ManagerFillATMFX extends Application {

    private Stage window;
    private final BankMachine machine;
    private final Employee employee;
    private final Scene main;

    public ManagerFillATMFX(Employee employee, BankMachine machine, Scene main){
        this.machine = machine;
        this.employee = employee;
        this.main = main;
    }
    /**
     * The scene for filling the ATM with bills.
     */
    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;

        Text fillATMText = new Text("Please choose how many of each bill you want to add:");
        Text fillATMMessage = new Text("");
        Text fiveText = new Text("5$ bills: ");
        TextField fiveField = new TextField("0");

        Text tenText = new Text("10$ bills: ");
        TextField tenField = new TextField("0");

        Text twentyText = new Text("20$ bills: ");
        TextField twentyField = new TextField("0");

        Text fiftyText = new Text("50$ bills: ");
        TextField fiftyField = new TextField("0");

        Button submitButton = new Button("Submit");
        Button backButton = new Button("Back");

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        gridPane.setMinSize(1024, 768);

        //Setting the padding
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        //Arranging all the nodes in the grid
        gridPane.add(fillATMText, 0, 0);
        gridPane.add(fiveText, 0, 1);
        gridPane.add(fiveField, 1, 1);
        gridPane.add(tenText, 0, 2);
        gridPane.add(tenField, 1, 2);
        gridPane.add(twentyText, 0, 3);
        gridPane.add(twentyField, 1, 3);
        gridPane.add(fiftyText, 0, 4);
        gridPane.add(fiftyField, 1, 4);

        gridPane.add(submitButton, 1, 5);
        gridPane.add(backButton, 0, 5);
        gridPane.add(fillATMMessage, 1, 6);

        Scene fillATMScene = new Scene(gridPane);
        window.setScene(fillATMScene);
        backButton.setOnAction(e -> window.setScene(main));
        submitButton.setOnAction(event -> submitButtonHandler(fillATMMessage, fiveField, tenField, twentyField, fiftyField));

    }

    private void submitButtonHandler(Text fillATMMessage, TextField fiveField, TextField tenField, TextField twentyField, TextField fiftyField){
        BankManager manager = (BankManager) employee;
        Map<BigDecimal, Bills> billsToAdd = new HashMap<>();
        Bills five = new Bills(new BigDecimal(5.00), 0);
        Bills ten = new Bills(new BigDecimal(10.00), 0);
        Bills twenty = new Bills(new BigDecimal(20.00), 0);
        Bills fifty = new Bills(new BigDecimal(50.00), 0);
        fillATMMessage.setText("");
        try {
            int fiveBills = Integer.valueOf(fiveField.getText());
            five.addQuantity(fiveBills);
        } catch (Exception e) {
            fillATMMessage.setText(fillATMMessage.getText() + "\n invalid 5$ amount!");
        }
        try {
            int tenBills = Integer.valueOf(tenField.getText());
            ten.addQuantity(tenBills);
        } catch (Exception e) {
            fillATMMessage.setText(fillATMMessage.getText() + "\n invalid 10$ amount!");
        }
        try {
            int twentyBills = Integer.valueOf(twentyField.getText());
            twenty.addQuantity(twentyBills);
        } catch (Exception e) {
            fillATMMessage.setText(fillATMMessage.getText() + "\n invalid 20$ amount!");
        }
        try {
            int fiftyBills = Integer.valueOf(fiftyField.getText());
            fifty.addQuantity(fiftyBills);
        } catch (Exception e) {
            fillATMMessage.setText(fillATMMessage.getText() + "\n invalid 50$ amount!");
        }
        if (fillATMMessage.getText().equals("")) {
            fillATMMessage.setText("Done!");
        }
        billsToAdd.put(new BigDecimal(5.00), five);
        billsToAdd.put(new BigDecimal(10.00), ten);
        billsToAdd.put(new BigDecimal(20.00), twenty);
        billsToAdd.put(new BigDecimal(50.00), fifty);

        manager.fillATM(machine.getCashMap(), billsToAdd);
    }
}
