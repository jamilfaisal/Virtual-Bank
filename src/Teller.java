package src;

import java.io.Serializable;

/**
 * src.Teller
 * - An employee that extends the abstract Employee class
 * - Less access than manager.
 */
public class Teller extends Employee implements Serializable {

    private final User associatedUser;

    /**
     *
     * @param username The username of this bank teller
     * @param salt The password's salt
     * @param hash The hashed password
     * @param userHolder The src.UserHolder instance holding the users of the atm
     * @param associatedUser the user associated with this teller
     * @param email the email address of this teller
     */
    public Teller(String username, String salt, String hash, UserHolder<User> userHolder, User associatedUser, String email) {
        super(username, "Teller", salt, hash, userHolder, email);
        this.associatedUser = associatedUser;
    }
}
