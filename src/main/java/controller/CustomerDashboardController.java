package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import dao.AccountDAO;
import dao.TransactionDAO;
import model.*;
import util.AlertUtil;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

public class CustomerDashboardController {

    @FXML private Label nameLabel;
    @FXML private TextArea accountDisplay;
    @FXML private TextField depositAmount;
    @FXML private TextField withdrawAmount;
    @FXML private TextField transferToField;
    @FXML private TextField transferAmountField;

    // newly declared/mapped FXML controls
    @FXML private javafx.scene.control.ComboBox<String> applyChoice;
    @FXML private javafx.scene.control.ComboBox<String> depositAccountChoice;
    @FXML private javafx.scene.control.ComboBox<String> withdrawAccountChoice;
    @FXML private javafx.scene.control.ComboBox<String> transferAccountChoice;
    @FXML private javafx.scene.control.ComboBox<String> investmentAccountChoice;
    @FXML private TextField investmentAmountField;

    @FXML private TableView<model.Transaction> transactionsTable;
    @FXML private TableColumn<model.Transaction, java.time.LocalDateTime> colDate;
    @FXML private TableColumn<model.Transaction, String> colType;
    @FXML private TableColumn<model.Transaction, Double> colAmount;
    @FXML private TableColumn<model.Transaction, Double> colBalance;
    @FXML private TableColumn<model.Transaction, String> colDesc;

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private Customer currentCustomer;

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
        nameLabel.setText("Welcome, " + customer.getFullName() + "!");
        refreshAccounts();
    }

    public void initialize() {
        // wire table columns (adjust property names to your TransactionRow/DTO)
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        // Transaction model doesn't carry balance after each tx; reuse amount column for balance column for now
        colBalance.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Populate account-apply choices
        applyChoice.setItems(FXCollections.observableArrayList("SAVINGS", "INVESTMENT", "CHEQUE"));

        // Ensure transactions table is empty at start
        transactionsTable.setItems(FXCollections.observableArrayList());
    }

    private void refreshAccounts() {
        if (currentCustomer == null) return;
        try {
            List<Account> accounts = accountDAO.findAccountsByCustomer(currentCustomer.getId());
            depositAccountChoice.getItems().clear();
            withdrawAccountChoice.getItems().clear();
            transferAccountChoice.getItems().clear();
            investmentAccountChoice.getItems().clear();

            StringBuilder sb = new StringBuilder("Your Accounts:\n\n");
            for (Account acc : accounts) {
                sb.append(String.format("%s | %s | %.2f | %s\n",
                        acc.getAccountType(), acc.getAccountNumber(), acc.getBalance(), acc.getStatus()));
                if ("ACTIVE".equalsIgnoreCase(acc.getStatus())) {
                    depositAccountChoice.getItems().add(acc.getAccountNumber());
                    withdrawAccountChoice.getItems().add(acc.getAccountNumber());
                    transferAccountChoice.getItems().add(acc.getAccountNumber());
                    investmentAccountChoice.getItems().add(acc.getAccountNumber());
                }
            }
            // If any choice boxes have items, select the first item so dropdown shows a value
            if (!depositAccountChoice.getItems().isEmpty()) depositAccountChoice.setValue(depositAccountChoice.getItems().get(0));
            if (!withdrawAccountChoice.getItems().isEmpty()) withdrawAccountChoice.setValue(withdrawAccountChoice.getItems().get(0));
            if (!transferAccountChoice.getItems().isEmpty()) transferAccountChoice.setValue(transferAccountChoice.getItems().get(0));
            if (!investmentAccountChoice.getItems().isEmpty()) investmentAccountChoice.setValue(investmentAccountChoice.getItems().get(0));
            accountDisplay.setText(sb.toString());
        } catch (SQLException e) {
            AlertUtil.error("Failed to load accounts: " + e.getMessage());
        }
    }

    @FXML
    private void onApplyAccount(ActionEvent event) {
        try {
            String type = applyChoice.getValue();
            if (type == null) {
                AlertUtil.error("Select an account type to apply for.");
                return;
            }
            String accNum = type.substring(0, Math.min(2, type.length())) + "-" + System.currentTimeMillis();
            Account account;
            switch (type) {
                case "INVESTMENT" -> account = new InvestmentAccount(currentCustomer.getId(), accNum, 0, "Main Branch");
                case "CHEQUE" -> account = new ChequeAccount(currentCustomer.getId(), accNum, 0, "Main Branch");
                default -> account = new SavingsAccount(currentCustomer.getId(), accNum, 0, "Main Branch");
            }
            account.setStatus("PENDING");
            accountDAO.createAccount(account);
            AlertUtil.info("Account application submitted for approval.");
            refreshAccounts();
        } catch (Exception e) {
            AlertUtil.error("Application failed: " + e.getMessage());
        }
    }

    @FXML
    private void onDeposit(ActionEvent ev) {
        String acct = depositAccountChoice.getValue();
        String amountTxt = depositAmount.getText();
        if (acct == null || acct.isBlank()) { showError("Select an account."); return; }
        double amt;
        try { amt = Double.parseDouble(amountTxt); if (amt <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { showError("Enter a valid positive amount."); return; }

        try {
            accountDAO.deposit(acct, amt);
            showInfo("Deposit completed.");
            refreshAccounts();
        } catch (SQLIntegrityConstraintViolationException ie) {
            showError("Database referential integrity error when depositing. Ensure account exists.");
        } catch (SQLException sq) {
            showError("Deposit failed: " + sq.getMessage());
        }
    }

    @FXML
    private void onWithdraw(ActionEvent ev) {
        String acct = withdrawAccountChoice.getValue();
        String amountTxt = withdrawAmount.getText();
        if (acct == null || acct.isBlank()) { showError("Select an account."); return; }
        double amt;
        try { amt = Double.parseDouble(amountTxt); if (amt <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { showError("Enter a valid positive amount."); return; }

        try {
            accountDAO.withdraw(acct, amt);
            showInfo("Withdrawal completed.");
            refreshAccounts();
        } catch (SQLIntegrityConstraintViolationException ie) {
            showError("Database referential integrity error when withdrawing. Check account and constraints.");
        } catch (SQLException sq) {
            showError("Withdrawal failed: " + sq.getMessage());
        }
    }

    @FXML
    private void onInvest(ActionEvent ev) {
        String acct = investmentAccountChoice.getValue();
        String amountTxt = investmentAmountField.getText();
        if (acct == null || acct.isBlank()) { showError("Select an account for the investment."); return; }
        double amt;
        try { amt = Double.parseDouble(amountTxt); if (amt <= 0) throw new NumberFormatException(); }
        catch (NumberFormatException e) { showError("Enter a valid positive amount."); return; }

        try {
            accountDAO.createInvestment(acct, amt);
            showInfo("Investment created.");
            refreshAccounts();
        } catch (SQLIntegrityConstraintViolationException ie) {
            showError("Referential integrity error when creating investment. Ensure account exists.");
        } catch (SQLException sq) {
            showError("Investment failed: " + sq.getMessage());
        }
    }

    @FXML
    private void onTransfer(ActionEvent event) {
        try {
            String fromAccNo = transferAccountChoice.getValue();
            String toAccNo = transferToField.getText().trim();
            double amount = Double.parseDouble(transferAmountField.getText());
            if (fromAccNo == null || toAccNo.isEmpty() || amount <= 0) {
                AlertUtil.error("Please select accounts and enter valid amount.");
                return;
            }

            Account from = accountDAO.findByAccountNumber(fromAccNo);
            Account to = accountDAO.findByAccountNumber(toAccNo);

            if (to == null) {
                AlertUtil.error("Target account not found.");
                return;
            }

            if (from.getBalance() < amount) {
                AlertUtil.error("Insufficient balance for transfer.");
                return;
            }

            from.deposit(-amount);
            to.deposit(amount);
            accountDAO.updateBalance(fromAccNo, from.getBalance());
            accountDAO.updateBalance(toAccNo, to.getBalance());
            transactionDAO.recordTransaction(from.getId(), "TRANSFER_OUT", amount, "Sent to " + toAccNo);
            transactionDAO.recordTransaction(to.getId(), "TRANSFER_IN", amount, "Received from " + fromAccNo);

            AlertUtil.info("Transfer successful.");
            refreshAccounts();
        } catch (Exception e) {
            AlertUtil.error("Transfer failed: " + e.getMessage());
        }
    }

    @FXML
    private void onViewTransactions(ActionEvent ev) {
        String acct = depositAccountChoice.getValue(); // or let user pick an account to view
        if (acct == null) { showError("Select an account to view transactions."); return; }
        try {
            List<model.Transaction> list = transactionDAO.getTransactionsForAccount(acct);
            ObservableList<model.Transaction> rows = FXCollections.observableArrayList(list);
            transactionsTable.setItems(rows);
        } catch (SQLException ex) {
            showError("Failed to load transactions: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg);
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.showAndWait();
    }

    @FXML
    private void onLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/LoginView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Banking System - Login");
            stage.show();
        } catch (Exception e) {
            AlertUtil.error("Logout failed: " + e.getMessage());
        }
    }
}
