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
import src.*;
import src.accounts.Account;

public class UserJointFX extends Application {
    private final User user;
    private final Scene userOptionsScene;
    private final UserHolder<User> userHolder;
    private final UserHolder<Employee> employeeHolder;
    public UserJointFX(Scene userOptionsScene, User user, UserHolder<User> userHolder, UserHolder<Employee> employeeHolder) {
        this.user = user;
        this.userOptionsScene = userOptionsScene;
        this.userHolder = userHolder;
        this.employeeHolder = employeeHolder;
    }
    /**
     * Sets the scene for requesting a joint user
     * @param primaryStage The application window
     */
    @Override
    public void start(Stage primaryStage) {

        // Creates the scene and displays it to the main window
        Scene requestJointAccountPaneSetup = new Scene(requestJointAccountPaneSetup(primaryStage), 1024, 768);
        primaryStage.setScene(requestJointAccountPaneSetup);
    }

    /**
     * The scene for requesting a joint account, calls EmployeeLoginFX
     * @param primaryStage The application window
     */
    private GridPane requestJointAccountPaneSetup(Stage primaryStage) {

        GridPane requestJointAccountPane = new GridPane();

        Label requestJointLabel = new Label("Request Joint Account: ");
        Label accountNameLabel = new Label("Account Name: ");

        ObservableList<String> accountsObservable = FXCollections.observableArrayList();
        for (Account account: user.getAccounts()) {
            accountsObservable.add(account.getAccountName());
        }

        ComboBox<String> accountsDropDown = new ComboBox<>(accountsObservable);

        Label jointUsernameLabel = new Label("Joint Username: ");
        TextField jointUsernameField = new TextField();

        Label userMessage = new Label("");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(this.userOptionsScene));

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            if (accountsDropDown.getValue() != null) {
                String accountName = accountsDropDown.getValue();
                String jointUsername = jointUsernameField.getText();
                Account account = user.identifyAccount(accountName);
                if (account == null) {
                    userMessage.setText("Account not found.");
                } else {
                    User user = userHolder.getUser(jointUsernameField.getText());
                    if (user == null) {
                        userMessage.setText("User not found.");
                    } else {
                        UserEmployeeLoginFX userEmployeeLoginFX = new UserEmployeeLoginFX(this.userOptionsScene, this.user, this.employeeHolder, false, jointUsername, account);
                        userEmployeeLoginFX.start(primaryStage);
                        /*GridPane employeePane = employeeLoginPaneSetup(primaryStage, false, jointUsername, account);
                        this.employeeLoginScene = new Scene(employeePane, 1024, 768);
                        primaryStage.setScene(this.employeeLoginScene);*/
                    }
                }
            } else {
                userMessage.setText("You must select an account");
            }
        });

        //Setting the padding
        requestJointAccountPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        requestJointAccountPane.setVgap(5);
        requestJointAccountPane.setHgap(5);

        //Setting the Grid alignment
        requestJointAccountPane.setAlignment(Pos.CENTER);

        requestJointAccountPane.add(requestJointLabel, 0, 0);
        requestJointAccountPane.add(accountNameLabel, 0, 1);
        requestJointAccountPane.add(accountsDropDown, 2, 1);
        requestJointAccountPane.add(jointUsernameLabel, 0, 2);
        requestJointAccountPane.add(jointUsernameField, 2, 2);
        requestJointAccountPane.add(userMessage, 1, 3);
        requestJointAccountPane.add(backButton, 0, 4);
        requestJointAccountPane.add(submitButton, 2, 4);

        return requestJointAccountPane;
    }


}
