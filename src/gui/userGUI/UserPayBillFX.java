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
import src.User;

import java.math.BigDecimal;


public class UserPayBillFX extends Application {
    private final User user;
    private final Scene userOptionsScene;

    /**
     *  Assigns the logged in user and previous scene
     * @param userOptionsScene The user options scene
     * @param user The user logged in
     */
    public UserPayBillFX(Scene userOptionsScene, User user) {
        this.user = user;
        this.userOptionsScene = userOptionsScene;
    }

    /**
     * Sets the scene for paying bills
     * @param primaryStage The application window
     */
    @Override
    public void start(Stage primaryStage) {

        // Creates the scene and displays it to the main window
        Scene payBillPaneSetup = new Scene(payBillPaneSetup(primaryStage), 1024, 768);
        primaryStage.setScene(payBillPaneSetup);
    }

    /**
     * The scene for paying bills
     * @param primaryStage The application window
     */
    private GridPane payBillPaneSetup(Stage primaryStage) {
        GridPane payBillPane = new GridPane();

        Label payBillLabel = new Label("Pay Bill:");
        Label accountNameLabel = new Label("Account Name: ");

        ObservableList<String> accountsObservable = FXCollections.observableArrayList();
        for (Account account: user.getAccounts()) {
            accountsObservable.add(account.getAccountName());
        }

        ComboBox<String> accountsDropDown = new ComboBox<>(accountsObservable);
        Label amountLabel = new Label("Amount to Pay: ");
        TextField amountField = new TextField();

        Label userMessage = new Label("");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(this.userOptionsScene));
        Button payButton = new Button("Pay");
        payButton.setOnAction(e -> {
            if (accountsDropDown.getValue() != null) {
                String accountName = accountsDropDown.getValue();
                Account account = user.identifyAccount(accountName);
                if (account == null) {
                    userMessage.setText("Account not found.");
                } else {
                    try {
                        BigDecimal amountToPay = new BigDecimal(amountField.getText());
                        if (amountToPay.compareTo(BigDecimal.ZERO) > 0) {
                            boolean paymentSuccessful = user.payBill(amountToPay, account);
                            if (paymentSuccessful) {
                                userMessage.setText("Payment successful!");
                            } else {
                                userMessage.setText("Payment failed.");
                            }
                        } else {
                            userMessage.setText("Amount to transfer must be positive.");
                        }
                    } catch (NumberFormatException exception) {
                        userMessage.setText("Incorrect format for amount to pay.");
                    }
                }
            } else {
                userMessage.setText("You must select an account");
            }
        });

        //Setting the padding
        payBillPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        payBillPane.setVgap(5);
        payBillPane.setHgap(5);

        //Setting the Grid alignment
        payBillPane.setAlignment(Pos.CENTER);

        payBillPane.add(payBillLabel, 0, 0);
        payBillPane.add(accountNameLabel, 0, 1);
        payBillPane.add(accountsDropDown, 2, 1);
        payBillPane.add(amountLabel, 0, 2);
        payBillPane.add(amountField, 2, 2);
        payBillPane.add(userMessage, 1, 3);
        payBillPane.add(backButton, 1, 4);
        payBillPane.add(payButton, 2, 4);

        return payBillPane;
    }
}
