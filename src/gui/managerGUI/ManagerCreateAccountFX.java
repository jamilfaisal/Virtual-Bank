package src.gui.managerGUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import src.*;
import src.utils.CurrencyConverter;

import java.io.IOException;

public class ManagerCreateAccountFX extends Application {

    private Stage window;
    private final Employee employee;
    private final Scene main;

    public ManagerCreateAccountFX(Employee employee, Scene main){
        this.employee = employee;
        this.main = main;
    }
    /**
     * The scene for creating accounts.
     */
    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;

        Text usernameText = new Text("Username: ");
        TextField usernameField = new TextField();

        ToggleGroup accountTypeGroup = new ToggleGroup();

        Text accountTypeText = new Text("Select Account Type:");
        RadioButton chequingButton = new RadioButton("Chequing");
        RadioButton savingsButton = new RadioButton("Savings");
        RadioButton creditCardButton = new RadioButton("Credit Card");
        RadioButton lineOfCreditButton = new RadioButton("Line Of Credit");
        RadioButton stocksButton = new RadioButton("Stocks");

        chequingButton.setToggleGroup(accountTypeGroup);
        savingsButton.setToggleGroup(accountTypeGroup);
        creditCardButton.setToggleGroup(accountTypeGroup);
        lineOfCreditButton.setToggleGroup(accountTypeGroup);
        stocksButton.setToggleGroup(accountTypeGroup);

        chequingButton.setTooltip(new Tooltip("Chequing"));
        savingsButton.setTooltip(new Tooltip("Savings"));
        creditCardButton.setTooltip(new Tooltip("CreditCard"));
        lineOfCreditButton.setTooltip(new Tooltip("LineOfCredit"));
        stocksButton.setTooltip(new Tooltip("StockAccount"));

        Text accountNameText = new Text("Account Name (alphanumeric):");
        TextField accountNameField = new TextField();

        Text accountCurrencyText = new Text("Currency (3 letters)");
        TextField accountCurrencyField = new TextField();

        Text accountCreatingMessage = new Text("");

        //Creating Buttons
        Button submitButton = new Button("Submit");
        Button backButton = new Button("Back");

        accountTypeGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            RadioButton rb = (RadioButton) accountTypeGroup.getSelectedToggle();
            if (rb == stocksButton) {
                accountCurrencyField.setText("USD");
                accountCurrencyField.setDisable(true);
            } else {
                accountCurrencyField.setText("");
                accountCurrencyField.setDisable(false);
            }
        });

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        gridPane.setMinSize(1024, 768);

        //Setting the padding
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);
//        gridPane.gridLinesVisibleProperty().setValue(true);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        //Arranging all the nodes in the grid
        gridPane.add(usernameText, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(accountTypeText, 0, 1);
        gridPane.add(chequingButton, 0, 2);
        gridPane.add(savingsButton, 0, 3);
        gridPane.add(creditCardButton, 0, 4);
        gridPane.add(lineOfCreditButton, 0, 5);
        gridPane.add(stocksButton, 0, 6);

        gridPane.add(accountNameText, 1, 1);
        gridPane.add(accountNameField, 1, 2);
        gridPane.add(accountCurrencyText, 1, 3);
        gridPane.add(accountCurrencyField, 1, 4);

        gridPane.add(submitButton, 1, 5);
        gridPane.add(backButton, 1, 6);
        gridPane.add(accountCreatingMessage, 1, 7);

        Scene createAccountScene = new Scene(gridPane);
        window.setScene(createAccountScene);
        backButton.setOnAction(e -> window.setScene(main));

        submitButton.setOnAction(e -> submitButtonHandler(usernameField, accountNameField, accountCreatingMessage, accountTypeGroup, accountCurrencyField));

    }
    private void submitButtonHandler(TextField usernameField, TextField accountNameField, Text accountCreatingMessage, ToggleGroup accountTypeGroup, TextField accountCurrencyField) {
        BankManager manager = (BankManager) employee;
        String username = usernameField.getText();
        String accountType = ((RadioButton) accountTypeGroup.getSelectedToggle()).getTooltip().getText();
        String accountName = accountNameField.getText();
        String accountCurrency = accountCurrencyField.getText();
        if (!CurrencyConverter.isCurrency(accountCurrency)) {
            accountCreatingMessage.setText("Invalid Currency");
            accountCurrencyField.clear();
        } else {
            boolean success;
            try {
                manager.createAccount(username, accountType, accountName, accountCurrency);
                success = true;
            } catch (IOException exception) {
                accountCreatingMessage.setText(exception.getMessage());
                success = false;
            }
            if (success) {
                accountCreatingMessage.setText("Account Creation Successful!");
            }
            usernameField.clear();
            accountNameField.clear();
        }
    }
}
