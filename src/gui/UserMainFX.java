package src.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import src.*;
import src.gui.userGUI.*;
import src.utils.HttpUtility;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * UserMainFX class
 * The main user GUI class
 * Calls all other possible GUI options
 */
public class UserMainFX extends Application {
    private final Scene mainMenuScene;
    private Scene userOptionsScene;
    private final User user;
    private final UserHolder<User> userHolder;
    private final UserHolder<Employee> employeeHolder;
    private final BankMachine machine;

    /**
     *  Assigns the logged in managerGUI and previous scene
     * @param mainMenuScene The mainMenu Scene
     * @param machine   The bank machine
     * @param user The logged in user
     * @param userHolder The holder of users
     */
    UserMainFX(Scene mainMenuScene, User user, UserHolder<User> userHolder, UserHolder<Employee> employeeHolder, BankMachine machine) {
        this.user = user;
        this.userHolder = userHolder;
        this.employeeHolder = employeeHolder;
        this.machine = machine;
        this.mainMenuScene = mainMenuScene;
    }

    /**
     * Displays the User options scene
     * @param primaryStage The application window
     */
    @Override
    public void start(Stage primaryStage) {
        GridPane userPaneOptions = userPaneOptionsSetup(primaryStage);
        //Creating a scene object
        primaryStage.setTitle("User Panel");
        this.userOptionsScene = new Scene(userPaneOptions, 1024, 768);
        primaryStage.setScene(userOptionsScene);
    }

    /**
     * Setup the main user menu.
     * @param primaryStage The stage on which the interface will be displayed.
     * @return GridPane corresponding to the main user menu is returned.
     */
    private GridPane userPaneOptionsSetup(Stage primaryStage) {
        GridPane userPaneOptions = new GridPane();

        Label userChoiceLabel = new Label("Welcome " + user.getUsername() + ", Please choose one of the following: ");
        Label RFIDInfoLabel = new Label("");

        //Creating Buttons
        Button infoButton = new Button("View Information");
        infoButton.setOnAction(e -> {
            UserInfoMainFX userInfoMainFX = new UserInfoMainFX(this.userOptionsScene, this.user);
            userInfoMainFX.start(primaryStage);
        });

        Button viewAccountBalanceButton = new Button("View Account Balance");
        viewAccountBalanceButton.setOnAction(e -> {
            UserAccountBalanceFX userAccountBalance = new UserAccountBalanceFX(this.userOptionsScene, this.user);
            userAccountBalance.start(primaryStage);
        });

        Button payBillButton = new Button("Pay Bill");
        payBillButton.setOnAction(e -> {
            UserPayBillFX userPayBillFX = new UserPayBillFX(this.userOptionsScene, this.user);
            userPayBillFX.start(primaryStage);
        });

        Button createAccountButton = new Button("Create a new account");
        createAccountButton.setOnAction(e -> {
            UserEmployeeLoginFX userEmployeeLoginFX = new UserEmployeeLoginFX(this.userOptionsScene, this.user, this.employeeHolder, true, null, null);
            userEmployeeLoginFX.start(primaryStage);
        });

        Button requestJointButton = new Button("Make an account joint");
        requestJointButton.setOnAction(e -> {
            UserJointFX userJointFX = new UserJointFX(this.userOptionsScene, this.user, this.userHolder, this.employeeHolder);
            userJointFX.start(primaryStage);
        });

        Button transferButtonUser = new Button("Transfer Money to Other User");
        transferButtonUser.setOnAction(e -> {
            UserTransferMainFX userTransferMainFX = new UserTransferMainFX(this.userOptionsScene, this.user, this.userHolder);
            userTransferMainFX.start(primaryStage);
        });

        Button transferButtonAccount = new Button("Transfer Money Between Accounts");
        transferButtonAccount.setOnAction(e -> {
            UserTransferMainFX userTransferMainFX = new UserTransferMainFX(this.userOptionsScene, this.user, this.userHolder);
            userTransferMainFX.startBetween(primaryStage);
        });

        Button withdrawButton = new Button("Withdraw Money");
        withdrawButton.setOnAction(e -> {
            UserWithdrawFX userWithdrawFX = new UserWithdrawFX(this.userOptionsScene, this.user, this.machine);
            userWithdrawFX.start(primaryStage);
        });

        Button depositButton = new Button("Deposit Money");
        depositButton.setOnAction(e -> {
            UserDepositFX userDepositFX = new UserDepositFX(this.userOptionsScene, this.user, this.machine);
            userDepositFX.start(primaryStage);
        });

        Button passwordButton = new Button("Change password");
        passwordButton.setOnAction(e -> {
            UserChangePassFX userChangePassFX = new UserChangePassFX(this.userOptionsScene, this.user);
            userChangePassFX.start(primaryStage);
        });

        Button stockButton = new Button("Enter stock trading");
        stockButton.setOnAction(e -> {
            UserStockMainFX userStockMainFX = new UserStockMainFX(this.userOptionsScene, this.user);
            userStockMainFX.start(primaryStage);
        });

        Button RFIDButton = new Button("Set RFID Card");
        RFIDButton.setOnAction(e -> SetRFID(RFIDInfoLabel));

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> {
            primaryStage.setTitle("Login");
            primaryStage.setScene(mainMenuScene);
        });

        //Setting the padding
        userPaneOptions.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        userPaneOptions.setVgap(5);
        userPaneOptions.setHgap(5);

        //Setting the Grid alignment
        userPaneOptions.setAlignment(Pos.CENTER);

        //Arranging all the nodes in the grid
        userPaneOptions.add(userChoiceLabel, 0, 0);
        userPaneOptions.add(infoButton, 0, 1);
        userPaneOptions.add(stockButton, 1, 1);
        userPaneOptions.add(viewAccountBalanceButton, 0, 2);
        userPaneOptions.add(payBillButton, 1, 2);
        userPaneOptions.add(transferButtonUser, 0, 3);
        userPaneOptions.add(transferButtonAccount, 1, 3);
        userPaneOptions.add(withdrawButton, 0, 4);
        userPaneOptions.add(depositButton, 1, 4);
        userPaneOptions.add(passwordButton, 0, 5);
        userPaneOptions.add(createAccountButton, 0, 6);
        userPaneOptions.add(requestJointButton, 0, 7);
        userPaneOptions.add(RFIDButton, 0, 8);
        userPaneOptions.add(RFIDInfoLabel, 1, 8);
        userPaneOptions.add(exitButton, 1, 9);

        return userPaneOptions;
    }

    /**
     * Sets a RFID card to the User
     * @param RFIDInfoLabel The info label for RFID
     */
    private void SetRFID(Label RFIDInfoLabel) {
        RFIDInfoLabel.setText("Please hold the RFID card to the reader until the operation ends.");

        Map<String, String> params = new HashMap<>();
        String requestURL = "http://csc207group0347.localtunnel.me/write";
        params.put("username", user.getUsername());

        try {
            HttpUtility.sendPostRequest(requestURL, params);
            String[] response = HttpUtility.readMultipleLinesRespone();
            String newRFID = response[0].trim();
            user.setRFID(newRFID);
            RFIDInfoLabel.setText("Operation successful. RFID card's UID: " + newRFID);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpUtility.disconnect();
    }
}