package src.accounts;

import src.Transaction;
import src.utils.TransactionType;
import src.User;
import src.UserHolder;
import src.utils.CurrencyConverter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Savings src.accounts.Account
 * - Extends src.accounts.Account
 * - Grows interest by 1% every month
 */
public class SavingsAccount extends Account implements AssetAccount, Serializable {
    private boolean isPremier;

    public SavingsAccount(User owner, String accountName, String currency) {
        super(owner, accountName, currency);
    }

    /**
     * @return If account is a premier account
     */
    public boolean isPremier() { return this.isPremier; }

    /**
     * Sets an account as premier or non-premier.
     */
    public void setPremier() { this.isPremier = !this.isPremier;}

    /**
     * Deposits into account
     *
     * @param initialAmount Amount of money being deposited in the base currency
     * @return Withdrawal successful
     */
    public boolean deposit(BigDecimal initialAmount) {

        BigDecimal money = CurrencyConverter.convert(initialAmount, this.BASE, this.getCurrency());

        if (money.compareTo(BigDecimal.ZERO) > 0) {
            this.balance = this.balance.add(money);
            if (this.balance.compareTo(new BigDecimal(10000.00)) > 0 && !this.isPremier()) {
                setPremier();
            }
            this.transactions.add(new Transaction(TransactionType.DEPOSIT, this.getUser(), this, new BigDecimal(money.doubleValue()), UserHolder.date()));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Withdraws money
     *
     * @param initialAmount Amount of money that needs to be withdrawn in the base currency
     * @return Withdrawal successful
     */
    public boolean withdraw(BigDecimal initialAmount) {
        BigDecimal money = CurrencyConverter.convert(initialAmount, this.BASE, this.getCurrency());

        if (this.balance.compareTo(money) >= 0) {
            this.balance = this.balance.subtract(money);
            if (this.isPremier() && this.balance.equals(BigDecimal.ZERO)) {
                this.setPremier();
            }
            if (this.isPremier() && this.balance.compareTo(new BigDecimal(1000.00)) <= 0) {
                this.setPremier();
            }
            this.transactions.add(new Transaction(TransactionType.WITHDRAW, this.getUser(), this, money, UserHolder.date()));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Increases account balance by 1%
     */
    public void growInterest() {
        BigDecimal interest = new BigDecimal(1.01);
        if (this.isPremier()) {
            interest = new BigDecimal(1.05);
        }
        this.balance = this.balance.multiply(interest);
    }


}
