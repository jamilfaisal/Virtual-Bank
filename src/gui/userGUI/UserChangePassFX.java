package src.gui.userGUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import src.User;
import src.exceptions.PasswordInvalidException;


public class UserChangePassFX extends Application {
    private final User user;
    private final Scene userOptionsScene;

    /**
     *  Assigns the logged in user and previous scene
     * @param userOptionsScene The user options scene
     * @param user The user logged in
     */
    public UserChangePassFX(Scene userOptionsScene, User user) {
        this.user = user;
        this.userOptionsScene = userOptionsScene;
    }

    /**
     * Sets the scene for changing password
     * @param primaryStage The application window
     */
    @Override
    public void start(Stage primaryStage) {

        // Creates the scene and displays it to the main window
        Scene userChangePasswordPaneSetup = new Scene(userChangePasswordPaneSetup(primaryStage), 1024, 768);
        primaryStage.setScene(userChangePasswordPaneSetup);
    }

    /**
     * The scene for changing password
     * @param primaryStage The application window
     */
    private GridPane userChangePasswordPaneSetup(Stage primaryStage) {
        GridPane userChangePasswordPane = new GridPane();

        Label userChangePasswordLabel = new Label("Change Password: ");
        Label newPasswordLabel = new Label("New Password: ");
        Label newPasswordCriteria = new Label("1. Minimum 8 characters\n" +
                "2. At least 1 uppercase character\n" +
                "3. At least 1 lowercase character\n" +
                "4. At least 1 of the following special characters: @#$%^&+=\n" +
                "5. At least 1 number from 0 to 9\n" +
                "6. No whitespace characters");
        TextField newPasswordField = new TextField();
        Label userMessage = new Label("");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(this.userOptionsScene));

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String newPassword = newPasswordField.getText();
            try {
                boolean success = this.user.setPassword(newPassword);
                if (success) {
                    userMessage.setText("Password changed successfully!");
                }
                else {
                    userMessage.setText("Password contains invalid characters.");
                }
            } catch (PasswordInvalidException e1) {
                userMessage.setText(e1.getMessage());
            }
        });

        //Setting the padding
        userChangePasswordPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        userChangePasswordPane.setVgap(5);
        userChangePasswordPane.setHgap(5);

        //Setting the Grid alignment
        userChangePasswordPane.setAlignment(Pos.CENTER);

        userChangePasswordPane.add(userChangePasswordLabel, 0, 0);
        userChangePasswordPane.add(newPasswordLabel, 0, 1);
        userChangePasswordPane.add(newPasswordField, 1, 1);
        userChangePasswordPane.add(newPasswordCriteria, 1, 2);
        userChangePasswordPane.add(userMessage, 1, 3);
        userChangePasswordPane.add(backButton, 0, 4);
        userChangePasswordPane.add(submitButton, 1, 4);

        return userChangePasswordPane;
    }
}
