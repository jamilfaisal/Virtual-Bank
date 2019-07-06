package src;

import src.exceptions.NotEnoughBillsException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

public class BankMachine implements Serializable {
    // map from value of bill (5, 10, 20, 50) to matching src.Bills object
    private final Map<BigDecimal, Bills> cash;

    public BankMachine() {
        cash = new HashMap<>();
        cash.put(new BigDecimal(50), new Bills(new BigDecimal(50), 0));
        cash.put(new BigDecimal(20), new Bills(new BigDecimal(20), 0));
        cash.put(new BigDecimal(10), new Bills(new BigDecimal(10), 0));
        cash.put(new BigDecimal(5), new Bills(new BigDecimal(5), 0));
    }

    /**
     * Returns the map of bill values to bills containing all of this machine's bills
     *
     * @return Map containing all bills in this machine
     */
    public Map<BigDecimal, Bills> getCashMap() {
        return this.cash;
    }

    /**
     * Returns the total value of all bills stored in this machine
     *
     * @return BigDecimal representing the sum of all bills in this machine
     */
    private BigDecimal totalBalance() {
        BigDecimal totalBalance = new BigDecimal(0.00);
        totalBalance = totalBalance.setScale(2, BigDecimal.ROUND_HALF_EVEN);

        for (BigDecimal key : cash.keySet()) {
            totalBalance = totalBalance.add(key.multiply(new BigDecimal(cash.get(key).getQuantity())));
        }
        return totalBalance;
    }

    /**
     * Attempts to withdrawal the given map of bills from the machine. This will reduce the number of bills
     * in this src.BankMachine and give them to the user.
     *
     * @param amount the dollar value which is being requested
     * @return Map of bill values to bills containing the user's withdrawal
     */
    public Map<BigDecimal, Bills> withdrawal(BigDecimal amount) throws NotEnoughBillsException {
        Map<BigDecimal, Bills> withdraw = new HashMap<>();
        List<BigDecimal> bills = new ArrayList<>(cash.keySet());
        if(this.totalBalance().compareTo(amount) < 0){
            throw new NotEnoughBillsException("Sorry, our ATM does not have enough money for this withdrawal.");
        }
        bills.sort(Collections.reverseOrder());

        for (BigDecimal billValue : bills) {
            if (amount.compareTo(billValue) >= 0) {
                int numberOfBill = Math.min(amount.intValue() / billValue.intValue(),
                        cash.get(billValue).getQuantity());
                amount = amount.subtract(billValue.multiply(new BigDecimal(numberOfBill)));
                Bills withdrawBill = new Bills(billValue, numberOfBill);
                withdraw.put(billValue, withdrawBill);
            }
        }
        if (!amount.equals(new BigDecimal(0))) {
            throw new NotEnoughBillsException("The given amount for withdrawal is not possible");
        }
        else { // removes bills from the ATM if the withdrawal was successful
            for (BigDecimal billValue : withdraw.keySet()) {
                cash.get(billValue).reduceQuantity(withdraw.get(billValue).getQuantity());
            }
        }
        try {
            alertCash();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return withdraw;
    }

    /**
     * Deposits the given map of bills into this machine.
     *
     * @param amounts the value and quantity of bills deposited
     */
    public void deposit(Map<BigDecimal, Bills> amounts) {
        for (BigDecimal dollarValue : amounts.keySet()) {
            if (cash.containsKey(dollarValue)) {
                cash.get(dollarValue).addQuantity(amounts.get(dollarValue).getQuantity());
            } else {
                cash.put(dollarValue, amounts.get(dollarValue));
            }
        }
        try {
            alertCash();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends an alert to the alert file if there are less than 20 of any given bill
     *
     */
    private void alertCash() throws IOException {
        StringBuilder alertMessage = new StringBuilder();
        for (BigDecimal key : cash.keySet()){
            if(cash.get(key).getQuantity() < 20){
                alertMessage.append("On ").append(UserHolder.date()).append(", the ATM is low on ");
                alertMessage.append(key.toString()).append(". Please restock money.\n");
            }
        }
        // file to send low bill alerts to
        String alertFile = "alerts.txt";
        File aFile = new File(alertFile);
        FileWriter aWriter = new FileWriter(aFile, false);
        aWriter.write(alertMessage.toString());
        aWriter.close();
    }
}
