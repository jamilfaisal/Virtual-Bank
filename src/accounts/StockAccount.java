package src.accounts;

import src.Transaction;
import src.utils.TransactionType;
import src.User;
import src.UserHolder;
import src.utils.CurrencyConverter;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * StockAccount src.accounts.Account
 * - A stock account enabling users to buy and sell stocks
 */
public class StockAccount extends Account implements AssetAccount, Serializable {
    //public HashMap<String, Object[]> stocks;
    /**
     * A list of stocks to buy
     */
    private final List<Stock> stocks;
    private BigDecimal commission;

    /**
     * Whether or not this is a premier account
     */
    private boolean isPremier;

    public StockAccount(User owner, String accountName) {
        super(owner, accountName, "USD");
        //stocks = new HashMap<>();
        stocks = new ArrayList<>();
        this.commission = new BigDecimal(7.50);
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
     * @param initialAmount Amount of money being deposited
     * @return Withdrawal successful
     */
    public boolean deposit(BigDecimal initialAmount) {
        BigDecimal money = CurrencyConverter.convert(initialAmount, this.getCurrency(), this.BASE);

        if (money.compareTo(BigDecimal.ZERO) > 0) {
            this.balance = this.balance.add(money);

            this.transactions.add(new Transaction(TransactionType.DEPOSIT, this.getUser(), this, new BigDecimal(money.doubleValue()), UserHolder.date()));
            if (this.balance.compareTo(new BigDecimal(1000.00)) > 0 && !this.isPremier()) {
                setPremier();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Withdraws money
     *
     * @param initialAmount Amount of money that needs to be withdrawn
     * @return Withdrawal successful
     */
    public boolean withdraw(BigDecimal initialAmount) {
        BigDecimal money = CurrencyConverter.convert(initialAmount, this.getCurrency(), this.BASE);

        if (this.balance.compareTo(money) >= 0) {
            this.balance = this.balance.subtract(money);

            this.transactions.add(new Transaction(TransactionType.WITHDRAW, this.getUser(), this, money, UserHolder.date()));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Buy a specific stock
     *
     * @param symbol Stock Ticker
     * @param amount Amount of shares
     * @return Buy successful
     */
    public boolean buyStock(String symbol, int amount) throws IOException {
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        BigDecimal price = stock.getInfo().get(0);
        if (price.equals(new BigDecimal(0.00))) {
            return false;
        }
        stock.setBuyPrice(price);
        stock.amount = amount;
        BigDecimal buyPrice = price.multiply(new BigDecimal(amount));
        if (this.isPremier) {
            this.commission = new BigDecimal(4.00);
        }
        buyPrice = buyPrice.add(this.commission);
        if (this.balance.compareTo(buyPrice) >= 0) {
            this.balance = this.balance.subtract(buyPrice);
            //Object[] details = new Object[][3];
            //details[0] = (int) this.stocks.get(symbol)[0] + amount;
            //details[1] = buyPrice.doubleValue();
            //if (this.stocks.containsKey(symbol)) {
            //    this.stocks.put(symbol, details);
            //} else {
            //    this.stocks.put(symbol, details);
            //}
            this.stocks.add(stock);
            return true;
        } else {
            throw new IOException("Not enough money for transaction");
        }
    }

    /**
     * sell a specific stock
     *
     * @param symbol Stock Ticker
     * @param amount Amount of shares
     * @return Selling successful
     */
    public boolean sellStock(String symbol, int amount) throws IOException{
        Stock selling = new Stock();
        boolean sold = false;
        for (Stock stock: this.stocks) {
            if (stock.getSymbol().equals(symbol)) {
                System.out.println("selling");
                selling = stock;
                BigDecimal price = selling.getInfo().get(0);
                BigDecimal sellPrice = price.multiply(new BigDecimal(amount));
                if (selling.amount >= amount) {
                    selling.amount -= amount;
                    this.balance = this.balance.add(sellPrice);
                    sold = true;
                    break;
                } else {
                    throw new IOException("Not enough shares to sell!");
                }
            }
        }
        if (!sold) {
            throw new IOException("Not enough shares to sell!");
        }
        if (selling.amount == 0) {
            this.stocks.remove(selling);
        }
        return true;
    }

    /**
     *
     * @return A list of all stocks in this account
     */
    public List<Stock> getStocks() {
        List<Stock> display = new ArrayList<>();
        for (Stock stock: this.stocks) {
            // what is going on here?
            List<BigDecimal> infoList = stock.getInfo();
            if (infoList.size()==0) {
                return display;
            }
            stock.percentage = infoList.get(1);
            stock.currentPrice = infoList.get(0);
            display.add(stock);
        }
        return display;
    }


}
