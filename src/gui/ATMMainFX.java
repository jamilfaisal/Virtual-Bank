package src.gui;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import src.*;
import src.accounts.Account;
import src.accounts.SavingsAccount;
import src.geolocation.Geolocation;
import src.utils.PasswordSecurity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ATMMainFX extends Application {

    private UserHolder<User> userHolder;
    private UserHolder<Employee> employeeHolder;
    private BankMachine machine;

    @Override
    public void start(Stage primaryStage) {

        File bankMachineFile = new File("./Data/BankMachine.ser");
        File userHolderFile = new File("./Data/UserHolder.ser");
        File employeeHolderFile = new File("./Data/EmployeeHolder.ser");
        File dateFile = new File("./Data/Date.ser");

        if (bankMachineFile.exists() && userHolderFile.exists() && employeeHolderFile.exists() && dateFile.exists()) {
            try {
                FileInputStream bankMachineFileStream = new FileInputStream(bankMachineFile);
                FileInputStream userHolderFileStream = new FileInputStream(userHolderFile);
                FileInputStream employeeHolderFileStream = new FileInputStream(employeeHolderFile);
                FileInputStream dateFileStream = new FileInputStream(dateFile);

                ObjectInputStream bankMachineIn = new ObjectInputStream(bankMachineFileStream);
                ObjectInputStream userHolderIn = new ObjectInputStream(userHolderFileStream);
                ObjectInputStream employeeHolderIn = new ObjectInputStream(employeeHolderFileStream);
                ObjectInputStream dateIn = new ObjectInputStream(dateFileStream);

                machine = (BankMachine) bankMachineIn.readObject();
                userHolder = (UserHolder<User>) userHolderIn.readObject();
                employeeHolder = (UserHolder<Employee>) employeeHolderIn.readObject();
                Date date = (Date) dateIn.readObject();

                bankMachineIn.close();
                userHolderIn.close();
                employeeHolderIn.close();
                dateIn.close();
                bankMachineFileStream.close();
                userHolderFileStream.close();
                employeeHolderFileStream.close();
                dateFileStream.close();

                UserHolder.setDate(date);
                UserHolder.addDay();
                // This for loop is used to circumvent deserialization issues to make sure ever instance of userHolder
                // and employeeHolder is the same between all tellers and bank managers.
                for (Employee employee: employeeHolder.getUserList()) {
                    employee.setUserHolder(userHolder);
                    if (employee instanceof BankManager) {
                        ((BankManager) employee).setEmployeeHolder(employeeHolder);
                    }
                }
            } catch (IOException i) {
                i.printStackTrace();
                return;
            } catch (ClassNotFoundException c) {
                System.out.println("Class not found.");
                c.printStackTrace();
                return;
            }
        } else {
            machine = new BankMachine();
            userHolder = new UserHolder<>();
            employeeHolder = new UserHolder<>();
            Optional<PasswordSecurity.SecurePair> securePairOptional = PasswordSecurity.securePassword("admin");
            if (!securePairOptional.isPresent()) {
                throw new RuntimeException("Hashing algorithm failed during hashing password of initial manager.");
            }
            PasswordSecurity.SecurePair securePair = securePairOptional.get();
            BankManager initialManager = new BankManager("ATMManager", securePair.getSalt(), securePair.getHash(), userHolder, employeeHolder, "default@email.com");
            try {
                Geolocation locator = new Geolocation();
                initialManager.setCity(locator.getCity());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            employeeHolder.createUser("ATMManager", initialManager);
        }
        updateInterest(userHolder.getUserList());

        BorderPane welcomeScreenPane = welcomeScreenPaneSetup();
        Scene welcomeScreenScene = new Scene(welcomeScreenPane, 1024, 768);

        primaryStage.setTitle("ATM");
        primaryStage.setScene(welcomeScreenScene);
        primaryStage.show();
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            LoginMainFX loginMainFX = new LoginMainFX(userHolder, employeeHolder, machine);
            loginMainFX.start(primaryStage);
        });
        pause.play();
        primaryStage.setOnCloseRequest(e -> {
            try {
                onExit();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

    }
    private static void updateInterest(List<User> users) {
        // checks if it is the first of the month to update the interest of savings accounts
        Calendar today = Calendar.getInstance();
        today.setTime(UserHolder.date());
        if (today.get(Calendar.DAY_OF_MONTH) == 1) {
            // this list stores every account that is iterated over so if there is a joint savings account, it does not
            // grow in interest twice.
            List<Account> duplicates = new ArrayList<>();
            for (User user: users) {
                for (Account account : user.getAccounts()) {
                    if (account instanceof SavingsAccount && !duplicates.contains(account)) {
                        ((SavingsAccount) account).growInterest();
                        duplicates.add(account);
                    }
                }
            }
        }
    }

    private BorderPane welcomeScreenPaneSetup() {
        Text welcomeScreenTitle = new Text("Welcome!");

        BorderPane welcomeScreenPane = new BorderPane();
        welcomeScreenPane.setCenter(welcomeScreenTitle);
        return welcomeScreenPane;
    }

    @Override
    public void stop() throws IOException {
        onExit();
    }

    private void onExit() throws IOException {
        Path path = Paths.get("./Data");
        Files.createDirectories(path);

        FileOutputStream bankMachineFileStream = new FileOutputStream("./Data/BankMachine.ser");
        ObjectOutputStream bankMachineOut = new ObjectOutputStream(bankMachineFileStream);
        bankMachineOut.writeObject(machine);
        bankMachineOut.close();
        bankMachineFileStream.close();

        FileOutputStream userHolderFileStream = new FileOutputStream("./Data/UserHolder.ser");
        ObjectOutputStream userHolderOut = new ObjectOutputStream(userHolderFileStream);
        userHolderOut.writeObject(userHolder);
        userHolderOut.close();
        userHolderFileStream.close();

        FileOutputStream employeeHolderFileStream = new FileOutputStream("./Data/EmployeeHolder.ser");
        ObjectOutputStream employeeHolderOut = new ObjectOutputStream(employeeHolderFileStream);
        employeeHolderOut.writeObject(employeeHolder);
        employeeHolderOut.close();
        employeeHolderFileStream.close();

        FileOutputStream dateFileStream = new FileOutputStream("./Data/Date.ser");
        ObjectOutputStream dateOut = new ObjectOutputStream(dateFileStream);
        dateOut.writeObject(UserHolder.date());
        dateOut.close();
        dateFileStream.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}