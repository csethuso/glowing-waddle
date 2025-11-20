package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import model.Customer;
import model.Transaction;
import dao.TransactionDAO;
import util.AlertUtil;

import java.sql.SQLException;
import java.util.List;

public class TransactionHistoryController {

    @FXML private Label customerNameLabel;
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, Double> amountColumn;
    @FXML private TableColumn<Transaction, String> descriptionColumn;
    @FXML private TableColumn<Transaction, String> dateColumn;

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private Customer currentCustomer;

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        customerNameLabel.setText("Transactions for " + customer.getFullName());
        loadTransactions();
    }

    private void loadTransactions() {
        try {
            List<Transaction> transactions = transactionDAO.findTransactionsByCustomer(currentCustomer.getId());
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            transactionTable.getItems().setAll(transactions);
        } catch (SQLException e) {
            AlertUtil.error("Failed to load transactions: " + e.getMessage());
        }
    }
}
