package src.gui;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import src.*;
import src.utils.HttpUtility;

import java.io.IOException;

public class LoginMainFX extends Application {

    private final UserHolder<User> userHolder;
    private final UserHolder<Employee> employeeHolder;
    private final BankMachine machine;
    private Scene mainMenuScene;

    /**
     *
     * @param userHolder The userHolder from the file storing all users
     * @param employeeHolder The employeeHolder from the file storing all employees
     * @param machine The atm machine being used
     */
    LoginMainFX(UserHolder<User> userHolder, UserHolder<Employee> employeeHolder, BankMachine machine) {
        this.userHolder = userHolder;
        this.employeeHolder = employeeHolder;
        this.machine = machine;
    }

    /**
     * Displays the main menu
     * @param primaryStage The application window
     */
    @Override
    public void start(Stage primaryStage) {

        GridPane mainMenuPane = mainMenuPaneSetup(primaryStage);
        primaryStage.setTitle("Login");
        this.mainMenuScene = new Scene(mainMenuPane, 1024, 768);
        primaryStage.setScene(mainMenuScene);
    }

    /**
     * Sets up the gridPane for the main menu scene
     * @param primaryStage The application window
     * @return The finished grid pane
     */
    private GridPane mainMenuPaneSetup(Stage primaryStage) {
        Label userLoginLabel = new Label("User Login");
        Label userNameLoginLabel = new Label("Username: ");
        Label userPasswordLoginLabel = new Label("Password: ");
        Label userMessageLabel = new Label();
        Label RFIDLoginLabel = new Label();

        TextField userNameLoginField = new TextField();
        userNameLoginField.setPrefColumnCount(22);
        PasswordField userPasswordLoginField = new PasswordField();
        userPasswordLoginField.setPrefColumnCount(22);

        // Clears all textfields
        Button userClearButton = new Button("Clear");
        userClearButton.setOnAction(e -> {
            userNameLoginField.clear();
            userPasswordLoginField.clear();
            userMessageLabel.setText("");
        });

        Button userLoginButton = new Button("Login");
        userLoginButton.setOnAction(e -> userLogin(primaryStage, userNameLoginField, userPasswordLoginField, userMessageLabel));

        // Initiates RFID login
        Button RFIDLoginButton = new Button("Login with Card");
        RFIDLoginButton.setOnAction(e -> RFIDLogin(primaryStage, RFIDLoginLabel));

        Label employeeLoginLabel = new Label("Employee Login: ");
        Label employeeNameLoginLabel = new Label("Username: ");
        Label employeePasswordLoginLabel = new Label("Password: ");
        Label employeeMessageLabel = new Label();

        TextField employeeNameLoginField = new TextField();
        employeeNameLoginField.setPrefColumnCount(22);
        PasswordField employeePasswordLoginField = new PasswordField();
        employeePasswordLoginField.setPrefColumnCount(22);

        Button employeeClearButton = new Button("Clear");
        employeeClearButton.setOnAction(e -> {
            employeeNameLoginField.clear();
            employeePasswordLoginField.clear();
            employeeMessageLabel.setText("");
        });

        Button employeeLoginButton = new Button("Login");
        employeeLoginButton.setOnAction(e -> employeeLoginHandler(employeeNameLoginField, employeePasswordLoginField, employeeMessageLabel, primaryStage));

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> Platform.exit());

        GridPane mainMenuPane = new GridPane();
        mainMenuPane.setPrefSize(1024, 768);
        mainMenuPane.setPadding(new Insets(10, 10, 10, 10));
        mainMenuPane.setAlignment(Pos.CENTER);
        mainMenuPane.setHgap(25);
        mainMenuPane.setVgap(50);
        mainMenuPane.add(userLoginLabel, 0, 0);
        mainMenuPane.add(userNameLoginLabel, 0, 1);
        mainMenuPane.add(userNameLoginField, 1, 1);
        mainMenuPane.add(userPasswordLoginLabel, 0, 2);
        mainMenuPane.add(userPasswordLoginField, 1, 2);
        mainMenuPane.add(userClearButton, 0, 3);
        mainMenuPane.add(userLoginButton, 1, 3);
        mainMenuPane.add(RFIDLoginButton, 0, 4);
        mainMenuPane.add(RFIDLoginLabel, 1, 4);
        mainMenuPane.add(userMessageLabel, 1, 5);
        mainMenuPane.add(employeeLoginLabel, 4, 0);
        mainMenuPane.add(employeeNameLoginLabel, 4, 1);
        mainMenuPane.add(employeeNameLoginField, 5, 1);
        mainMenuPane.add(employeePasswordLoginLabel, 4, 2);
        mainMenuPane.add(employeePasswordLoginField, 5, 2);
        mainMenuPane.add(employeeClearButton, 4, 3);
        mainMenuPane.add(employeeLoginButton, 5, 3);
        mainMenuPane.add(employeeMessageLabel, 5, 4);
        mainMenuPane.add(exitButton, 2, 5);

