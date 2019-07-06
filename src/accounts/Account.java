package src.accounts;

import src.*;
import src.utils.CurrencyConverter;
import src.utils.EmailManager;
import src.utils.TransactionType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * src.accounts.Account Abstract class
 * - Identified with a id
 * - Holds balance, accountName, accountOwner, creationDate and list of transactions
 * - To be extended by Chequing src.accounts.Account, src.accounts.SavingsAccount, CreditAccount, LOCAccount
 */

public abstract class Account implements Serializable {
    /**
     * accountName used to identify the account
     */
    private final String accountName;
    /**
     * Unique id for every account
     */
    private final int id;
    /**
     * Which user this account belongs to.
     */
    private final User owner;
    /**
     * Joint account user.
     */
    private User owner2;
    /**
     * Balance for every account, use BigDecimal for precision to two decimal places
     */
    public BigDecimal balance;
    /**
     * Creation date for the account
     */
    private final Date creationDate;
    /**
     * List of Transactions for each account
     */
    public final List<Transaction> transactions;

    /**
     * Represents whether or not the account is currently active
     */
    boolean isActive;



    /***
     * The currency of the money in this account
     */
    private final String currency;

    /***
     * the base currency of this ATM
     */
    final String BASE;

    /**
     * Creates a new account
     */
    Account(User owner, String accountName, String currency) {
        this.owner = owner;
        this.accountName = accountName;
        this.balance = new BigDecimal(0.00);
        this.balance = this.balance.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        this.id = (int) Instant.now().toEpochMilli();
        this.creationDate = new Date();
        this.currency = currency;
        BASE = "CAD";
        this.transactions = new ArrayList<>();


        this.isActive = true;
    }

    /**
     * @return This account's balance
     */
    public BigDecimal getBalance() {
        BigDecimal b1 = new BigDecimal(0);

        if (this instanceof AssetAccount && this.balance.compareTo(b1) < 0) {
            String subject = "Warning: Account Balance Negative!";
            String content = "Please be aware that the balance for your " + this.accountName + " with id " + this.id + " has a negative balance of: " + this.balance + ". Make sure to make payments soon!";
            EmailManager.sendEmail(this.getUser().getEmail(), subject, content);
        }

        return this.balance;
    }

    /**
     * @return return account id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * @return This account's transactions
     */
    public List<Transaction> getTransactions() {
        return this.transactions;
    }

    /**
     * Gets account name
     *
     * @return src.accounts.Account name
     */
    public String getAccountName() {
        return this.accountName;
    }

    public String getCurrency() {
        return currency;
    }

    /**
     * Transfers money into another account.
     *
     * @param initialAmount   Money being transferred.
     * @param account which account its being transferred to.
     * @return Transfer success
     */
    public boolean transfer(BigDecimal initialAmount, Account account) {

        BigDecimal money = CurrencyConverter.convert(initialAmount, this.BASE, this.currency);

        if (this.balance.compareTo(money) >= 0) {
            BigDecimal otherMoney = CurrencyConverter.convert(money, currency, account.getCurrency());

            if(this instanceof AssetAccount) {
                if (account instanceof AssetAccount) {
                    this.balance = this.balance.subtract(money);
                    account.balance = account.balance.add(otherMoney);
                } else {
                    this.balance = this.balance.subtract(money);
                    account.balance = account.balance.subtract(otherMoney);
                }
            } else if(this instanceof LineOfCreditAccount) {
                if (account instanceof AssetAccount) {
                    this.balance = this.balance.add(money);
                    account.balance = account.balance.add(otherMoney);
                } else {
                    this.balance = this.balance.add(money);
                    account.balance = account.balance.subtract(otherMoney);
                }
            } else if (this instanceof CreditCardAccount) {
                return false;
            }
            //logs transaction between the users
            Transaction transaction = new Transaction(TransactionType.TRANSFER, this.getUser(), account.getUser(), this, account, money, UserHolder.date());
            this.transactions.add(transaction);
            account.transactions.add(transaction);

            return true;
        } else {
            return false;
        }
    }

    /**
     * @return This account's owner
     */
    User getUser() {
        return this.owner;
    }

    /**
     * Gives the date of creation of this account
     *
     * @return date of creation of this account
     */
    public Date getCreationDate() {
        return this.creationDate;
    }


    /**
     * sets the account to be active
     */
    void makeActive() {
        isActive = true;
    }

    public abstract boolean withdraw(BigDecimal money);

    public abstract boolean deposit(BigDecimal money);

    /**
     * adds a joint owner.
     * @param user User being added as joint owner.
     */
    public void addJoint(User user) {
        this.owner2 = user;
    }

    /**
     * @return This account's joint owner.
     */
    public String getJoint(String username) {

        if (this.owner2 == null) {
            return "N/A";
        }
        else {
            String username2 = this.owner2.getUsername();
            if (username.equals(username2)) {
                return this.owner.getUsername();
            } else {
                return username2;
            }
        }
    }

    /**
     * @return If account is active.
     */
    public String isActive() {
        if (this.isActive) {
            return "ACTIVE";
        } else {
            return "DEACTIVATED";
        }
    }

}
