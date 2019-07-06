package src;

import src.geolocation.Geolocation;
import src.utils.EmailManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class UserHolder<T extends BankInteractor> implements Serializable {

    // map from username to user of all users
    private final Map<String, T> users;
    private static Date date;

    public UserHolder() {
        Calendar today = Calendar.getInstance();
        date = today.getTime();
        users = new HashMap<>();
    }

    /**
     * Increments the current day by one in the src.UserHolder's date.
     */
    public static void addDay() {
        Calendar today = Calendar.getInstance();
        today.setTime(date);
        today.add(Calendar.DAY_OF_MONTH, 1);
        date = today.getTime();
    }

    /**
     * Attempts to get the account associated with the given username and password
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the src.User that matches the given username and password if one exists
     */
    public T login(String username, String password) {
        T attempt = users.get(username);
        if ((attempt != null) && attempt.checkPassword(password)) {
            try {
                Geolocation locator = new Geolocation();
                if ( !attempt.getLastCity().equals(locator.getCity())) {
                    sendLocationEmail(attempt, locator.getCity(), locator.getCountry(), locator.getRegion());
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            return attempt;

        }
        return null;
    }

    private void sendLocationEmail(T user, String city, String country, String region) {
        String subject = "Unknown login from "+ city +", "+region+", "+country;
        String content = "Attention "+ user.getUsername()+", \n " +
                "an unknown login was detected from the following location: \n" +
                "City: " + city + "\n" +
                ", Region: " + region + "\n" +
                ", Country: " + country + "\n \n" +
                "If this is you, you can safely ignore this email. If not please contact us immediately for password reset. \n" +
                "Thank you for your time.";
        EmailManager.sendEmail(user.getEmail(), subject, content);
    }

    // Never got time to implement
    public boolean resetPasswordRequest(String username) {
        T attempt = users.get(username);
        if (attempt != null) {
            attempt.setRecoveryString(String.valueOf((int) (Math.random() * 10000000)));
            EmailManager.sendEmail(attempt.getEmail(), "Password reset request", "Please enter this string at" +
                    "your ATM to reset your password: " + attempt.getRecoveryString());
            return true;
        }
        return false;
    }

    //Never got time to implement.
    public boolean resetPassword(String username, String password, String recoveryString) {
        T attempt = users.get(username);
        if (attempt == null) {
            // how to contact GUI
            return false;
        } else if (!attempt.getRecoveryString().equals(recoveryString)) {
            return false;
        } else {
            try {
                attempt.setPassword(password);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * Returns the list of users stored in this account
     *
     * @return ArrayList of all users
     */
    public List<T> getUserList() {
        return new ArrayList<>(users.values());
    }

    /**
     * Gets the user associated with the given username
     *
     * @param username the username of the user
     * @return src.User with the given username
     */
    public T getUser(String username) {
        return users.get(username);
    }

    /**
     * Adds the user to the user-list with the given username as the key
     *
     * @param username the username of the user
     * @param user     the user to be added
     * @return boolean of whether or not the user was successfully added
     */
    public boolean createUser(String username, T user) {
        if (users.get(username) != null) {
            return false;
        }
        users.put(username, user);
        return true;
    }

    /**
     * Returns the current date stored in src.UserHolder
     *
     * @return Date containing today's date
     */
    public static Date date() {
        return date;
    }

    public static void setDate(Date newDate) {
        date = newDate;
    }
}
