package src.gui.userGUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import src.*;
import src.accounts.Stock;
import src.gui.NumberTextField;

import java.util.Objects;

public class UserStockMainFX extends Application {

    private final Scene userOptionsScene;
    private Scene stockOptionsScene;
    private Scene buyStockOptionsScene;
    private Scene sellStockOptionsScene;
    private Scene viewStockOptionsPane;
    private final User user;

    /**
     *  Assigns the logged in user and previous scene
     * @param userOptionsScene The user options scene
     * @param user The user logged in
     */
    public UserStockMainFX(Scene userOptionsScene, User user) {
        this.user = user;
        this.userOptionsScene = userOptionsScene;
    }

    /**
     * Displays the Stock options scene
     * @param primaryStage The application window
     */
    @Override
    public void start(Stage primaryStage) {
        GridPane stockOptionsPane = stockOptionsPaneSetup(primaryStage);
        this.stockOptionsScene = new Scene(stockOptionsPane, 1024, 768);
        primaryStage.setScene(stockOptionsScene);

        GridPane buyStockOptionsPane = buyStockOptionsPaneSetup(primaryStage);
        this.buyStockOptionsScene =  new Scene(buyStockOptionsPane, 1024, 768);

        GridPane sellStockOptionsPane = sellStockOptionsPaneSetup(primaryStage);
        this.sellStockOptionsScene = new Scene(sellStockOptionsPane, 1024, 768);

        ScrollPane viewStockOptionsPaneSetup = viewStockOptionsPane(primaryStage);
        this.viewStockOptionsPane = new Scene(viewStockOptionsPaneSetup, 1024, 768);
    }

    /**
     * Creates the grid pane for viewing user's options with their stock account.
     * @param primaryStage The application window
     * @return the finished gridpane
     */
    private GridPane stockOptionsPaneSetup(Stage primaryStage) {
        GridPane stockOptionsPane = new GridPane();
        if (!this.user.hasStockAccount()) {
            Label error = new Label("You do not own a stock account. Please request the creation of one.");
            Button backButton = new Button("Back");
            backButton.setOnAction(e -> primaryStage.setScene(userOptionsScene));

            //Setting the padding
            stockOptionsPane.setPadding(new Insets(20, 20, 20, 20));

            //Setting the vertical and horizontal gaps between the columns
            stockOptionsPane.setVgap(5);
            stockOptionsPane.setHgap(5);

            //Setting the Grid alignment
            stockOptionsPane.setAlignment(Pos.CENTER);

            //Arranging all the nodes in the grid
            stockOptionsPane.add(error, 0, 0);
            stockOptionsPane.add(backButton, 0 ,1);
        } else {
            Button buyStock = new Button("Buy Stock");
            buyStock.setOnAction(e -> primaryStage.setScene(this.buyStockOptionsScene));

            Button sellStock = new Button("Sell Stock");
            sellStock.setOnAction(e -> primaryStage.setScene(this.sellStockOptionsScene));

            Button viewStock = new Button("View Portfolio");
            viewStock.setOnAction(e -> primaryStage.setScene(this.viewStockOptionsPane));

            Button backButton = new Button("Back");
            backButton.setOnAction(e -> primaryStage.setScene(userOptionsScene));

            //Setting the padding
            stockOptionsPane.setPadding(new Insets(20, 20, 20, 20));

            //Setting the vertical and horizontal gaps between the columns
            stockOptionsPane.setVgap(5);
            stockOptionsPane.setHgap(5);

            //Setting the Grid alignment
            stockOptionsPane.setAlignment(Pos.CENTER);

            //Arranging all the nodes in the grid
            stockOptionsPane.add(buyStock, 0, 0);
            stockOptionsPane.add(sellStock, 0, 1);
            stockOptionsPane.add(viewStock, 0, 2);
            stockOptionsPane.add(backButton, 0, 3);
        }
        return stockOptionsPane;
    }

