package src.gui.userGUI;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import src.accounts.Account;
import src.Transaction;
import src.User;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;

public class UserInfoMainFX extends Application {

    private final User user;
    private final Scene userOptionsScene;
    private Scene userViewInfoOptionsScene;

    /**
     *  Assigns the logged in user and previous scene
     * @param userOptionsScene The user options scene
     * @param user The user logged in
     */
    public UserInfoMainFX(Scene userOptionsScene, User user) {
        this.user = user;
        this.userOptionsScene = userOptionsScene;
    }

    /**
     * Displays the Information options scene
     * @param primaryStage The application window
     */
    @Override
    public void start(Stage primaryStage) {

        // Creates the scene and displays it to the main window
        this.userViewInfoOptionsScene = new Scene(userViewInfoOptionsPaneSetup(primaryStage), 1024, 768);
        primaryStage.setScene(userViewInfoOptionsScene);
    }

    /**
     * Creates the grid pane for viewing user's information scene
     * @param primaryStage The application window
     * @return the finished gridpane
     */
    private GridPane userViewInfoOptionsPaneSetup(Stage primaryStage) {
        GridPane userViewInfoOptionsPane = new GridPane();

        Label userMessageLabel = new Label("View Information About Your Account: ");
        Label userNetBalanceLabel = new Label("Your Balance: " + new DecimalFormat("#0.##").format(this.user.getNetTotal()));


        Button viewAccountsSummaryButton = new Button("View Accounts Summary");
        viewAccountsSummaryButton.setOnAction(e -> primaryStage.setScene(new Scene(userViewAccountSummaryPaneSetup(primaryStage), 1024, 768)));

        Button viewLatestTransactionButton = new Button("View Latest Transaction on an Account");
        viewLatestTransactionButton.setOnAction(e -> primaryStage.setScene(new Scene(userViewLatestTransactionPaneSetup(primaryStage), 1024, 768)));

        Button viewAccountCreationDateButton = new Button("View Account Creation Date");
        viewAccountCreationDateButton.setOnAction(e -> primaryStage.setScene(new Scene(userViewAccountCreationDatePaneSetup(primaryStage), 1024, 768)));

        Button backButton = new Button("Go Back");
        backButton.setOnAction(e -> primaryStage.setScene(this.userOptionsScene));

        //Setting the padding
        userViewInfoOptionsPane.setPadding(new Insets(20, 20, 20, 20));

        //Setting the vertical and horizontal gaps between the columns
        userViewInfoOptionsPane.setVgap(5);
        userViewInfoOptionsPane.setHgap(5);

        //Setting the Grid alignment
        userViewInfoOptionsPane.setAlignment(Pos.CENTER);

        userViewInfoOptionsPane.add(userMessageLabel, 0, 0);
        userViewInfoOptionsPane.add(userNetBalanceLabel, 0, 1);
        userViewInfoOptionsPane.add(viewAccountsSummaryButton, 0, 2);
        userViewInfoOptionsPane.add(viewLatestTransactionButton, 2, 2);
        userViewInfoOptionsPane.add(viewAccountCreationDateButton, 0, 3);
        userViewInfoOptionsPane.add(backButton, 1, 4);

        return userViewInfoOptionsPane;
    }

    /**
     *  Creates the gridpane for viewing the summary of all accounts
     * @param primaryStage The application window
     * @return The finished grid pane
     */
    private GridPane userViewAccountSummaryPaneSetup(Stage primaryStage) {
        GridPane userViewAccountsSummaryPane = new GridPane();

        // Retrieves the balances of the accounts for the user
        Map<Account, BigDecimal> accountBalances = user.getAccountBalances();

        // Sets the headers
        Label firstColumnHeader = new Label("Account Name");
        Label secondColumnHeader = new Label("Account Type");
        Label thirdColumnHeader = new Label("Account Balance");
        Label fourthColumnHeader = new Label("Currency");
        Label fifthColumnHeader = new Label("Joint owner");
        Label sixthColumnHeader = new Label("Status");
        Button backButton = new Button("Go Back");
        backButton.setOnAction(e -> primaryStage.setScene(this.userOptionsScene));

        userViewAccountsSummaryPane.add(firstColumnHeader, 0, 0);
        userViewAccountsSummaryPane.add(secondColumnHeader, 1, 0);
        userViewAccountsSummaryPane.add(thirdColumnHeader, 2, 0);
        userViewAccountsSummaryPane.add(fourthColumnHeader, 3, 0);
        userViewAccountsSummaryPane.add(fifthColumnHeader, 4, 0);
        userViewAccountsSummaryPane.add(sixthColumnHeader, 5, 0);


        // Loops through the account balances and adds them to the gridpane
        int i = 1;
        for (Map.Entry<Account, BigDecimal> entry: accountBalances.entrySet()) {
            userViewAccountsSummaryPane.add(new Label(entry.getKey().getAccountName()), 0, i);
            userViewAccountsSummaryPane.add(new Label(entry.getKey().getClass().toString()), 1, i);
            userViewAccountsSummaryPane.add(new Label(new DecimalFormat("#0.##").format(entry.getValue())), 2, i);
            userViewAccountsSummaryPane.add(new Label(entry.getKey().getCurrency()), 3, i);
            userViewAccountsSummaryPane.add(new Label(entry.getKey().getJoint(this.user.getUsername())), 4, i);
            userViewAccountsSummaryPane.add(new Label(entry.getKey().isActive()), 5, i);
            i++;
        }
        userViewAccountsSummaryPane.add(backButton, 0, i+1);

        backButton.setOnAction(e -> primaryStage.setScene(userViewInfoOptionsScene));

        //Setting the padding
        userViewAccountsSummaryPane.setPadding(new Insets(20, 20, 20, 20));

        //Setting the vertical and horizontal gaps between the columns
        userViewAccountsSummaryPane.setVgap(5);
        userViewAccountsSummaryPane.setHgap(5);


        //Setting the Grid alignment
        userViewAccountsSummaryPane.setAlignment(Pos.CENTER);

        return userViewAccountsSummaryPane;
    }

