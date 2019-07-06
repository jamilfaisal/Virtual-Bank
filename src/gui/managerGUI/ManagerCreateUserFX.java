package src.gui.managerGUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import src.*;

public class ManagerCreateUserFX extends Application {

    private Stage window;
    private final Employee employee;
    private final Scene main;

    public ManagerCreateUserFX(Employee employee, Scene main){
        this.employee = employee;
        this.main = main;
    }
    /**
     * The scene for creating users.
     */
    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        //creating label email
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

        Text userCreatingMessage = new Text("");

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

        //Arranging all the nodes in the grid
        gridPane.add(usernameText, 0, 0);
        gridPane.add(usernameField, 1, 0);
        gridPane.add(passwordText, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(passwordCriteriaButton, 2, 1);
        gridPane.add(passwordCriteriaText, 3, 1);
        gridPane.add(accountNameText, 0, 2);
        gridPane.add(accountNameField, 1, 2);
        gridPane.add(emailText, 0, 3);
        gridPane.add(emailField, 1, 3);
        gridPane.add(submitButton, 1, 4);
        gridPane.add(backButton, 0, 4);
        gridPane.add(userCreatingMessage, 1, 5);

        Scene createUserScene = new Scene(gridPane);
        window.setScene(createUserScene);

        backButton.setOnAction(e -> window.setScene(main));
        passwordCriteriaButton.setOnAction(e -> {
            passwordCriteriaText.setVisible(!passwordCriteriaText.isVisible());
            passwordCriteriaText.setManaged(!passwordCriteriaText.isManaged());
        });
        submitButton.setOnAction(event -> submitButtonHandler(usernameField, passwordField, accountNameField, emailField, userCreatingMessage));

    }
    private void submitButtonHandler(TextField usernameField, TextField passwordField, TextField accountNameField, TextField emailField, Text userCreatingMessage){
        BankManager manager = (BankManager) employee;
        boolean success;
        String message = "Please try again!";
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String defaultAccountName = accountNameField.getText();
            String defaultEmail = emailField.getText();
            success = manager.createUser(username, password, defaultAccountName, defaultEmail);
        } catch (Exception e) {
            message = e.getMessage();
            success = false;
        }
        if (success) {
            userCreatingMessage.setText("User Creation Successful!");
        } else {
            userCreatingMessage.setText(message);
            usernameField.clear();
            passwordField.clear();
            accountNameField.clear();
            emailField.clear();
        }
    }
}