    /**
     *  Creates the gridpane for buying stocks.
     * @param primaryStage The application window
     * @return The finished grid pane
     */
    private GridPane buyStockOptionsPaneSetup(Stage primaryStage) {
        GridPane buyStockOptionsPane = new GridPane();

        Label tickerText = new Label("Enter Stock Ticker:");
        TextField symbolField = new TextField();
        Label amountText = new Label("Enter amount of shares:");
        NumberTextField amountField = new NumberTextField();
        Label successMessage = new Label("");

        Button submitButton = new Button("Submit Trade");
        submitButton.setOnAction(e -> {
            boolean success;
            String stringAmount = amountField.getText();
            String symbol = symbolField.getText();
            int amount = 0;
            String possibleException = "";
            try {
                symbol = symbolField.getText();
                amount = Integer.parseInt(amountField.getText());
                this.user.getStockAccount().buyStock(symbol, amount);
                success = true;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                possibleException = exception.getMessage();
                success = false;
            }
            if (success) {
                successMessage.setText("Bought "+amount+" "+symbol+" shares!");
            } else {
                if (possibleException.equals("")) {
                    successMessage.setText("Error buying "+stringAmount+" "+symbol+ " shares!");
                } else {
                    successMessage.setText(possibleException);
                }
                symbolField.clear();
                amountField.clear();
            }
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(this.stockOptionsScene));


        //Setting the padding
        buyStockOptionsPane.setPadding(new Insets(20, 20, 20, 20));

        //Setting the vertical and horizontal gaps between the columns
        buyStockOptionsPane.setVgap(5);
        buyStockOptionsPane.setHgap(5);

        //Setting the Grid alignment
        buyStockOptionsPane.setAlignment(Pos.CENTER);

        //Arranging all the nodes in the grid
        buyStockOptionsPane.add(tickerText, 0, 0);
        buyStockOptionsPane.add(symbolField, 1, 0);
        buyStockOptionsPane.add(amountText, 0, 1);
        buyStockOptionsPane.add(amountField, 1, 1);
        buyStockOptionsPane.add(successMessage, 0, 2);
        buyStockOptionsPane.add(submitButton, 0, 3);
        buyStockOptionsPane.add(backButton, 1, 3);

        return buyStockOptionsPane;
    }

    /**
     *  Creates the gridpane for selling stocks.
     * @param primaryStage The application window
     * @return The finished grid pane
     */
    private GridPane sellStockOptionsPaneSetup(Stage primaryStage) {
        GridPane sellStockOptionsPane = new GridPane();

        Label tickerText = new Label("Enter Stock Ticker:");
        TextField symbolField = new TextField();
        Label amountText = new Label("Enter amount of shares:");
        NumberTextField amountField = new NumberTextField();
        Label successMessage = new Label("");
        Button submitButton = new Button("Submit trade");
        Button backButton = new Button("Back");

        //Setting the padding
        sellStockOptionsPane.setPadding(new Insets(20, 20, 20, 20));

        //Setting the vertical and horizontal gaps between the columns
        sellStockOptionsPane.setVgap(5);
        sellStockOptionsPane.setHgap(5);

        //Setting the Grid alignment
        sellStockOptionsPane.setAlignment(Pos.CENTER);

        //Arranging all the nodes in the grid
        sellStockOptionsPane.add(tickerText, 0, 0);
        sellStockOptionsPane.add(symbolField, 1, 0);
        sellStockOptionsPane.add(amountText, 0, 1);
        sellStockOptionsPane.add(amountField, 1, 1);
        sellStockOptionsPane.add(successMessage, 0, 2);
        sellStockOptionsPane.add(submitButton, 0, 3);
        sellStockOptionsPane.add(backButton, 1, 3);

        backButton.setOnAction(e -> primaryStage.setScene(stockOptionsScene));
        submitButton.setOnAction(event -> {
            boolean success;
            String stringAmount = amountField.getText();
            String symbol = symbolField.getText();
            int amount = 0;
            String possibleException = "";
            try {
                symbol = symbolField.getText();
                amount = Integer.parseInt(amountField.getText());
                this.user.getStockAccount().sellStock(symbol, amount);
                success = true;
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                possibleException = exception.getMessage();
                success = false;
            }
            if (success) {
                successMessage.setText("Sold "+amount+" "+symbol+" shares!");
            } else {
                if (Objects.equals(possibleException, "")) {
                    successMessage.setText("Error selling "+stringAmount+" "+symbol+" shares!");
                } else {
                    successMessage.setText(possibleException);
                }
                symbolField.clear();
                amountField.clear();
            }
        });

        return sellStockOptionsPane;
    }

    /**
     *  Creates the gridpane for viewing the all the stocks held.
     * @param primaryStage The application window
     * @return The finished grid pane
     */
    private ScrollPane viewStockOptionsPane(Stage primaryStage) {
        GridPane viewStockOptionsPane = new GridPane();
        Label firstColumnHeader = new Label("Symbol");
        Label secondColumnHeader = new Label("Buy Price");
        Label thirdColumnHeader = new Label("Current Price");
        Label fourthColumnHeader = new Label("Amount");
        Label fifthColumnHeader = new Label("Percentage");
        firstColumnHeader.setStyle("-fx-font-weight: bold");
        Button backButton = new Button("Go Back");
        backButton.setOnAction(e -> primaryStage.setScene(this.userOptionsScene));
        viewStockOptionsPane.add(firstColumnHeader, 0, 0);
        viewStockOptionsPane.add(secondColumnHeader, 1, 0);
        viewStockOptionsPane.add(thirdColumnHeader, 2, 0);
        viewStockOptionsPane.add(fourthColumnHeader, 3, 0);
        viewStockOptionsPane.add(fifthColumnHeader, 4, 0);


        int i = 1;
        for (Stock stock: this.user.getStockAccount().getStocks()) {
            viewStockOptionsPane.add(new Label(stock.getSymbol()), 0, i);
            viewStockOptionsPane.add(new Label(stock.getBuyPrice().toString()), 1, i);
            viewStockOptionsPane.add(new Label(stock.currentPrice.toString()), 2, i);
            viewStockOptionsPane.add(new Label(Integer.toString(stock.amount)), 3, i);
            viewStockOptionsPane.add(new Label(stock.percentage.toString()), 4, i);
            i++;
        }
        viewStockOptionsPane.add(backButton, 0, i+1);

        backButton.setOnAction(e -> primaryStage.setScene(stockOptionsScene));

        //Setting the padding
        viewStockOptionsPane.setPadding(new Insets(20, 20, 20, 20));

        //Setting the vertical and horizontal gaps between the columns
        viewStockOptionsPane.setVgap(5);
        viewStockOptionsPane.setHgap(5);


        //Setting the Grid alignment
        viewStockOptionsPane.setAlignment(Pos.CENTER);
        ScrollPane scrollPane = new ScrollPane();
        //Setting the Grid alignment

        this.viewStockOptionsPane = new Scene(viewStockOptionsPane, 1024, 768);
        scrollPane.setContent(viewStockOptionsPane);
        return scrollPane;
    }
}