    /**
     *  Creates the grid pane for viewing the latest transaction for a specific account
     * @param primaryStage The application window
     * @return The finished grid pane
     */
    private GridPane userViewLatestTransactionPaneSetup(Stage primaryStage) {
        GridPane userViewLatestTransactionPane = new GridPane();

        Label accountNameLabel = new Label("Please select an account:");

        ObservableList<String> accountsObservable = FXCollections.observableArrayList();
        for (Account account: user.getAccounts()) {
            accountsObservable.add(account.getAccountName());
        }

        ComboBox<String> accountsDropDown = new ComboBox<>(accountsObservable);

        Label userMessage = new Label("");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(userViewInfoOptionsScene));

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            if (accountsDropDown.getValue() != null) {
                String accountName = accountsDropDown.getValue();
                // Checks the account
                Account account = this.user.identifyAccount(accountName);
                if (account == null) {
                    userMessage.setText("Account not found.");
                }
                else {
                    // Gets the latest transaction
                    Transaction recentTransaction = user.getRecentTransaction(account);
                    if (recentTransaction == null) {
                        userMessage.setText("No transactions found.");
                    } else {
                        userMessage.setText(recentTransaction.toString());
                    }
                }
            } else {
                userMessage.setText("You must select an account");
            }
        });

        //Setting the padding
        userViewLatestTransactionPane.setPadding(new Insets(20, 20, 20, 20));

        //Setting the vertical and horizontal gaps between the columns
        userViewLatestTransactionPane.setVgap(5);
        userViewLatestTransactionPane.setHgap(5);

        //Setting the Grid alignment
        userViewLatestTransactionPane.setAlignment(Pos.CENTER);

        userViewLatestTransactionPane.add(accountNameLabel, 0, 0);
        userViewLatestTransactionPane.add(accountsDropDown, 1, 0);
        userViewLatestTransactionPane.add(userMessage, 1, 1);
        userViewLatestTransactionPane.add(submitButton, 1, 2);
        userViewLatestTransactionPane.add(backButton, 0, 2);
        return userViewLatestTransactionPane;
    }

    /**
     * Creates the grid pane for viewing the account creation date
     * @param primaryStage The application window
     * @return The finished grid pane
     */
    private GridPane userViewAccountCreationDatePaneSetup(Stage primaryStage) {
        GridPane userViewAccountCreationDatePane = new GridPane();
        Label accountNameLabel = new Label("Please select an account: ");

        ObservableList<String> accountsObservable = FXCollections.observableArrayList();
        for (Account account: user.getAccounts()) {
            accountsObservable.add(account.getAccountName());
        }

        ComboBox<String> accountsDropDown = new ComboBox<>(accountsObservable);

        Label userMessage = new Label("");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(this.userViewInfoOptionsScene));

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            if (accountsDropDown.getValue() != null) {
                String accountName = accountsDropDown.getValue();
                // Finds the account
                Account account = this.user.identifyAccount(accountName);
                if (account == null) {
                    userMessage.setText("Account not found.");
                }
                else {
                    // Displays creation date of the account
                    userMessage.setText(account.getCreationDate().toString());
                }
            } else {
                userMessage.setText("You must select an account");
            }
        });

        //Setting the padding
        userViewAccountCreationDatePane.setPadding(new Insets(20, 20, 20, 20));

        //Setting the vertical and horizontal gaps between the columns
        userViewAccountCreationDatePane.setVgap(5);
        userViewAccountCreationDatePane.setHgap(5);

        //Setting the Grid alignment
        userViewAccountCreationDatePane.setAlignment(Pos.CENTER);


        userViewAccountCreationDatePane.add(accountNameLabel, 0, 0);
        userViewAccountCreationDatePane.add(accountsDropDown, 1, 0);
        userViewAccountCreationDatePane.add(userMessage, 1, 1);
        userViewAccountCreationDatePane.add(backButton, 0, 2);
        userViewAccountCreationDatePane.add(submitButton, 1, 2);

        return userViewAccountCreationDatePane;
    }
}
