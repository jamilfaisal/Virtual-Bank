package src.gui.managerGUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import src.*;
import src.exceptions.TypeInvalidException;

import java.io.IOException;

public class ManagerCreateEmployeeFX extends Application {

    private Stage window;
    private final Employee employee;
    private final Scene main;

    public ManagerCreateEmployeeFX(Employee employee, Scene main){
        this.employee = employee;
        this.main = main;
    }
    /**
     * The scene for creating employees.
     */
    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        Text usernameText = new Text("New Username (alphanumeric): ");
        TextField usernameField = new TextField();

        Text passwordText = new Text("Password");
        TextField passwordField = new TextField();
        Text passwordCriteriaText = new Text("1. Minimum 8 characters \n" +
                "2. At least 1 uppercase character \n" +
                "3. At least 1 lowercase character \n" +
                "4. At least 1 of the following \n" +
                "   special characters: @#$%^&+= \n" +
                "5. At least 1 number from 0 to 9 \n" +
                "6. No whitespace characters");
        passwordCriteriaText.setFill(Color.rgb(150, 54, 72));
        Button passwordCriteriaButton = new Button("criteria:");

        Text accountNameText = new Text("Account Name (alphanumeric):");
        TextField accountNameField = new TextField();

        Text emailText = new Text("Email: ");
        TextField emailField = new TextField();

        Text employeeTypeText = new Text("Employee Type:");
        ToggleGroup employeeTypeGroup = new ToggleGroup();
        RadioButton tellerButton = new RadioButton("Teller");
        tellerButton.setToggleGroup(employeeTypeGroup);
        tellerButton.setSelected(true);
        RadioButton managerButton = new RadioButton("Manager");
        managerButton.setToggleGroup(employeeTypeGroup);

        employeeTypeGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            RadioButton rb = (RadioButton) employeeTypeGroup.getSelectedToggle();
            if (rb == managerButton) {
                accountNameField.setDisable(true);
                accountNameText.setDisable(true);
            } else if (rb == tellerButton) {
                accountNameField.setDisable(false);
                accountNameText.setDisable(false);
            }
        });

        Text employeeCreatingMessage = new Text();

        //Creating Buttons
        Button submitButton = new Button("Submit");
        Button backButton = new Button("Back");

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        gridPane.setMinSize(1024, 768);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);
//        gridPane.gridLinesVisibleProperty().setValue(true);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(employeeTypeText, 0, 0);
        gridPane.add(tellerButton, 1, 0);
        gridPane.add(managerButton, 2, 0);
        gridPane.add(usernameText, 0, 1);
        gridPane.add(usernameField, 1, 1);
        gridPane.add(passwordText, 0, 2);
        gridPane.add(passwordField, 1, 2);
        gridPane.add(passwordCriteriaButton, 2, 2);
        gridPane.add(passwordCriteriaText, 3, 2);
        gridPane.add(accountNameText, 0, 3);
        gridPane.add(accountNameField, 1, 3);
        gridPane.add(emailText, 0, 4);
        gridPane.add(emailField, 1, 4);
        gridPane.add(submitButton, 1, 5);
        gridPane.add(backButton, 0, 5);
        gridPane.add(employeeCreatingMessage, 1, 6);

        Scene createEmployeeScene = new Scene(gridPane);
        window.setScene(createEmployeeScene);

        backButton.setOnAction(e -> window.setScene(main));
        passwordCriteriaButton.setOnAction(e -> {
            passwordCriteriaText.setVisible(!passwordCriteriaText.isVisible());
            passwordCriteriaText.setManaged(!passwordCriteriaText.isManaged());
        });
        submitButton.setOnAction(e -> submitButtonHandler(usernameField,passwordField,accountNameField,emailField, employeeCreatingMessage, employeeTypeGroup));

    }
    private void submitButtonHandler(TextField usernameField, TextField passwordField, TextField accountNameField, TextField emailField, Text employeeCreatingMessage, ToggleGroup employeeTypeGroup) {
        BankManager manager = (BankManager) employee;
        String username = usernameField.getText();
        String password = passwordField.getText();
        String accountName = accountNameField.getText();
        String email = emailField.getText();
        String accountType = ((RadioButton) employeeTypeGroup.getSelectedToggle()).getText();
        try {
            boolean success = manager.createEmployee(username, password, accountName, email, accountType);
            if (success) {
                employeeCreatingMessage.setText("Employee Creation Successful!");
            } else {
                employeeCreatingMessage.setText("Please Try Again!");
            }
        } catch (IOException | TypeInvalidException ex) {
            employeeCreatingMessage.setText(ex.getMessage());
        }
        usernameField.clear();
        passwordField.clear();
        accountNameField.clear();
        emailField.clear();
    }
}
