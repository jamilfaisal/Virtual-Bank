package src;

import src.accounts.*;
import src.exceptions.NameInvalidException;
import src.exceptions.PasswordInvalidException;
import src.exceptions.TypeInvalidException;
import src.geolocation.Geolocation;
import src.utils.EmailManager;
import src.utils.PasswordSecurity;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BankManager extends Employee implements Serializable {

    private UserHolder<Employee> employeeHolder;

    /**
     * Creates a new src.BankManager
     *
     * @param username   username of bank manager
     * @param salt       password's salt
     * @param hash       hashed password
     * @param userHolder the src.UserHolder instance holding the users of the atm
     * @param email      the email address of the manager
     */
    public BankManager(String username, String salt, String hash, UserHolder<User> userHolder, UserHolder<Employee> employeeHolder, String email) {
        super(username, "Bank Manager", salt, hash, userHolder, email);
        this.employeeHolder = employeeHolder;
    }

    /**
     * Method to fill the atm with the provided bills
     *
     * @param billStorage the atm's bills map where the bills are stored
     * @param billsToAdd  the map of the bills to add to the atm
     */
    public void fillATM(Map<BigDecimal, Bills> billStorage, Map<BigDecimal, Bills> billsToAdd) {
        for (Map.Entry<BigDecimal, Bills> entry : billStorage.entrySet()) {
            if (billsToAdd.containsKey(entry.getKey())) {
                entry.getValue().addQuantity(billsToAdd.get(entry.getKey()).getQuantity());
            }
        }
    }


    /**
     * Create a new user and add them to the src.UserHolder database.
     *
     * @param username           username of new user
     * @param password           password of new user
     * @param defaultAccountName account name of new user's main account
     * @param defaultEmail       default email address for the account
     * @return boolean to indicate whether user creation succeeded or not
     * @throws IOException when username, password, main account name, or email address don't fit
     *                     their respective formats
     */
    public boolean createUser(String username, String password, String defaultAccountName, String defaultEmail) throws IOException {
        User newUser;
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
            throw new PasswordInvalidException("Password contains invalid characters");
        }
        if (!username.matches("[a-zA-Z0-9]+")) {
            throw new NameInvalidException("Username contains non-alphanumeric characters");
        }
        if (!defaultAccountName.matches("[a-zA-Z0-9]+")) {
            throw new NameInvalidException("Main account name contains non-alphanumeric characters");
        }
        if (!defaultEmail.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")) {
            throw new NameInvalidException("Default email contains non-alphanumeric characters");
        }
        try {
            Optional<PasswordSecurity.SecurePair> securePairOptional = PasswordSecurity.securePassword(password);
            if (securePairOptional.isPresent()) {
                PasswordSecurity.SecurePair securePair = securePairOptional.get();
                newUser = new User(username, securePair.getSalt(), securePair.getHash(), defaultEmail);
            } else {
                return false;
            }
            Geolocation locator = new Geolocation();
            newUser.setCity(locator.getCity());
        } catch (Exception e) {
            return false;
        }
        boolean success = userHolder.createUser(username, newUser);
        if (success) {
            createAccount(username, "Chequing", defaultAccountName, "CAD");
        }
        return success;
    }

    /**
     * Create a new teller and add them to the src.UserHolder database.
     *
     * @param username           username of new teller
     * @param password           password of new teller
     * @param defaultAccountName account name of new teller's main account
     * @param defaultEmail       email of the new teller
     * @return The teller creation is successful
     * @throws IOException If account creation fails
     */

    public boolean createEmployee(String username, String password, String defaultAccountName, String defaultEmail, String accountType) throws IOException, TypeInvalidException {
        Employee newEmployee;
        Geolocation locator = new Geolocation();
        if (accountType.equalsIgnoreCase("manager")){
            if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
                throw new PasswordInvalidException("Password contains invalid characters");
            }
            if (!username.matches("[a-zA-Z0-9]+")) {
                throw new NameInvalidException("Username contains non-alphanumeric characters");
            }
            if (!defaultEmail.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")) {
                throw new NameInvalidException("Default email contains non-alphanumeric characters");
            }
            try {
                Optional<PasswordSecurity.SecurePair> securePairOptional = PasswordSecurity.securePassword(password);
                if (securePairOptional.isPresent()) {
                    PasswordSecurity.SecurePair securePair = securePairOptional.get();
                    newEmployee = new BankManager(username, securePair.getSalt(), securePair.getHash(), this.userHolder, this.employeeHolder, defaultEmail);
                } else {
                    return false;
                }
                newEmployee.setCity(locator.getCity());
                return employeeHolder.createUser(username, newEmployee);
            } catch (Exception e) {
                return false;
            }
        } else if (accountType.equalsIgnoreCase("teller")){
            if (!this.createUser(username, password, defaultAccountName, defaultEmail)) {
                return false;
            }
            User newUser = userHolder.getUser(username);
            if (PasswordSecurity.securePassword(password).isPresent()) {
                PasswordSecurity.SecurePair securePair = PasswordSecurity.securePassword(password).get();
                newEmployee = new Teller(username, securePair.getSalt(), securePair.getHash(), this.userHolder, newUser, defaultEmail);
                newEmployee.setCity(locator.getCity());
                return this.employeeHolder.createUser(username, newEmployee);
            }
            return false;
        } else {
            throw new src.exceptions.TypeInvalidException("Not valid type of employee");
        }
    }

    /**
     * Create a new account for the specified user
     *
     * @param username    username of user to create the new account for
     * @param type        the type of the new account (Chequing, Savings, CreditCard, LineOfCredit)
     * @param accountName the name of the new account
     * @throws IOException in the case of an invalid account name
     */
    public void createAccount(String username, String type, String accountName, String currency) throws IOException {
        Account account;
        User user = userHolder.getUser(username);
        if (user == null) {
            throw new NameInvalidException("User with given username dose not exist");
        }

        if (user.identifyAccount(accountName) != null) {
            throw new NameInvalidException("An account with the same name already exists");
        }

        if (!accountName.matches("[a-zA-Z0-9]+")) {
            throw new NameInvalidException("Account name contains non-alphanumeric characters");
        }
        if (type.equalsIgnoreCase("Chequing")) {
            if (user.accounts.size() == 0) {
                account = new ChequingAccount(user, accountName, currency, true);
            } else {
                account = new ChequingAccount(user, accountName, currency, false);
            }
            user.accounts.add(account);
        } else if (type.equalsIgnoreCase("Savings")) {
            account = new SavingsAccount(user, accountName, currency);
            user.accounts.add(account);
        } else if (type.equalsIgnoreCase("LineOfCredit")) {
            account = new LineOfCreditAccount(user, accountName, currency);
            user.accounts.add(account);
        } else if (type.equalsIgnoreCase("CreditCard")) {
            account = new CreditCardAccount(user, accountName, currency);
            user.accounts.add(account);
        } else if (type.equalsIgnoreCase("StockAccount")) {
            if (user.hasStockAccount()) {
                throw new NameInvalidException("Already have stock account.");
            } else {
                account = new StockAccount(user, accountName);
                user.accounts.add(account);
                user.setStockAccount((StockAccount) account);
            }
        } else {
            throw new NameInvalidException("Invalid account type");
        }

        if (type.equalsIgnoreCase("Savings") || type.equalsIgnoreCase("Chequing") || type.equalsIgnoreCase("LineOfCredit") || type.equalsIgnoreCase("CreditCard") || type.equalsIgnoreCase("StockAccount")) {
            String subject = "New Account Created";
            String content = "Congrats! A new " + type + " account was created for user " + user.getUsername();
            EmailManager.sendEmail(user.getEmail(), subject, content);
        }
    }

    /**
     * Reverts the latest transaction on the given account
     *
     * @param account         Which account to undo the last Transaction of
     * @param numTransactions the number of transactions to undo
     * @return if the transaction was successfully undone
     */
    public boolean undoLatestTransactions(Account account, int numTransactions) {
        List<Transaction> transactions = account.getTransactions();
        if (numTransactions <= transactions.size()) {
            for (int i = 0; i < numTransactions; i++) {
                if (!undoLatestTransaction(account)) {
                    break;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to circumvent deserialization issues.
     * @param newEmployeeHolder UserHolder<Employee> instance to switch to.
     */
    public void setEmployeeHolder(UserHolder<Employee> newEmployeeHolder) {
        this.employeeHolder = newEmployeeHolder;
    }
}

