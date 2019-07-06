Readme for Project

== Steps to start the project ==
1. When cloning project or after, make sure the sources root folder in Project Structure points to phase2 to ensure everything works.
2. Upon cloning project, right-click on the jar files in /lib/ and add them to library.
3. To use the GUI, make sure JavaFX is installed and enabled in your Intellij to use the GUI.
(Reference on adding JavaFX to IntelliJ: https://www.jetbrains.com/help/idea/preparing-for-javafx-application-development.html)
4. The main entry point of the project is in the src.gui.ATMMainFX class, run it to start the GUI.
5. The default bank manager's user name is "ATMManager" and the default password is "admin". After logging into manager, you can create new managers and users.
6. To login as a user, after user creation, use user login from the main login page to access the user functions and accounts.
7. If you are creating a new account or requesting a joint account as a user, a manager must login to fullfill the request, you will automatically be prompted for the login.
8. The GUI is built with accessibility in mind so all program features can be found in either the user pages or the employee pages.
9. Our program uses serialization to store data, so if you terminate the program accidentally, it might not save the data. Always click exit or press the x button to leave the program
and save the data.

== Extra features that we have added ==
1. Stock Trading
    - Users can now buy stocks with the balance in their stock account to invest.
    - Users can now sell stocks in their stock account for profit or loss.
    - Stock accounts function like normal accounts where they can be deposited/withdrawn and transferred in and out from.
    - Commission fees also get withdrawn upon each transaction by the bank, the amount depends on the balance of the stock account.
    - Limit for issuing stock buy and sell requests is 5 per minute as the API we are using has a request limit.

2. Multiple Currencies
    - Users can set currencies for each account they have, apart from the default checking account.
    - Upon deposit, the amount deposited automatically converts from CAD to currency of the account.
    - Upon withdrawal, the amount automatically turns into CAD.
    - Since our API supports crypto-currencies, now you can now have BTC accounts!

3. RFID Cards
    - If you have the RFID card reader for a Raspberry PI, you can set a User to have a RFID card associated with their account.
    - Users can log in using their respective RFID cards in lieu of a username and password.
    - Setting a new RFID card overrides the old one so it is a secure log in.
    - This was demoed successfully during the presentation.

4. Email Management System
    - Users can now receive emails regarding their account from the email they entered upon account creation.
    - Emails inform users of new accounts created, overdraft or exceeding maximum debt.
    - Emails also send a cautionary email if the user is signing in from an unknown location.

5. Geolocation
    - Each user has a last logged in city associated.
    - If users log in from an unknown location, a cautionary email is sent to the user.

6. Premier accounts
    - If an account reaches a certain threshold of balance, they gain special privileges.
    - Premier chequing account can overdraft up to a $1000, savings accounts earn 5% every month, and stock accounts pay less commission.

== Things to Note ==
- DISCLAIMER: Pressing log in with RFID or set RFID without having a Raspberry PI with the server setup will cause the program to timeout for 20 seconds.

- Emails are sent to the email address of the user whenever their user account is first made, if the check balance feature
shows a balance less than 0, or if they have exceeded the maximum amount of debt. Emails are sent from a new email address
created for this project (csc207atmmachine@gmail.com), and because it is a new account, emails may end up in your spam / junk folder.

- All withdrawals are in CAD, and deposits are converted to the default currency specified by the user.

- DebtAccounts have a positive balance when money is owed, whereas AssetAccounts have a positive balance when the user has money.

- Maximum amount of debt for CreditCards is $10000 CAD and Line Of Credit accounts is $100000. Once this amount of debt is exceeded, most of the functionality in the
that account is locked until more funds are deposited into the account and the debt goes below the max level.

- If view stocks is returning empty or giving an index exception when buying or viewing stocks, the Stock API is being overloaded, due to us using the free plan we can only
send a maximum of 5 requests so sometimes it will only return certain info for a share. If we were able to buy the premium account for the API, we could request up to a lot more.

- If a serialization error comes up when starting the program due to editing of any class files, please delete the serialization files to reinitialize the program.

== Code we have used from other sources ==
HTTPUtility: www.codejava.net
NumberTextField: Stack Overflow
PasswordSecurity (partial): https://dev.to/awwsmm/how-to-encrypt-a-password-in-java-42dh

== Using deposits.txt ==
While depreciated as you can now use the GUI, if you would like to use deposits.txt:
The initial deposits.txt file contains sample deposits with correct formatting, however the users and accounts of the
sample deposits don't exist when the program is started, they are just for demonstration of the correct format.

The deposits.txt file structure is as described below:

Each line represents 1 deposit.

For cash deposits, the following format should be used:
<username>, <account name>, cash, <number of fives>, <number of tens>, <number of twenties>, <number of fifties>
For example:
shayanKhalili, myMainAccount, cash, 0, 0, 2, 3

For cheque deposits, the following format should be used:
<username>, <account name>, cheque, <amount>
For example:
rishabhL, account1, cheque, 192.55

