package src.accounts;

import src.Transaction;
import src.utils.TransactionType;
import src.User;
import src.UserHolder;
import src.utils.CurrencyConverter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Chequing src.accounts.Account
 * - Extends src.accounts.Account
 * - Cannot transfer out but can transfer in
 */
public class ChequingAccount extends Account implements AssetAccount, Serializable {
    private boolean primary;
    private boolean isPremier;

    public ChequingAccount(User owner, String accountName, String currency, boolean primary) {
        super(owner, accountName, currency);
        this.primary = primary;
    }

    /**
     * @return If account is primary
     */
    public boolean getPrimary() {
        return this.primary;
    }

    /**
     * Sets primary account
     * Never implemented
     * @param primary To set account as primary or not.
     */
    public void setPrimary(boolean primary) {
        this.primary = primary;
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

        if (money.doubleValue() > 0) {
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
     * Withdraw from account
     *
     * @param initialAmount Amount of money being withdrawn in the base currency
     * @return Withdrawal successful
     */
    @Override
    public boolean withdraw(BigDecimal initialAmount) {

        BigDecimal money = CurrencyConverter.convert(initialAmount, this.BASE, this.getCurrency());

        /*
        if (!getCurrency().equals(BASE)) {
            money = CurrencyConverter.convert(money, BASE, getCurrency());
        }
        */
        BigDecimal b1 = new BigDecimal(-100);
        if (this.isPremier()) {
            b1 = new BigDecimal(-1000);
        }
        if ((this.balance.subtract(money)).compareTo(b1) >= 0) {
            if (this.balance.compareTo(money) >= 0) {
                this.balance = this.balance.subtract(money);
                this.transactions.add(new Transaction(TransactionType.WITHDRAW, this.getUser(), this, money, UserHolder.date()));
                if (this.isPremier() && this.balance.compareTo(new BigDecimal(1000.00)) <= 0) {
                    this.setPremier();
                }
                return true;
            }
        }
        return false;
    }
}
