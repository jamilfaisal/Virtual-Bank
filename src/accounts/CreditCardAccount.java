package src.accounts;

import src.Transaction;
import src.utils.TransactionType;
import src.User;
import src.UserHolder;
import src.utils.CurrencyConverter;
import src.utils.EmailManager;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Chequing src.accounts.Account
 * - Extends src.accounts.Account
 * - Depositing money decreases balance
 * - Withdrawing money increases balance
 */
public class CreditCardAccount extends Account implements DebtAccount, Serializable {
    /**
     * The maximum amount of debt that is allowed
     */
    private final BigDecimal maxDebt = new BigDecimal(10000.00);

    public CreditCardAccount(User owner, String accountName, String currency) {
        super(owner, accountName, currency);
    }

    /**
     * Deposits into account
     *
     * @param initialAmount Amount of money being deposited in the base currency
     * @return Withdrawal successful
     */
    public boolean deposit(BigDecimal initialAmount) {

        BigDecimal money = CurrencyConverter.convert(initialAmount, this.BASE, this.getCurrency());

        //amount owed decreases
        this.balance = this.balance.subtract(money);

        this.transactions.add(new Transaction(TransactionType.DEPOSIT, this.getUser(), this, money, UserHolder.date()));
        if (maxDebt.compareTo(this.balance) > 0) {
            this.makeActive();
            String emailSubject = "Your Credit Card is now reactivated!";
            String emailContent = "Dear user, \n Your credit card account has been unlocked as you have paid your debt. \n " +
                    "Your new balance is $" + this.balance;
            EmailManager.sendEmail(this.getUser().getEmail(), emailSubject, emailContent);
        }
        return true;
    }

    /**
     * Withdraw from account
     *
     * @param initialAmount Amount of money being withdrawn in the base currency
     * @return Withdrawal successful
     */
    @Override
    public boolean withdraw(BigDecimal initialAmount) {

        BigDecimal money = CurrencyConverter.convert(initialAmount, this.BASE, this.getCurrency());
        if (this.isActive) {
            //amount owed increases
            this.isActive = false;
            this.balance = this.balance.add(money);
            if (money.add(this.balance).compareTo(this.maxDebt) > 0) {
                String emailSubject = "Warning! Your Credit Card Account has exceeded the max level of debt!";
                String emailContent = "Dear user, \n Your account has been locked due to excessive debt. \n " +
                        "Please deposit more money in order to gain access to the rest of your account. \n" +
                        "You currently owe: " + this.balance;
                EmailManager.sendEmail(this.getUser().getEmail(), emailSubject, emailContent);
            }
            return true;
        } else {
            return false;

        }
    }
}
