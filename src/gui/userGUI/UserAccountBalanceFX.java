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
import src.User;

import java.text.DecimalFormat;

public class UserAccountBalanceFX extends Application {
    private final User user;
    private final Scene userOptionsScene;

    /**
     *  Assigns the logged in user and previous scene
     * @param userOptionsScene The user options scene
     * @param user The user logged in
     */
    public UserAccountBalanceFX(Scene userOptionsScene, User user) {
        this.user = user;
        this.userOptionsScene = userOptionsScene;
    }

    /**
     * Sets the scene for viewing account balance
     * @param primaryStage The application window
     */
    @Override
    public void start(Stage primaryStage) {

        // Creates the scene and displays it to the main window
        Scene userViewAccountBalancePaneSetup = new Scene(userViewAccountBalancePaneSetup(primaryStage), 1024, 768);
        primaryStage.setScene(userViewAccountBalancePaneSetup);
    }
    /**
     * The scene for account balance
     * @param primaryStage The application window
     */
    private GridPane userViewAccountBalancePaneSetup(Stage primaryStage) {
        GridPane userViewAccountBalancePane = new GridPane();

        Label accountNameLabel = new Label("Please select an account: ");

        ObservableList<String> accountsObservable = FXCollections.observableArrayList();
        for (Account account: user.getAccounts()) {
            accountsObservable.add(account.getAccountName());
        }

        ComboBox<String> accountsDropDown = new ComboBox<>(accountsObservable);

        Label userMessage = new Label("");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            if (accountsDropDown.getValue() != null) {
                String accountName = accountsDropDown.getValue();
                Account account = user.identifyAccount(accountName);

                if (account == null) {
                    userMessage.setText("Account not found.");
                } else {
                    userMessage.setText("Balance: " + new DecimalFormat("#0.##").format(account.getBalance())
                    + " " + account.getCurrency());
                }
            } else {
                userMessage.setText("You must select an account");
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(this.userOptionsScene));

        //Setting the padding
        userViewAccountBalancePane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        userViewAccountBalancePane.setVgap(5);
        userViewAccountBalancePane.setHgap(5);

        //Setting the Grid alignment
        userViewAccountBalancePane.setAlignment(Pos.CENTER);

        userViewAccountBalancePane.add(accountNameLabel, 0, 0);
        userViewAccountBalancePane.add(accountsDropDown, 2, 0);
        userViewAccountBalancePane.add(userMessage, 1, 1);
        userViewAccountBalancePane.add(backButton, 0, 2);
        userViewAccountBalancePane.add(submitButton, 1, 2);

        return userViewAccountBalancePane;
    }
}
