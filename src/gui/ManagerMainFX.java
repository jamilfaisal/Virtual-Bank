package src.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import src.*;
import src.gui.managerGUI.*;

public class ManagerMainFX extends Application {
    private Stage window;
    private final Scene mainMenuScene;
    private Scene main;

    private final BankMachine machine;
    private final Employee employee;
    private final UserHolder<User> userHolder;

    /**
     *  Assigns the logged in managerGUI and previous scene
     * @param mainMenuScene The mainMenu Scene
     * @param machine   The bank machine
     * @param employee The user logged in
     */
    ManagerMainFX(Scene mainMenuScene, BankMachine machine, Employee employee, UserHolder<User> userHolder) {
        this.employee = employee;
        this.machine = machine;
        this.userHolder = userHolder;
        this.mainMenuScene = mainMenuScene;
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Displays the Employee options scene
     * @param primaryStage The application window
     */
    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        Text text1 = new Text("Welcome " + employee.getUsername() + ",\n\n Please choose one of the following: ");
        //Creating Buttons
        Button createUserButton = new Button("Create User");
        Button createEmployeeButton = new Button("Create Employee");
        Button createAccountButton = new Button("Create Account");
        Button fillATMButton = new Button("Fill ATM");
        Button fulfillDepositButton = new Button("Fulfill Deposits");
        Button undoLastTransButton = new Button("Undo Latest Transaction");
        Button billStatusButton = new Button("ATM Bill Status");
        Button exitButton = new Button("Exit");

        GridPane gridPane = new GridPane();

        //Setting size for the pane
        gridPane.setMinSize(1024, 768);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);
//        gridPane.gridLinesVisibleProperty().setValue(true);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        //Arranging all the nodes in the grid
        gridPane.add(text1, 0, 0);
        gridPane.add(createUserButton, 0, 1);
        gridPane.add(createEmployeeButton, 0, 2);
        gridPane.add(createAccountButton, 0, 3);
        gridPane.add(fillATMButton, 1, 4);
        gridPane.add(billStatusButton,0,4);
        gridPane.add(fulfillDepositButton, 0, 5);
        gridPane.add(undoLastTransButton, 0, 6);
        gridPane.add(exitButton, 1, 7);

        gridPane.setAlignment(Pos.CENTER);
        //Creating a scene object
        this.main = new Scene(gridPane);
        primaryStage.setTitle("Employee Panel");
        primaryStage.setScene(main);
//        primaryStage.initStyle(StageStyle.UNDECORATED);

        createUserButton.setOnAction(e -> {
            ManagerCreateUserFX createUser = new ManagerCreateUserFX(employee, main);
            createUser.start(primaryStage);
        });
        createEmployeeButton.setOnAction(e -> {
            ManagerCreateEmployeeFX createEmployee = new ManagerCreateEmployeeFX(employee, main);
            createEmployee.start(primaryStage);
        });
        createAccountButton.setOnAction(e -> {
            ManagerCreateAccountFX createAccount = new ManagerCreateAccountFX(employee, main);
            createAccount.start(primaryStage);
        });
        billStatusButton.setOnAction(e -> {
            ManagerBillStatusFX billStatus = new ManagerBillStatusFX(machine, main);
            billStatus.start(primaryStage);
        });
        fillATMButton.setOnAction(e -> {
            ManagerFillATMFX fillATM = new ManagerFillATMFX(employee, machine, main);
            fillATM.start(primaryStage);
        });
        fulfillDepositButton.setOnAction(e -> {
            ManagerFulfillDepositFX fulfillDeposit = new ManagerFulfillDepositFX(employee, machine, main);
            fulfillDeposit.start(primaryStage);
        });
        undoLastTransButton.setOnAction(e -> {
            ManagerUndoLastTransFX undoLastTrans = new ManagerUndoLastTransFX(employee, userHolder, main);
            undoLastTrans.start(primaryStage);
        });
        exitButton.setOnAction(e -> {
            try {
                primaryStage.setTitle("Login");
                window.setScene(mainMenuScene);
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        });

        if (employee instanceof Teller) {
            createUserButton.setDisable(true);
            createEmployeeButton.setDisable(true);
            createAccountButton.setDisable(true);
            fillATMButton.setDisable(true);
        }

        primaryStage.show();

    }

}
