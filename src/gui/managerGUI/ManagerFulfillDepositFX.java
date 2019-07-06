package src.gui.managerGUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import src.BankMachine;
import src.Employee;

import java.util.Map;

public class ManagerFulfillDepositFX extends Application {

    private Stage window;
    private final BankMachine machine;
    private final Employee employee;
    private final Scene main;

    public ManagerFulfillDepositFX(Employee employee, BankMachine machine, Scene main){
        this.machine = machine;
        this.employee = employee;
        this.main = main;
    }
    /**
     * The scene for fulfilling the deposits.
     */
    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;

        Button fulfillButton = new Button("Fulfill Deposits");
        Text fulfillMessage = new Text("");
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
        gridPane.add(fulfillButton, 1, 0);
        gridPane.add(backButton, 0, 0);
        gridPane.add(fulfillMessage, 1, 2);

        Scene fulfillDepositScene = new Scene(gridPane);
        window.setScene(fulfillDepositScene);
        backButton.setOnAction(e -> window.setScene(main));
        fulfillButton.setOnAction(e -> submitButtonHandler(fulfillMessage));

    }
    private void submitButtonHandler(Text fulfillMessage){
        Map<String, Exception> failures = employee.fulfillDeposits("./deposits.txt", machine);
        if (failures == null) {
            fulfillMessage.setText("There was an error reading the file.");
        } else if (failures.size() > 0) {
            StringBuilder failMessage;
            failMessage = new StringBuilder("Deposits have been fulfilled, however the following failed:\n");
            for (Map.Entry<String, Exception> entry : failures.entrySet()) {
                failMessage.append("line: ").append(entry.getKey()).append("\n");
                failMessage.append(entry.getValue().getClass()).append(": ");
                failMessage.append(entry.getValue().getMessage()).append("\n");
            }
            fulfillMessage.setText(failMessage.toString());
        } else {
            fulfillMessage.setText("All deposits have been fulfilled");
        }
    }
}
