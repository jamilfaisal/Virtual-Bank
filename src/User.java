package src;

import src.accounts.*;
import src.exceptions.NotEnoughBillsException;
import src.exceptions.NotEnoughFundsException;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * src.User class
 * - Identified with a username and password
 * - Can change password
 * - Owns bank accounts
 * - Can withdraw, deposit, and transfer money between different types of accounts
 */
public class User extends BankInteractor implements Serializable {

    /**
     * A list of each account owned
     */
    public final ArrayList<Account> accounts;


    /**
     * If user has stock account
     */
    private StockAccount stock;

    /**
     * The ID of the user's RFID card, if the user has one. Otherwise, an empty string.
     */
    private String RFID;


    public User(String username, String salt, String hash, String email) {
        super(username, salt, hash, email);
        this.accounts = new ArrayList<>();
        this.RFID = "";
    }


    /**
     * Set this user's RFID field to a new RFID card
     * @param newRFID ID of the new RFID card
     */
    public void setRFID(String newRFID) {
        this.RFID = newRFID;
    }

    /**
     * Check if RFID tag corresponds to the tag stored on the User.
     * @param RFIDToCheck the tag to check
     * @return true if RFID tag matches the one stored on the User, false otherwise.
     */
    public boolean checkRFID(String RFIDToCheck) {
        return this.RFID.equals(RFIDToCheck);
    }


    /**
     * @return A list of this user's accounts
     */
    public ArrayList<Account> getAccounts() {
        return this.accounts;
    }

    /**
     * @return If user has a stock account.
     */
    public boolean hasStockAccount() {
        return this.stock != null;
    }

    /**
     * Sets this user to now having the given stock account
     * @param  account A StockAccount to be used by this User
     */
    public void setStockAccount(StockAccount account) {this.stock = account;}

    /**
     * @return User's stock account.
     */
    public StockAccount getStockAccount() {return this.stock;}



    /**
     * @return Map containing each account and its balance
     */
    public Map<Account, BigDecimal> getAccountBalances() {
        Map<Account, BigDecimal> balances = new HashMap<>();
        for (Account account : this.accounts) {
            balances.put(account, account.getBalance());
        }
        return balances;
    }

    /**
     * @param account The account being checked
     * @return The most recent transaction in the specified account
     */
    public Transaction getRecentTransaction(Account account) {
        if (account.transactions.isEmpty()) {
            return null;
        }
        return account.transactions.get(account.transactions.size() - 1);
    }

    /**
     * Calculates the user's net total
     * (The total of debt account balances subtracted from the total of their asset account balances.)
     *
     * @return The user's net total
     */
    public BigDecimal getNetTotal() {
        BigDecimal total = new BigDecimal(0.00);
        total = total.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        for (Account account : this.accounts) {
            if (account instanceof AssetAccount) {
                total = total.add(account.getBalance());
            } else {
                total = total.subtract(account.getBalance());
            }
        }
        return total;
    }

    /**
     * Identifies account.
     *
     * @param identifier Identifier
     * @return src.accounts.Account
     */
    public Account identifyAccount(Object identifier) {
        for (Account account : this.accounts) {
            System.out.println(account.getAccountName());
            if (identifier instanceof String) {
                if (account.getAccountName().equals(identifier)) {
                    return account;
                }
            } else if (identifier instanceof Integer) {
                if (account.getId() == (int) identifier) {
                    return account;
                }
            }
        }
        return null;
    }

    /**
     * Gets Primary account for that user
     *
     * @return Primary account
     */
    private Account getPrimary() {
        Account primary = null;
        for (Account account : this.accounts) {
            if (account instanceof ChequingAccount && ((ChequingAccount) account).getPrimary()) {
                primary = account;
                break;
            }
        }
        return primary;
    }

    /**
     * Transfers money from one user's account to another.
     *
     * @param money    The amount to be transferred
     * @param account1 The account money is withdrawn from
     * @param account2 The account money is deposited into
     * @return Transfer success
     */
    public boolean innerTransfer(BigDecimal money, Account account1, Account account2) {
        if (account1.equals(account2)) {
            return false;
        }

        if (!(account1 instanceof CreditCardAccount)) {
            return account1.transfer(money, account2);
        }
        return false;

    }

    /**
     * Withdraws money from an account.
     *
     * @param money   The amount of money withdrawn
     * @param account The account money is withdrawn from
     * @param machine The BankMachine to be used for bill retrieval
     * @return A map of the bills taken from machine
     */
    public Map<BigDecimal, Bills> withdraw(BigDecimal money, Account account, BankMachine machine) throws NotEnoughBillsException, NotEnoughFundsException {
        Map<BigDecimal, Bills> cashMap = machine.withdrawal(money);
        if (!account.withdraw(money)) {
            machine.deposit(cashMap);
            throw new NotEnoughFundsException("Not Enough funds in this account to withdraw.");
        }
        return cashMap;
    }

    /**
     * Transfer money to another user's account.
     *
     * @param money     The amount of money transferred.
     * @param account       The account being transferred from.
     * @param otherUser The user receiving the money.
     * @return Transfer success
     */
    public boolean userTransfer(BigDecimal money, Account account, User otherUser) {
        if (account == null) {
            return false;
        }
        return account.transfer(money, otherUser.getPrimary());
    }



    /**
     * Pays a bill by transferring money to a non-user's account (Stored in outgoing.txt)
     *
     * @param money   The amount of money transferred
     * @param account The account money is transferred from
     * @return If the payment was successful
     */
    public boolean payBill(BigDecimal money, Account account) {
        boolean withdraw = account.withdraw(money);
        if (withdraw) {
            try (FileWriter fw = new FileWriter("outgoing.txt", true);
                 PrintWriter out = new PrintWriter(new BufferedWriter(fw))) {
                out.println("Amount received: $" + money + " from " + this.getUsername() + ".");
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }

    }

    /**
     * Requests a joint owner to an account
     *
     * @param account  Account being made joint
     * @param employee The bank employee adding the joint owner
     * @param username the new joint owner of the account
     * @return Joint owner success
     */
    public boolean requestJoint(Account account, Employee employee, String username) {
        if (account == null) {
            System.out.println("account not found!");
            return false;

        } else {
            try {
                employee.addJoint(account, username);
                System.out.println("YES");
                return true;
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return false;
            }

        }
    }
}
