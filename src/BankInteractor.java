package src;

import src.exceptions.PasswordInvalidException;
import src.utils.PasswordSecurity;

import java.io.Serializable;
import java.util.Optional;

public abstract class BankInteractor implements Serializable {

    /**
     * Username used to identify the user
     */
    private final String username;

    /**
     * The password's salt
     */
    private String salt;

    /**
     * the hashed version of the password
     */
    private String hash;

    /**
     * A randomly generated string used for password recovery
     */
    private String recoveryString;

    /**
     * the user's email
     */
    private final String email;

    /**
     * Last city logged in from
     */
    private String lastCity;

    BankInteractor(String username, String salt, String hash, String email) {
        this.username = username;
        this.salt = salt;
        this.hash = hash;
        this.email = email;
        this.recoveryString = "";
    }

    /**
     * @return boolean indicating if the entered password matches this user's password
     */
    public boolean checkPassword(String passToCheck) {
        return PasswordSecurity.verifyPassword(passToCheck, hash, salt);
    }

    /**
     * Sets the new password
     *
     * @param newPass The new password
     * @return If the password hashing runs into a failure
     */
    public boolean setPassword(String newPass) throws PasswordInvalidException {
        if (!newPass.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new PasswordInvalidException("Password contains invalid characters");
        }

        Optional<PasswordSecurity.SecurePair> securePairOptional = PasswordSecurity.securePassword(newPass);
        if (securePairOptional.isPresent()) {
            PasswordSecurity.SecurePair securePair = securePairOptional.get();
            this.salt = securePair.getSalt();
            this.hash = securePair.getHash();
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return This user's email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * @return This user's username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the password recovery string for this user to the given string
     * @param recoveryString The string to be used for password recovery
     */
    public void setRecoveryString(String recoveryString) {
        this.recoveryString = recoveryString;
    }

    /**
     * @return The password recovery string
     */
    public String getRecoveryString() {
        return recoveryString;
    }

    /**
     * @return Last logged in city.
     */
    public String getLastCity() {return this.lastCity;}

    /**
     * Sets last city
     *
     * @param city Default city
     */
    public void setCity(String city) {this.lastCity = city;}
}
