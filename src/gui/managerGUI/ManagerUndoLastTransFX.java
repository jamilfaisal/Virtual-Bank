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
import src.*;
import src.accounts.Account;

public class ManagerUndoLastTransFX extends Application {

    private Stage window;
    private final Employee employee;
    private final Scene main;
    private final UserHolder<User> userHolder;

    public ManagerUndoLastTransFX(Employee employee, UserHolder<User> userUserHolder, Scene main){
        this.employee = employee;
        this.userHolder = userUserHolder;
        this.main = main;
    }
    /**
     * The scene for undoing the latest transactions.
     */
    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        Text usernameText = new Text("Username: ");
        TextField usernameField = new TextField();

        Text accountNameText = new Text("Account Name:");
        TextField accountNameField = new TextField();

        Text amountTransactionText = new Text("Amount of transactions to be undone: ");
        TextField amountTransactionField = new TextField();

        if (employee instanceof Teller) {
            amountTransactionField.setText("1");
            amountTransactionField.setDisable(true);
        }

        Text undoLastTransMessage = new Text("");
        Text userMessage = new Text("");
        Text accountMessage = new Text("");

        //Creating Buttons
        Button submitButton = new Button("Submit");
        Button backButton = new Button("Back");

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        gridPane.setMinSize(1024, 768);

        //Setting the padding
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(usernameText, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(userMessage, 2, 0);
        gridPane.add(accountNameText, 0, 1);
        gridPane.add(accountNameField, 1, 1);
        gridPane.add(accountMessage, 2, 1);
        gridPane.add(amountTransactionText,0,2);
        gridPane.add(amountTransactionField, 1, 2);
        gridPane.add(submitButton, 1, 3);
        gridPane.add(backButton, 0, 3);
        gridPane.add(undoLastTransMessage, 1, 4);

        Scene undoLastTransScene = new Scene(gridPane);
        window.setScene(undoLastTransScene);
        backButton.setOnAction(e -> window.setScene(main));
        submitButton.setOnAction(event -> submitButtonHandler(usernameField, userMessage, accountMessage, undoLastTransMessage, accountNameField, amountTransactionField));
    }
    private void submitButtonHandler(TextField usernameField, Text userMessage, Text accountMessage, Text undoLastTransMessage, TextField accountNameField, TextField amountTransactionField){

        User user = userHolder.getUser(usernameField.getText());
        if (user == null) {
            userMessage.setText("User not found!");
            undoLastTransMessage.setText("Process Failed.");
        } else {
            Account account = user.identifyAccount(accountNameField.getText());
            if (account == null) {
                accountMessage.setText("Account not found.");
                undoLastTransMessage.setText("Process Failed.");
            } else {
                try{
                    int numberOfTransactions = Integer.parseInt(amountTransactionField.getText());
                    if (numberOfTransactions > 0) {
                        boolean success;
                        if (employee instanceof BankManager) {
                            success = ((BankManager) employee).undoLatestTransactions(account, numberOfTransactions);
                        } else {
                            success = employee.undoLatestTransaction(account);
                        }
                        if (success) {
                            undoLastTransMessage.setText("Transaction Successfully Undone.");
                            userMessage.setText("");
                            accountMessage.setText("");
                        } else {
                            undoLastTransMessage.setText("Process Failed, can't undo that many transactions.");
                        }
                    } else {
                        undoLastTransMessage.setText("Must enter a positive integer for number of transactions.");
                    }
                } catch (NumberFormatException e) {
                    undoLastTransMessage.setText("Incorrect format for number of transactions.");
                }
            }
        }

    }
}
