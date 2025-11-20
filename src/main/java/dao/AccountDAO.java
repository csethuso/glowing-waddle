package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    // Create a new account record
    public void createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (customer_id, account_number, account_type, branch, balance, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, account.getCustomerId());
            ps.setString(2, account.getAccountNumber());
            ps.setString(3, account.getAccountType());
            ps.setString(4, account.getBranch());
            ps.setDouble(5, account.getBalance());
            String status = account.getStatus() == null ? "PENDING" : account.getStatus();
            ps.setString(6, status);
            ps.executeUpdate();
        }
    }

    // Find all accounts for a specific customer
    public List<Account> findAccountsByCustomer(long customerId) throws SQLException {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAccount(rs));
                }
            }
        }
        return list;
    }

    // Find single account by account number (for transfers)
    public Account findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAccount(rs);
                }
            }
        }
        return null; // not found
    }

    // Update account balance
    public boolean updateBalance(String accountNumber, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, newBalance);
            ps.setString(2, accountNumber);
            return ps.executeUpdate() == 1;
        }
    }

    // Helper to map result set to Account object
    private Account mapRowToAccount(ResultSet rs) throws SQLException {
        String type = rs.getString("account_type");
        long id = rs.getLong("id");
        long cid = rs.getLong("customer_id");
        String accNo = rs.getString("account_number");
        double bal = rs.getDouble("balance");
        String branch = rs.getString("branch");
        String status = rs.getString("status");

        Account acc;
        switch (type == null ? "" : type.toUpperCase()) {
            case "SAVINGS":
                acc = new SavingsAccount(cid, accNo, bal, branch);
                break;
            case "INVESTMENT":
                acc = new InvestmentAccount(cid, accNo, bal, branch);
                break;
            case "CHEQUE":
                acc = new ChequeAccount(cid, accNo, bal, branch);
                break;
            default:
                throw new SQLException("Unknown account type: " + type);
        }
        acc.setId(id);
        if (status != null) acc.setStatus(status);
        return acc;
    }

    // Deposit amount into account atomically and record transaction
    public void deposit(String accountNumber, double amount) throws SQLException {
        String selectSql = "SELECT id, balance FROM accounts WHERE account_number = ?";
        String updateSql = "UPDATE accounts SET balance = ? WHERE id = ?";
        String insertTx = "INSERT INTO transactions (account_id, type, amount, details) VALUES (?, 'DEPOSIT', ?, ?)";

        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(selectSql)) {
                ps.setString(1, accountNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        c.rollback();
                        throw new SQLException("Account not found: " + accountNumber);
                    }
                    long id = rs.getLong("id");
                    double bal = rs.getDouble("balance");
                    double newBal = bal + amount;

                    try (PreparedStatement ups = c.prepareStatement(updateSql)) {
                        ups.setDouble(1, newBal);
                        ups.setLong(2, id);
                        ups.executeUpdate();
                    }

                    try (PreparedStatement txs = c.prepareStatement(insertTx)) {
                        txs.setLong(1, id);
                        txs.setDouble(2, amount);
                        txs.setString(3, "Deposit via UI");
                        txs.executeUpdate();
                    }
                    c.commit();
                }
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    // Withdraw amount (checks balance) and record transaction
    public void withdraw(String accountNumber, double amount) throws SQLException {
        String selectSql = "SELECT id, balance FROM accounts WHERE account_number = ?";
        String updateSql = "UPDATE accounts SET balance = ? WHERE id = ?";
        String insertTx = "INSERT INTO transactions (account_id, type, amount, details) VALUES (?, 'WITHDRAWAL', ?, ?)";

        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(selectSql)) {
                ps.setString(1, accountNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        c.rollback();
                        throw new SQLException("Account not found: " + accountNumber);
                    }
                    long id = rs.getLong("id");
                    double bal = rs.getDouble("balance");
                    if (bal < amount) {
                        c.rollback();
                        throw new SQLException("Insufficient funds");
                    }
                    double newBal = bal - amount;

                    try (PreparedStatement ups = c.prepareStatement(updateSql)) {
                        ups.setDouble(1, newBal);
                        ups.setLong(2, id);
                        ups.executeUpdate();
                    }

                    try (PreparedStatement txs = c.prepareStatement(insertTx)) {
                        txs.setLong(1, id);
                        txs.setDouble(2, amount);
                        txs.setString(3, "Withdrawal via UI");
                        txs.executeUpdate();
                    }
                    c.commit();
                }
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    // Add funds to an investment (treated like a deposit but kept as a helper)
    public void createInvestment(String accountNumber, double amount) throws SQLException {
        // For now, treat investment as a deposit into the investment account and record a deposit transaction
        deposit(accountNumber, amount);
    }
}
