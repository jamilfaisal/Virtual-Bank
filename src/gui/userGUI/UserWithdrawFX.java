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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import src.accounts.Account;
import src.BankMachine;
import src.User;
import src.exceptions.NotEnoughBillsException;
import src.exceptions.NotEnoughFundsException;

import java.math.BigDecimal;

public class UserWithdrawFX extends Application {
    private final User user;
    private final Scene userOptionsScene;
    private final BankMachine machine;
    /**
     *  Assigns the logged in user and previous scene
     * @param userOptionsScene The user options scene
     * @param user The user logged in
     */
    public UserWithdrawFX(Scene userOptionsScene, User user, BankMachine machine) {
        this.user = user;
        this.userOptionsScene = userOptionsScene;
        this.machine = machine;
    }

    /**
     * Sets the scene for withdraw
     * @param primaryStage The application window
     */
    @Override
    public void start(Stage primaryStage) {

        // Creates the scene and displays it to the main window
        Scene withDrawPaneSetup = new Scene(withDrawPaneSetup(primaryStage), 1024, 768);
        primaryStage.setScene(withDrawPaneSetup);
    }

    /**
     * The scene for withdraw
     * @param primaryStage The application window
     */
    private GridPane withDrawPaneSetup(Stage primaryStage) {
        GridPane withdrawPane = new GridPane();

        Label accountNameLabel = new Label("Please select the account: ");

        ObservableList<String> accountsObservable = FXCollections.observableArrayList();
        for (Account account: user.getAccounts()) {
            accountsObservable.add(account.getAccountName());
        }

        ComboBox<String> accountsDropDown = new ComboBox<>(accountsObservable);

        Label withdrawAmountLabel = new Label("Please enter the amount you would like to withdraw (in multiples of 5): ");
        TextField withdrawAmountField = new TextField();

        Label userMessage = new Label("");

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            if (accountsDropDown.getValue() != null) {
                String accountName = accountsDropDown.getValue();
                String withdrawAmount = withdrawAmountField.getText();

                boolean validAmount;
                double amountDbl = 0.0;
                BigDecimal amount = new BigDecimal(0);

                try {
                    amountDbl = Double.parseDouble(withdrawAmount);
                    amount = new BigDecimal(amountDbl);

                    validAmount = (amountDbl > 0 && amountDbl % 5 == 0);

                } catch (NumberFormatException except) {
                    validAmount = false;
                }

                Account account = user.identifyAccount(accountName);

                if (account == null) {
                    userMessage.setText("Account not found.");
                } else if (!validAmount) {
                    userMessage.setText("Please enter a valid amount to withdraw. (Must be multiple of 5)");
                } else {
                    try {
                        user.withdraw(amount, account, machine);
                        userMessage.setText("Withdrew $" + amountDbl + " successfully!\n" + "New balance: " + account.getBalance());
                    } catch (NotEnoughBillsException e2) {
                        userMessage.setText("Unable to withdraw. Our ATM seems to be low on bills.");
                    } catch (NotEnoughFundsException e3) {
                        userMessage.setText("Unable to withdraw. Please check that you have sufficient funds.");
                    }
                }
            } else {
                userMessage.setText("You must select an account");
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(this.userOptionsScene));

        //Setting the padding
        withdrawPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        withdrawPane.setVgap(5);
        withdrawPane.setHgap(5);

        //Setting the Grid alignment
        withdrawPane.setAlignment(Pos.CENTER);

        withdrawPane.add(accountNameLabel, 0, 0);
        withdrawPane.add(accountsDropDown, 2, 0);

        withdrawPane.add(withdrawAmountLabel, 0, 2);
        withdrawPane.add(withdrawAmountField, 2, 2);

        withdrawPane.add(userMessage, 0, 6);
        withdrawPane.add(backButton, 0, 8);
        withdrawPane.add(submitButton, 0, 4);

        return withdrawPane;
    }
}