        return mainMenuPane;
    }

    private void employeeLoginHandler(TextField employeeNameLoginField, PasswordField employeePasswordLoginField, Label employeeMessageLabel, Stage primaryStage) {
        {
            String username = employeeNameLoginField.getText();
            String password = employeePasswordLoginField.getText();
            if ((username == null || username.isEmpty()) && (password == null || password.isEmpty())) {
                employeeMessageLabel.setText("Please enter your username and password!");
            } else if (username == null || username.isEmpty()) {
                employeeMessageLabel.setText("Please enter your username!");
            } else if (password == null || password.isEmpty()) {
                employeeMessageLabel.setText("Please enter your password!");
            } else {
                Employee employee = employeeHolder.login(username, password);
                if (employee == null) {
                    employeeMessageLabel.setText("Username or Password incorrect! Please try again.");
                } else {
                    employeeMessageLabel.setText("Login Successful!");
                    employeeNameLoginField.clear();
                    employeePasswordLoginField.clear();
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(e2 -> {
                        employeeMessageLabel.setText("");
                        ManagerMainFX managerMainFX = new ManagerMainFX(mainMenuScene, machine, employee, userHolder);
                        managerMainFX.start(primaryStage);
                    });
                    pause.play();
                }
            }
        }
    }

    /**
     * Handles user Logging in
     * @param primaryStage The application window
     * @param userNameLoginField The textfield containing the username
     * @param userPasswordLoginField The textfield containing the password
     * @param userMessageLabel The label displaying messages to the user
     */
    private void userLogin(Stage primaryStage, TextField userNameLoginField, PasswordField userPasswordLoginField, Label userMessageLabel) {
        String username = userNameLoginField.getText();
        String password = userPasswordLoginField.getText();
        // Checks for empty textfields
        if ((username == null || username.isEmpty()) && (password == null || password.isEmpty())){
            userMessageLabel.setText("Please enter your username and password!");
        }
        else if (username == null || username.isEmpty()) {
            userMessageLabel.setText("Please enter your username!");
        }
        else if (password == null || password.isEmpty()) {
            userMessageLabel.setText("Please enter your password!");
        }
        else {
            User user = userHolder.login(username, password);
            if (user == null) {
                userMessageLabel.setText("Username or Password incorrect! Please try again.");
                userNameLoginField.clear();
                userPasswordLoginField.clear();
            }
            else {
                userMessageLabel.setText("Login Successful!");
                // Adds a pause to imitate login
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(e2 -> {
                    // Switches to the next scene
                    userMessageLabel.setText("");
                    UserMainFX userMainFX = new UserMainFX(mainMenuScene, user, userHolder, employeeHolder, machine);
                    userMainFX.start(primaryStage);
                });
                pause.play();
            }
        }
    }

    /**
     * Handles logging in with RFID
     * @param primaryStage The application window
     * @param RFIDLoginLabel The Label for logging in with RFID
     */
    private void RFIDLogin(Stage primaryStage, Label RFIDLoginLabel) {
        RFIDLoginLabel.setText("Please hold the card to the reader.");
        String requestURL = "http://csc207group0347.localtunnel.me/read";
        try {
            HttpUtility.sendGetRequest(requestURL);
            String[] response = HttpUtility.readMultipleLinesRespone();
            String ID = response[0].trim();
            String username = response[1].trim();
            User user = userHolder.getUser(username);
            if (user == null) {
                RFIDLoginLabel.setText("User not found.");
            } else {
                if (user.checkRFID(ID)){
                    RFIDLoginLabel.setText("Login Successful!");
                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(e2 -> {
                        RFIDLoginLabel.setText("");
                        UserMainFX userMainFX = new UserMainFX(mainMenuScene, user, userHolder, employeeHolder, machine);
                        userMainFX.start(primaryStage);
                    });
                    pause.play();
                } else {
                    RFIDLoginLabel.setText("Invalid card for user " + username);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpUtility.disconnect();
    }
}
