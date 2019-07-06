package src.utils;

import src.utils.HttpUtility;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrencyConverter implements Serializable {
    private static final String url = "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&";


    /***
     *
     * @param amount The amount of money in the currency to be converted from
     * @param from The currency to convert from
     * @param to The currency to convert to
     * @return The equivalent value of amount in the to currency
     */
    public static BigDecimal convert(BigDecimal amount, String from, String to) {
        String requestURL = url + "from_currency=" + from + "&to_currency=" + to + "&apikey=7W501V15THJBMUZ6";
        BigDecimal value = new BigDecimal(0.00);
        value = value.setScale(2, RoundingMode.CEILING);
        try {
            HttpUtility.sendGetRequest(requestURL);
            String[] response = HttpUtility.readMultipleLinesRespone();
            for (String line : response) {
                if (line.contains("Rate")) {
                    String[] arrOfStr = line.split(":", 2);
                    line = arrOfStr[1];
                    Pattern pattern = Pattern.compile("\\d*\\.\\d*");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        value = new BigDecimal(matcher.group());
                        //System.out.println(value);
                    }
                }

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpUtility.disconnect();
        return amount.multiply(value);
    }

    public static boolean isCurrency(String currency) {
        String requestURL = url + "from_currency=" + currency + "&to_currency=CAD&apikey=7W501V15THJBMUZ6";
        try {
            HttpUtility.sendGetRequest(requestURL);
            String[] response = HttpUtility.readMultipleLinesRespone();
            for (String line : response) {
                if (line.contains("Error")) {
                    return false;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpUtility.disconnect();
        return true;
    }
}
