package src.gui.userGUI;

import javafx.animation.PauseTransition;
import javafx.application.Application;
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
import src.accounts.Account;

public class UserEmployeeLoginFX extends Application {

    private final User user;
    private final Scene userOptionsScene;
    private final UserHolder<Employee> employeeHolder;
    private Employee employee;
    private final boolean accountCreation;
    private final String jointUsername;
    private final Account account;

    public UserEmployeeLoginFX(Scene userOptionsScene, User user, UserHolder<Employee> employeeHolder, boolean accountCreation, String jointUsername, Account account) {
        this.user = user;
        this.userOptionsScene = userOptionsScene;
        this.employeeHolder = employeeHolder;
        this.accountCreation = accountCreation;
        this.jointUsername = jointUsername;
        this.account = account;
    }
    /**
     * Displays the Information options scene
     * @param primaryStage The application window
     */
    @Override
    public void start(Stage primaryStage) {

        // Creates the scene and displays it to the main window
        Scene userEmployeeLoginFX = new Scene(employeeLoginPaneSetup(primaryStage, this.accountCreation, this.jointUsername, this.account), 1024, 768);
        primaryStage.setScene(userEmployeeLoginFX);
    }

    /**
     * The scene for employee login, can either be approving a joint account or leading to account creation scene
     * @param primaryStage The application window
     */
    private GridPane employeeLoginPaneSetup(Stage primaryStage, boolean accountCreation, String jointUsername, Account account) {

        GridPane employeeLoginPane = new GridPane();

        Label employeeLoginLabel = new Label("Employee Login: ");
        Label employeeLoginNameLabel = new Label("Username: ");
        TextField employeeLoginNameField = new TextField();
        Label employeeLoginPasswordLabel = new Label("Password: ");
        PasswordField employeeLoginPasswordField = new PasswordField();

        Label employeeMessage = new Label("");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(this.userOptionsScene));

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String username = employeeLoginNameField.getText();
            String password = employeeLoginPasswordField.getText();
            if ((username == null || username.isEmpty()) && (password == null || password.isEmpty())) {
                employeeMessage.setText("Please enter your username and password!");
            } else if (username == null || username.isEmpty()) {
                employeeMessage.setText("Please enter your username!");
            } else if (password == null || password.isEmpty()) {
                employeeMessage.setText("Please enter your password!");
            } else {
                this.employee = employeeHolder.login(username, password);
                if (employee == null) {
                    employeeMessage.setText("Username or Password incorrect! Please try again.");
                } else if (accountCreation) {
                    employeeLoginLabel.setText("Manager Login: ");
                    if (employee instanceof BankManager){
                        employeeMessage.setText("Login Successful!");
                        employeeLoginNameField.clear();
                        employeeLoginPasswordField.clear();
                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
                        pause.setOnFinished(e2 -> {
                            employeeMessage.setText("");
                            UserCreateAccountFX usercreateAccountFX = new UserCreateAccountFX(this.userOptionsScene, this.user, this.employee);
                            usercreateAccountFX.start(primaryStage);
                            //primaryStage.setScene(new Scene(userCreateAccountPaneSetup(primaryStage), 1024, 768));
                        });
                        pause.play();
                    } else {
                        employeeMessage.setText("Must be a bank managerGUI.");
                        employeeLoginNameField.clear();
                        employeeLoginPasswordField.clear();
                        PauseTransition pause = new PauseTransition(Duration.seconds(2));
                        pause.setOnFinished(e2 -> {
                            employeeMessage.setText("");
                            primaryStage.setScene(this.userOptionsScene);
                        });
                        pause.play();
                    }
                }
                else {
                    employeeMessage.setText("Login Successful!");
                    employeeLoginNameField.clear();
                    employeeLoginPasswordField.clear();
                    PauseTransition pause = new PauseTransition(Duration.seconds(2));
                    pause.setOnFinished(e2 -> {
                        boolean success = user.requestJoint(account, employee, jointUsername);
                        if (success) {
                            employeeMessage.setText("Account is now joint with " + jointUsername);
                        }
                        else {
                            employeeMessage.setText("Process Failed!");
                        }
                    });
                    pause.play();
                }
            }
        });
        //Setting the padding
        employeeLoginPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        employeeLoginPane.setVgap(5);
        employeeLoginPane.setHgap(5);

        //Setting the Grid alignment
        employeeLoginPane.setAlignment(Pos.CENTER);

        employeeLoginPane.add(employeeLoginLabel, 0, 0);
        employeeLoginPane.add(employeeLoginNameLabel, 0, 1);
        employeeLoginPane.add(employeeLoginNameField, 2, 1);
        employeeLoginPane.add(employeeLoginPasswordLabel, 0, 2);
        employeeLoginPane.add(employeeLoginPasswordField, 2, 2);
        employeeLoginPane.add(employeeMessage, 1, 3);
        employeeLoginPane.add(backButton, 0, 4);
        employeeLoginPane.add(loginButton, 1, 4);

        return employeeLoginPane;
    }
}
