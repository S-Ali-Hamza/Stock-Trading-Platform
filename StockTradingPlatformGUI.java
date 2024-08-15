package StockTradingPlatform;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;

public class StockTradingPlatformGUI {

    static class Stock {
        String symbol;
        String name;
        double price;

        Stock(String symbol, String name, double price) {
            this.symbol = symbol;
            this.name = name;
            this.price = price;
        }
    }

    static class Portfolio {
        Map<String, Integer> holdings = new HashMap<>();
        List<String> transactions = new ArrayList<>();

        void buyStock(String symbol, int quantity, Map<String, Stock> marketData) {
            Stock stock = marketData.get(symbol);
            if (stock == null) {
                JOptionPane.showMessageDialog(null, "Error: Stock symbol '" + symbol + "' not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(null, "Error: Quantity must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            holdings.put(symbol, holdings.getOrDefault(symbol, 0) + quantity);
            String transaction = "Bought " + quantity + " shares of " + stock.name + " (" + symbol + ") at $" + stock.price + " each on " + getCurrentTime();
            transactions.add(transaction);
            JOptionPane.showMessageDialog(null, "Successfully bought " + quantity + " shares of " + symbol + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
        }

        void sellStock(String symbol, int quantity, Map<String, Stock> marketData) {
            if (!holdings.containsKey(symbol)) {
                JOptionPane.showMessageDialog(null, "Error: Stock '" + symbol + "' not found in portfolio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int currentQuantity = holdings.get(symbol);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(null, "Error: Quantity must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (currentQuantity < quantity) {
                JOptionPane.showMessageDialog(null, "Error: Insufficient shares of '" + symbol + "' to sell. Current quantity: " + currentQuantity, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            holdings.put(symbol, currentQuantity - quantity);
            if (holdings.get(symbol) == 0) {
                holdings.remove(symbol);
            }
            Stock stock = marketData.get(symbol);
            String transaction = "Sold " + quantity + " shares of " + stock.name + " (" + symbol + ") at $" + stock.price + " each on " + getCurrentTime();
            transactions.add(transaction);
            JOptionPane.showMessageDialog(null, "Successfully sold " + quantity + " shares of " + symbol + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
        }

        double getValue(Map<String, Stock> marketData) {
            double totalValue = 0;
            for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
                Stock stock = marketData.get(entry.getKey());
                if (stock != null) {
                    totalValue += stock.price * entry.getValue();
                }
            }
            return totalValue;
        }

        String[] getHoldings() {
            if (holdings.isEmpty()) {
                return new String[]{"Portfolio is empty. No stocks to display."};
            }
            String[] holdingsArray = new String[holdings.size()];
            int index = 0;
            for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
                holdingsArray[index++] = entry.getKey() + ": " + entry.getValue() + " shares";
            }
            return holdingsArray;
        }

        String[] getTransactions() {
            if (transactions.isEmpty()) {
                return new String[]{"No transactions to display."};
            }
            return transactions.toArray(new String[0]);
        }

        private String getCurrentTime() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(new Date());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Stock Trading Platform");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLayout(new BorderLayout());

            // Create market data
            Map<String, Stock> marketData = new HashMap<>();
            for (int i = 1; i <= 100; i++) {
                marketData.put("STK" + i, new Stock("STK" + i, "Stock " + i + " Name", Math.round(Math.random() * 1000) / 10.0));
            }

            Portfolio portfolio = new Portfolio();

            // Market Data Panel
            JPanel marketPanel = new JPanel();
            marketPanel.setLayout(new BorderLayout());
            JTable marketTable = new JTable();
            String[] columnNames = {"Symbol", "Name", "Price"};
            DefaultTableModel marketTableModel = new DefaultTableModel(columnNames, 0);
            marketTable.setModel(marketTableModel);
            marketTable.setEnabled(false);

            // Refresh market data to display all stocks
            refreshMarketData(marketTableModel, marketData);

            JScrollPane marketScrollPane = new JScrollPane(marketTable);
            marketPanel.add(new JLabel("Market Data", JLabel.CENTER), BorderLayout.NORTH);
            marketPanel.add(marketScrollPane, BorderLayout.CENTER);

            // Input Panel
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;

            JLabel symbolLabel = new JLabel("Stock Symbol:");
            JTextField symbolField = new JTextField(10);
            JLabel quantityLabel = new JLabel("Quantity:");
            JTextField quantityField = new JTextField(10);
            JButton buyButton = new JButton("Buy Stock");
            JButton sellButton = new JButton("Sell Stock");

            gbc.gridx = 0;
            gbc.gridy = 0;
            inputPanel.add(symbolLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            inputPanel.add(symbolField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            inputPanel.add(quantityLabel, gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            inputPanel.add(quantityField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            inputPanel.add(sellButton, gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            inputPanel.add(buyButton, gbc);

            // Output Panel
            JPanel outputPanel = new JPanel();
            outputPanel.setLayout(new BorderLayout());

            JTextArea outputArea = new JTextArea();
            outputArea.setEditable(false);
            JScrollPane outputScrollPane = new JScrollPane(outputArea);
            outputPanel.add(new JLabel("Portfolio Performance", JLabel.CENTER), BorderLayout.NORTH);
            outputPanel.add(outputScrollPane, BorderLayout.CENTER);

            JButton valueButton = new JButton("Check Portfolio Value");
            JButton holdingsButton = new JButton("View Holdings");
            JButton transactionsButton = new JButton("View Transactions");

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(valueButton);
            buttonPanel.add(holdingsButton);
            buttonPanel.add(transactionsButton);

            outputPanel.add(buttonPanel, BorderLayout.SOUTH);

            // Search Panel
            JPanel searchPanel = new JPanel();
            searchPanel.setLayout(new BorderLayout());
            JTextField searchField = new JTextField(10);
            JButton searchButton = new JButton("Search");

            searchPanel.add(new JLabel("Search Stock: "), BorderLayout.WEST);
            searchPanel.add(searchField, BorderLayout.CENTER);
            searchPanel.add(searchButton, BorderLayout.EAST);

            frame.add(searchPanel, BorderLayout.NORTH);
            frame.add(marketPanel, BorderLayout.WEST);
            frame.add(inputPanel, BorderLayout.CENTER);
            frame.add(outputPanel, BorderLayout.EAST);

            // Action Listeners
            buyButton.addActionListener(e -> {
                String symbol = symbolField.getText().toUpperCase();
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Error: Quantity must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                portfolio.buyStock(symbol, quantity, marketData);
                refreshMarketData(marketTableModel, marketData);
                symbolField.setText("");
                quantityField.setText("");
            });

            sellButton.addActionListener(e -> {
                String symbol = symbolField.getText().toUpperCase();
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Error: Quantity must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                portfolio.sellStock(symbol, quantity, marketData);
                refreshMarketData(marketTableModel, marketData);
                symbolField.setText("");
                quantityField.setText("");
            });

            valueButton.addActionListener(e -> {
                double value = portfolio.getValue(marketData);
                outputArea.setText(String.format("Current portfolio value: $%.2f", value));
            });

            holdingsButton.addActionListener(e -> {
                String[] holdings = portfolio.getHoldings();
                StringBuilder sb = new StringBuilder();
                for (String holding : holdings) {
                    sb.append(holding).append("\n");
                }
                outputArea.setText(sb.toString());
            });

            transactionsButton.addActionListener(e -> {
                String[] transactions = portfolio.getTransactions();
                StringBuilder sb = new StringBuilder();
                for (String transaction : transactions) {
                    sb.append(transaction).append("\n");
                }
                outputArea.setText(sb.toString());
            });

            searchButton.addActionListener(e -> {
                String query = searchField.getText().toUpperCase();
                Stock stock = marketData.get(query);
                if (stock != null) {
                    JOptionPane.showMessageDialog(frame, "Stock found:\nSymbol: " + stock.symbol + "\nName: " + stock.name + "\nPrice: $" + stock.price, "Search Result", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "No stock found with symbol '" + query + "'", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            frame.setVisible(true);
        });
    }

    private static void refreshMarketData(DefaultTableModel model, Map<String, Stock> marketData) {
        model.setRowCount(0);
        for (Stock stock : marketData.values()) {
            model.addRow(new Object[]{stock.symbol, stock.name, stock.price});
        }
    }
}
