package src;

import src.accounts.Account;
import src.accounts.AssetAccount;
import src.accounts.DebtAccount;
import src.accounts.LineOfCreditAccount;
import src.exceptions.NameInvalidException;
import src.exceptions.TypeInvalidException;
import src.utils.DepositHelper;
import src.utils.TransactionType;
import src.utils.CurrencyConverter;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public abstract class Employee extends BankInteractor implements Serializable {
    /**
     * the name of this employee's job
     */
    private final String jobTitle;

    UserHolder<User> userHolder;

    Employee(String username, String jobTitle, String salt, String hash, UserHolder<User> userHolder, String email) {
        super(username, salt, hash, email);
        this.jobTitle = jobTitle;
        this.userHolder = userHolder;
    }

    /**
     * Returns the job title of this employee
     *
     * @return String the string representation of this employee's job title
     */
    @SuppressWarnings("unused")
    public String getJobTitle() {
        return this.jobTitle;
    }

    /**
     * Method to circumvent deserialization issues.
     * @param newUserHolder The UserHolder<User> instance to switch to.
     */
    public void setUserHolder(UserHolder<User> newUserHolder) {
        this.userHolder = newUserHolder;
    }

    /**
     *
     * @param account The account to be made joint
     * @param username the username of the user who owns the account
     * @throws IOException If the given user does not exist
     */
    public void addJoint(Account account, String username) throws IOException {
        User user = userHolder.getUser(username);
        if (user == null) {
            throw new NameInvalidException("src.User with given username dose not exist");
        } else {
            account.addJoint(user);
            user.accounts.add(account);
        }

    }

    /**
     * Undo the latest transaction on a user's account
     * @param account the account to undo the transaction on
     * @return boolean to indicate if the operation was successful
     */
    public boolean undoLatestTransaction(Account account) {
        if (!account.transactions.isEmpty()) {
            int lastItemIndex = account.transactions.size() - 1;
            Transaction latestTransaction = account.transactions.remove(lastItemIndex);

            if (latestTransaction.getType() == TransactionType.DEPOSIT) {
                //undo deposit
                Account userAccount = latestTransaction.getSenderAccount();

                BigDecimal value = latestTransaction.getValue();

                if (userAccount instanceof DebtAccount) {
                    //undo deposit of debt account - so balance goes up (amount owed goes up)
                    userAccount.balance = userAccount.balance.add(value);
                } else if (userAccount instanceof AssetAccount) {
                    //undo deposit to asset account - so balance goes down
                    userAccount.balance = userAccount.balance.subtract(value);
                }

                return true;

            } else if (latestTransaction.getType() == TransactionType.WITHDRAW) {
                Account senderAccount = latestTransaction.getSenderAccount();
                BigDecimal value = latestTransaction.getValue();

                if (senderAccount instanceof LineOfCreditAccount) {
                    //(only LineOfCreditAccounts can have withdrawals to begin with) undo withdrawal of debt account - so balance goes down (amount owed goes down)
                    senderAccount.balance = senderAccount.balance.subtract(value);
                } else if (senderAccount instanceof AssetAccount) {
                    //undo withdrawal to asset account - so balance goes up
                    senderAccount.balance = senderAccount.balance.add(value);
                }
                return true;
            } else if (latestTransaction.getType() == TransactionType.TRANSFER)  {
                //undo transfer
                Account senderAccount = latestTransaction.getSenderAccount();
                BigDecimal value = latestTransaction.getValue();

                Account recipientAccount = latestTransaction.getRecipientAccount();

                if (recipientAccount == null) {
                    //this was a transfer to a non-user account

                    if (senderAccount instanceof AssetAccount) {
                        //adding to asset, balance goes up
                        senderAccount.balance = senderAccount.balance.add(value);
                        return true;
                    } else if (senderAccount instanceof DebtAccount) {
                        //reducing debt, balance goes down
                        senderAccount.balance = senderAccount.balance.subtract(value);
                        return true;
                    }
                } else {
                    //balance of this account increases, balance of other account decreases

                    if (senderAccount instanceof AssetAccount) {
                        //adding to asset, balance goes up
                        senderAccount.balance = senderAccount.balance.add(value);
                    } else if (senderAccount instanceof DebtAccount) {
                        //reducing debt, balance goes down
                        senderAccount.balance = senderAccount.balance.subtract(value);
                    }

                    if (recipientAccount instanceof AssetAccount) {
                        //balance goes down
                        recipientAccount.balance = recipientAccount.balance.subtract(CurrencyConverter.convert(value,
                                senderAccount.getCurrency(), recipientAccount.getCurrency()));
                        recipientAccount.transactions.remove(latestTransaction);
                        return true;
                    } else if (recipientAccount instanceof DebtAccount) {
                        //balance goes up since debt goes up
                        recipientAccount.balance = recipientAccount.balance.add(CurrencyConverter.convert(value,
                                senderAccount.getCurrency(), recipientAccount.getCurrency()));
                        recipientAccount.transactions.remove(latestTransaction);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Fulfills the deposits in deposits.txt
     * @param depositsFile the path to the deposits.txt file
     * @param atm The instance of src.BankMachine to deposit the money to
     * @return A map from the lines in deposit.txt that failed to an Exception object dictating the reason they failed
     */
    public Map<String, Exception> fulfillDeposits(String depositsFile, BankMachine atm) {
        Path path = Paths.get(depositsFile);
        Map<String, Exception> failures = new HashMap<>();
        try (BufferedReader fileInput = Files.newBufferedReader(path)) {
            int lineCounter = 0;
            String line = fileInput.readLine();
            // iterate through each line of the file.
            while (line != null) {
                lineCounter++;
                // split the line into a string array by commas.
                String[] lineArray = line.split(",");
                // trim each entry in the line of leading and trailing whitespace.
                for (int i = 0; i < lineArray.length; i++) {
                    lineArray[i] = lineArray[i].trim();
                }
                // the line must have exactly 4 entries if its a cheque deposit, or more if it's a cash deposit.
                if (lineArray.length < 4) {
                    failures.put(line, new IOException("Invalid format for deposit on line " + lineCounter));
                    line = fileInput.readLine();
                    continue;
                }
                String username = lineArray[0];
                User user = userHolder.getUser(username);
                if (user == null) {
                    failures.put(line, new NameInvalidException("Invalid username on line " + lineCounter));
                    line = fileInput.readLine();
                    continue;
                }
                String accountName = lineArray[1];
                Account account = user.identifyAccount(accountName);
                if (account == null) {
                    failures.put(line, new NameInvalidException("Invalid account name on line " + lineCounter));
                    line = fileInput.readLine();
                    continue;
                } else if (account instanceof DebtAccount) {
                    failures.put(line, new TypeInvalidException("src.accounts.Account specified on line " + lineCounter + " is a debt account"));
                    line = fileInput.readLine();
                    continue;
                }
                AssetAccount assetAccount = (AssetAccount) account;
                DepositHelper depositHelper= new DepositHelper();
                String depositType = lineArray[2];
                if (depositType.equals("cash")) {
                    depositHelper.fulfillCashDeposit(line, lineArray, lineCounter, failures, atm, assetAccount);
                } else if (depositType.equals("cheque")) {
                    depositHelper.fulfillChequeDeposit(line, lineArray, failures, assetAccount);
                } else {
                    failures.put(line, new NameInvalidException("Invalid deposit type on line " + lineCounter));
                }
                line = fileInput.readLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
        try (FileWriter fileWriter = new FileWriter(depositsFile)) {
            fileWriter.write("");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return failures;
    }
}
