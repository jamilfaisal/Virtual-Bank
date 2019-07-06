package src;

import src.accounts.Account;
import src.utils.TransactionType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * src.Transaction Record
 * - Stores information about every transaction
 */
public class Transaction implements Serializable {

    /**
     * The type of Transaction that this is
     */
    private final TransactionType type;

    /**
     * The User who sent this Transaction
     */
    private final User sender;

    /**
     * The User who received this Transaction
     */
    private final User recipient;

    /**
     * The value of the money sent in this Transaction
     */
    private final BigDecimal value;

    /**
     * The account of the User who sent this Transaction
     */
    private final Account senderAccount;

    /**
     * The account of the User who received this Transaction
     */
    private final Account recipientAccount;

    /**
     * The date when this Transaction occurred
     */
    private final Date transactionDate;

    public Transaction(TransactionType type, User sender, User recipient, Account senderAccount, Account recipientAccount, BigDecimal value, Date transactionDate )  {
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;

        this.senderAccount = senderAccount;
        this.recipientAccount = recipientAccount;
        this.value = value;
        this.transactionDate = new Date(transactionDate.getTime());

    }

    public Transaction(TransactionType type, User sender, Account senderAccount, BigDecimal value, Date transactionDate) {
        this.type = type;

        this.value = value;

        this.transactionDate = new Date(transactionDate.getTime());

        this.sender = sender;
        this.senderAccount = senderAccount;
        this.recipient = null;
        this.recipientAccount = null;
    }

    /**
     * @return The type of transaction (from src.utils.TransactionType enum)
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * @return the Account that sent this Transaction
     */
    public Account getSenderAccount() {

        return this.senderAccount;
    }

    /**
     * @return The Account that received this Transaction
     */
    public Account getRecipientAccount() {
        return this.recipientAccount;

    }

    /**
     * @return The value of the money transferred in the Transaction
     */
    public BigDecimal getValue() {
      return this.value;
    }

    /**
     * @return The User who sent this Transaction
     */
    public User getSender() {
        return sender;
    }

    /**
     * @return The User who received this Transaction
     */
    public User getRecipient() {
        return recipient;
    }

    /**
     * @return The String representation of this Transaction
     */
    public String toString() {

        if (this.recipient == null) {
            return "Type: " + type + " | Sender: " + sender.getUsername() + " | Value: " + new DecimalFormat("#0.##").format(value) + " | Date: " + UserHolder.date().toString();
        } else {
            return "Type: " + type + " | Sender: " + sender.getUsername() + " | Recipient: " + recipient.getUsername() + " | Value: " + new DecimalFormat("#0.##").format(value) + " | Date: " + UserHolder.date().toString();
        }
    }
}
