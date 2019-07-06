package src.accounts;

import src.utils.HttpUtility;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stock src.accounts.Account
 * - A stock class that holds info about a stock when bought
 */
public class Stock implements Serializable {
    /**
     * The symbol identifying this stock
     */
    private String symbol;

    /**
     * The price the stock was bought at in USD
     */
    private BigDecimal buyPrice;

    /**
     * The price the stock is currently at in USD
     */
    public BigDecimal currentPrice;

    /**
     * The amount of this stock currently owned
     */
    public int amount;

    /**
     *
     */
    public BigDecimal percentage;

    /**
     * Gets current price from AlphaVantage API
     * @return Current share price in BigDecimal
     */
    public List<BigDecimal> getInfo() {
        String requestURL = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + this.symbol + "&apikey=7W501V15THJBMUZ6";
        BigDecimal value = new BigDecimal(0.00);
        BigDecimal percentage = new BigDecimal(0.00);
        List<BigDecimal> infoList = new ArrayList<>();
        value = value.setScale(2, RoundingMode.CEILING);
        try {
            HttpUtility.sendGetRequest(requestURL);
            String[] response = HttpUtility.readMultipleLinesRespone();
            for (String line : response) {
                if (line.contains("price")) {
                    String[] arrOfStr = line.split(":", 2);
                    line = arrOfStr[1];
                    Pattern pattern = Pattern.compile("\\d*\\.\\d*");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        value = new BigDecimal(matcher.group());
                        infoList.add(value);
                    }
                }
                if (line.contains("percent")) {
                    System.out.println(line);
                    String[] arrOfStr = line.split(":", 2);
                    line = arrOfStr[1];
                    System.out.println(line);
                    Pattern pattern = Pattern.compile("\\d*\\.\\d*");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        percentage = new BigDecimal(matcher.group());

                        infoList.add(percentage);
                        //return matcher.group();
                    }
                }

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpUtility.disconnect();
        System.out.println(value);
        return infoList;
    }

    /**
     * @return The symbol identifying this stock
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Sets the symbol for this stock
     * @param symbol The symbol identifying this stock
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * @return The price this stock was bought at
     */
    public BigDecimal getBuyPrice() {
        return buyPrice;
    }

    /**
     * Sets the price this stock was bought at
     * @param buyPrice The price this stock was bought at
     */
    public void setBuyPrice(BigDecimal buyPrice) {
        this.buyPrice = buyPrice;
    }
}
