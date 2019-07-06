package src.gui.userGUI;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import src.accounts.Account;
import src.BankMachine;
import src.Bills;
import src.User;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class UserDepositFX extends Application {
    private final User user;
    private final Scene userOptionsScene;
    private final BankMachine machine;
    /**
     *  Assigns the logged in user and previous scene
     * @param userOptionsScene The user options scene
     * @param user The user logged in
     */
    public UserDepositFX(Scene userOptionsScene, User user, BankMachine machine) {
        this.user = user;
        this.userOptionsScene = userOptionsScene;
        this.machine = machine;
    }

    /**
     * Sets the scene to deposit
     * @param primaryStage The application window
     */
    @Override
    public void start(Stage primaryStage) {

        // Creates the scene and displays it to the main window
        Scene depositPaneSetup = new Scene(depositPaneSetup(primaryStage), 1024, 768);
        primaryStage.setScene(depositPaneSetup);
    }
    /**
     * The scene for deposits
     * @param primaryStage The application window
     */
    private GridPane depositPaneSetup(Stage primaryStage) {
        GridPane depositPane = new GridPane();

        Label typeLabel = new Label("Select deposit type:");
        ToggleGroup depositTypeGroup = new ToggleGroup();
        RadioButton typeCash = new RadioButton("Cash Deposit");
        RadioButton typeCheque = new RadioButton("Cheque Deposit");
        typeCash.setToggleGroup(depositTypeGroup);
        typeCheque.setToggleGroup(depositTypeGroup);

        Label accountNameLabel = new Label("Please enter the name of the account: ");

        ObservableList<String> accountsObservable = FXCollections.observableArrayList();
        for (Account account: user.getAccounts()) {
            accountsObservable.add(account.getAccountName());
        }

        ComboBox<String> accountsDropDown = new ComboBox<>(accountsObservable);

        Label chequeAmountLabel = new Label("Please enter the amount you want to deposit (cheque):");

        TextField chequeAmountField = new TextField();

        Label depositPromptLabel = new Label("Please enter the number of bills (MAX 1000 bills for each type):");

        Label fiveDollarLabel = new Label("Number of $5 bills:");
        TextField fiveDollarField = new TextField();
        fiveDollarField.setText("0");

        Label tenDollarLabel = new Label("Number of $10 bills:");
        TextField tenDollarField = new TextField();
        tenDollarField.setText("0");

        Label twentyDollarLabel = new Label("Number of $20 bills:");
        TextField twentyDollarField = new TextField();
        twentyDollarField.setText("0");

        Label fiftyDollarLabel = new Label("Number of $50 bills:");
        TextField fiftyDollarField = new TextField();
        fiftyDollarField.setText("0");

        Label userMessage = new Label("");

        depositTypeGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            RadioButton rb = (RadioButton) depositTypeGroup.getSelectedToggle();
            if (rb == typeCash) {
                chequeAmountField.setDisable(true);
                fiveDollarField.setDisable(false);
                tenDollarField.setDisable(false);
                twentyDollarField.setDisable(false);
                fiftyDollarField.setDisable(false);
            } else {
                chequeAmountField.setDisable(false);
                fiveDollarField.setDisable(true);
                tenDollarField.setDisable(true);
                twentyDollarField.setDisable(true);
                fiftyDollarField.setDisable(true);
            }
        });

        depositTypeGroup.selectToggle(typeCash);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String fiveAmount = fiveDollarField.getText();
            String tenAmount = tenDollarField.getText();
            String twentyAmount = twentyDollarField.getText();
            String fiftyAmount = fiftyDollarField.getText();

            Map<BigDecimal, Bills> depositMap = new HashMap<>();

            Bills five = new Bills(new BigDecimal(5.00), 0);
            Bills ten = new Bills(new BigDecimal(10.00), 0);
            Bills twenty = new Bills(new BigDecimal(20.00), 0);
            Bills fifty = new Bills(new BigDecimal(50.00), 0);

            boolean validAmount;

            int fives;
            int tens;
            int twenties;
            int fifties;

            BigDecimal amount = new BigDecimal(0);

            if (depositTypeGroup.getSelectedToggle() == typeCash){
                try {
                    fives =  Integer.parseInt(fiveAmount);
                    tens =  Integer.parseInt(tenAmount);
                    twenties =  Integer.parseInt(twentyAmount);
                    fifties =  Integer.parseInt(fiftyAmount);

                    int totalBills = fives + tens + twenties + fifties;

                    amount = new BigDecimal(fives * 5 + tens * 10 + twenties * 20 + fifties * 50);

                    if(fives >=0 && tens >= 0 && twenties >= 0 && fifties >= 0 && totalBills > 0 && fives <= 1000
                    && tens <= 1000 && twenties <= 1000 && fifties <= 1000) {
                        validAmount = true;
                        five.addQuantity(fives);
                        ten.addQuantity(tens);
                        twenty.addQuantity(twenties);
                        fifty.addQuantity(fifties);

                        depositMap.put(new BigDecimal(5.00), five);
                        depositMap.put(new BigDecimal(10.00), ten);
                        depositMap.put(new BigDecimal(20.00), twenty);
                        depositMap.put(new BigDecimal(50.00), fifty);
                    } else {
                        validAmount = false;
                    }

                } catch(NumberFormatException except){
                    validAmount = false;
                }
            } else {
                try {
                    amount = new BigDecimal(chequeAmountField.getText());
                    validAmount = amount.compareTo(BigDecimal.ZERO) > 0;
                } catch (NumberFormatException except) {
                    validAmount = false;
                }
            }

            if (accountsDropDown.getValue() != null) {
                String accountName = accountsDropDown.getValue();
                Account account = user.identifyAccount(accountName);

                if (account == null) {
                    userMessage.setText("Account not found.");
                } else if (!validAmount) {
                    userMessage.setText("Please enter a valid amount to deposit.");
                } else {
                    if (depositTypeGroup.getSelectedToggle() == typeCash) {
                        machine.deposit(depositMap);
                        account.deposit(amount);
                    } else {
                        account.deposit(amount);
                    }
                    userMessage.setText("Successfully deposited $" + amount + " to your account!");
                }
            } else {
                userMessage.setText("You must select an account");
            }

        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(this.userOptionsScene));

        //Setting the padding
        depositPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        depositPane.setVgap(5);
        depositPane.setHgap(5);

        //Setting the Grid alignment
        depositPane.setAlignment(Pos.CENTER);

        depositPane.add(typeLabel, 0, 0);
        depositPane.add(typeCash, 1, 0);
        depositPane.add(typeCheque, 2, 0);

        depositPane.add(accountNameLabel, 0, 1);
        depositPane.add(accountsDropDown, 1, 1);

        depositPane.add(chequeAmountLabel, 0, 2);
        depositPane.add(chequeAmountField, 1, 2);

        depositPane.add(depositPromptLabel, 0, 3);

        depositPane.add(fiveDollarLabel, 0, 4);
        depositPane.add(tenDollarLabel, 1, 4);
        depositPane.add(twentyDollarLabel, 2, 4);
        depositPane.add(fiftyDollarLabel, 3, 4);

        depositPane.add(fiveDollarField, 0, 5);
        depositPane.add(tenDollarField, 1, 5);
        depositPane.add(twentyDollarField, 2, 5);
        depositPane.add(fiftyDollarField, 3, 5);

        depositPane.add(submitButton, 0, 6);

        depositPane.add(userMessage, 0, 7);
        depositPane.add(backButton, 0, 8);

        return depositPane;
    }
}
