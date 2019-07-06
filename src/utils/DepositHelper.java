package src.utils;

import src.accounts.AssetAccount;
import src.BankMachine;
import src.Bills;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DepositHelper {
    /**
     * Helper for fulfillDeposits when the deposit type is cash.
     * @param line the current line in deposits.txt
     * @param lineArray the current line in deposits.txt split into an array by commas
     * @param lineCounter the number of the current line being processed
     * @param failures A map from the lines in deposit.txt that failed to an Exception object dictating the reason they failed
     * @param atm The instance of src.BankMachine to deposit the money into
     * @param assetAccount The account whose balance will increase from the deposit
     */
    public void fulfillCashDeposit(String line, String[] lineArray, int lineCounter, Map<String, Exception> failures, BankMachine atm, AssetAccount assetAccount) {
        // the following ArrayList contains all of the types of bills in the ATM.
        ArrayList<BigDecimal> billTypeList = new ArrayList<>(atm.getCashMap().keySet());
        Collections.sort(billTypeList);
        if (lineArray.length < 3 + billTypeList.size()) {
            failures.put(line, new IOException("Not enough fields specifying bill quantities. Line " +
                    lineCounter + " has " + (lineArray.length - 3) + " when it should have " +
                    billTypeList.size()));
            return;
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        totalAmount = totalAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Map<BigDecimal, Bills> billsToDeposit = new HashMap<>();

        for (int i = 0; i < billTypeList.size(); i++) {
            int quantity;
            try {
                quantity = Integer.parseInt(lineArray[i + 3]);
            } catch (NumberFormatException e) {
                failures.put(line, e);
                return;
            }

            Bills bills = new Bills(billTypeList.get(i), quantity);
            billsToDeposit.put(billTypeList.get(i), bills);

            int intVal = billTypeList.get(i).intValue();
            // multiply the value of the bill with the quantity and add it to the total balance.
            totalAmount = totalAmount.add(new BigDecimal(intVal * quantity));
        }
        atm.deposit(billsToDeposit);
        assetAccount.deposit(totalAmount);

    }

    /**
     * Helper for fulfillDeposits when the deposit type is cheque.
     * @param line the current line in deposits.txt
     * @param lineArray the current line in deposits.txt split into an array by commas
     * @param failures A map from the lines in deposit.txt that failed to an Exception object dictating the reason they failed
     * @param assetAccount The account whose balance will increase from the deposit
     */
    public void fulfillChequeDeposit(String line, String[] lineArray, Map<String, Exception> failures, AssetAccount assetAccount) {
        try {
            // at this point, 4th field in the line should be the deposit amount on the cheque.
            BigDecimal depositAmount = new BigDecimal(lineArray[3]);
            assetAccount.deposit(depositAmount);
        } catch (NumberFormatException e) {
            failures.put(line, e);
        }
    }
}
