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
import src.UserHolder;

import java.math.BigDecimal;

public class UserTransferMainFX extends Application {
    private final User user;
    private final Scene userOptionsScene;
    private final UserHolder<User> userHolder;
    public UserTransferMainFX(Scene userOptionsScene, User user, UserHolder<User> userHolder) {
        this.user = user;
        this.userOptionsScene = userOptionsScene;
        this.userHolder = userHolder;
    }
    /**
     * Sets the scene to transferring to other users
     * @param primaryStage The application window
     */
    @Override
    public void start(Stage primaryStage) {

        // Creates the scene and displays it to the main window
        Scene transferToUserPaneSetup = new Scene(transferToUserPaneSetup(primaryStage), 1024, 768);
        primaryStage.setScene(transferToUserPaneSetup);
    }
    /**
     * Sets the scene to transferring between accounts
     * @param primaryStage The application window
     */
    public void startBetween(Stage primaryStage) {
        // Creates the scene and displays it to the main window
        Scene transferBetweenAccountsPaneSetup = new Scene(transferBetweenAccountsPaneSetup(primaryStage), 1024, 768);
        primaryStage.setScene(transferBetweenAccountsPaneSetup);
    }

    /**
     * The scene for transferring to other users
     * @param primaryStage The application window
     */
    private GridPane transferToUserPaneSetup(Stage primaryStage) {
        GridPane transferToUserPane = new GridPane();

        Label transferToUserLabel = new Label("Transfer to Other User: ");
        Label otherUserLabel = new Label("Other Username: ");
        TextField otherUserField = new TextField();
        Label fromAccountLabel =  new Label("Transferring Account: ");

        ObservableList<String> accountsObservable = FXCollections.observableArrayList();
        for (Account account: user.getAccounts()) {
            accountsObservable.add(account.getAccountName());
        }

        ComboBox<String> accountsDropDown = new ComboBox<>(accountsObservable);

        Label dollarAmountLabel = new Label("Amount to Transfer: ");
        TextField dollarAmountField = new TextField();

        Label userMessage = new Label("");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(this.userOptionsScene));
        Button transferButton = new Button("Transfer");
        transferButton.setOnAction(e -> {
            String username = otherUserField.getText();
            User otherUser = userHolder.getUser(username);
            if (otherUser == null) {
                userMessage.setText("User not found.");
            } else {
                if (accountsDropDown.getValue() != null) {
                    String accountName = accountsDropDown.getValue();
                    Account account = user.identifyAccount(accountName);
                    if (account == null) {
                        userMessage.setText("Account not found.");
                    } else {
                        try {
                            BigDecimal amountToTransfer = new BigDecimal(dollarAmountField.getText());
                            if (amountToTransfer.compareTo(BigDecimal.ZERO) > 0) {
                                boolean success = user.userTransfer(amountToTransfer, account, otherUser);
                                if (success) {
                                    userMessage.setText("Transfer Complete.");
                                } else {
                                    userMessage.setText("Transfer Failed.");
                                }
                            } else {
                                userMessage.setText("Amount to transfer must be positive.");
                            }
                        } catch (NumberFormatException exception) {
                            userMessage.setText("Incorrect format for amount to transfer.");
                        }
                    }
                } else {
                    userMessage.setText("You must select an account");
                }
            }
        });
        //Setting the padding
        transferToUserPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        transferToUserPane.setVgap(5);
        transferToUserPane.setHgap(5);

        //Setting the Grid alignment
        transferToUserPane.setAlignment(Pos.CENTER);

        transferToUserPane.add(transferToUserLabel, 0, 0);
        transferToUserPane.add(otherUserLabel, 0, 1);
        transferToUserPane.add(otherUserField, 2, 1);
        transferToUserPane.add(fromAccountLabel, 0, 2);
        transferToUserPane.add(accountsDropDown, 2, 2);
        transferToUserPane.add(dollarAmountLabel, 0, 3);
        transferToUserPane.add(dollarAmountField, 2, 3);
        transferToUserPane.add(userMessage, 1, 4);
        transferToUserPane.add(backButton, 1, 5);
        transferToUserPane.add(transferButton, 2, 5);

        return transferToUserPane;
    }
    /**
     * The scene for transferring between accounts
     * @param primaryStage The application window
     */
    private GridPane transferBetweenAccountsPaneSetup(Stage primaryStage) {
        GridPane transferBetweenAccountsPane = new GridPane();

        Label transferBetweenAccountsMessage = new Label("Transfer Between Accounts: ");

        ObservableList<String> accountsObservable = FXCollections.observableArrayList();
        for (Account account: user.getAccounts()) {
            accountsObservable.add(account.getAccountName());
        }

        Label sendingAccountLabel = new Label("Sending Account: ");
        ComboBox<String> sendingAccountsDropDown = new ComboBox<>(accountsObservable);
        Label receivingAccountLabel = new Label("Receiving Account Name: ");
        ComboBox<String> receivingAccountsDropDown = new ComboBox<>(accountsObservable);

        Label dollarAmountLabel = new Label("Amount to Transfer: ");
        TextField dollarAmountField = new TextField();

        Label userMessage = new Label("");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(this.userOptionsScene));

        Button transferButton = new Button("Transfer");
        transferButton.setOnAction(e -> {
            if (sendingAccountsDropDown.getValue() != null && receivingAccountsDropDown.getValue() != null) {
                String sendingAccountName = sendingAccountsDropDown.getValue();
                Account sendingAccount = this.user.identifyAccount(sendingAccountName);
                if (sendingAccount == null) {
                    userMessage.setText("Sending Account Not Found.");
                } else {
                    String receivingAccountName = receivingAccountsDropDown.getValue();
                    Account receivingAccount = this.user.identifyAccount(receivingAccountName);
                    if (receivingAccount == null) {
                        userMessage.setText("Receiving Account Not Found.");
                    } else {
                        try {
                            BigDecimal amountToTransfer = new BigDecimal(dollarAmountField.getText());
                            if (amountToTransfer.compareTo(BigDecimal.ZERO) > 0) {
                                boolean success = user.innerTransfer(amountToTransfer, sendingAccount, receivingAccount);
                                if (success) {
                                    userMessage.setText("Transfer Complete!");
                                } else {
                                    userMessage.setText("Failed to Transfer.");
                                }
                            } else {
                                userMessage.setText("Amount to transfer must be positive.");
                            }
                        } catch (NumberFormatException exception) {
                            userMessage.setText("Incorrect format for account to transfer.");
                        }
                    }
                }
            } else {
                userMessage.setText("You must select an account");
            }
        });

        //Setting the padding
        transferBetweenAccountsPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        transferBetweenAccountsPane.setVgap(5);
        transferBetweenAccountsPane.setHgap(5);

        //Setting the Grid alignment
        transferBetweenAccountsPane.setAlignment(Pos.CENTER);

        transferBetweenAccountsPane.add(transferBetweenAccountsMessage, 0, 0);
        transferBetweenAccountsPane.add(sendingAccountLabel, 0, 1);
        transferBetweenAccountsPane.add(sendingAccountsDropDown, 2, 1);
        transferBetweenAccountsPane.add(receivingAccountLabel, 0, 2);
        transferBetweenAccountsPane.add(receivingAccountsDropDown, 2, 2);
        transferBetweenAccountsPane.add(dollarAmountLabel, 0, 3);
        transferBetweenAccountsPane.add(dollarAmountField, 2, 3);
        transferBetweenAccountsPane.add(userMessage, 1, 4);
        transferBetweenAccountsPane.add(backButton, 0, 5);
        transferBetweenAccountsPane.add(transferButton, 2, 5);

        return transferBetweenAccountsPane;
    }
}
