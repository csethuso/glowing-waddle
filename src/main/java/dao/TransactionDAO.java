package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Transaction;
import model.Account;

public class TransactionDAO {

    // Record a new transaction for a given account
    public void recordTransaction(long accountId, String type,
                                  double amount, String details) throws SQLException {
        String sql = "INSERT INTO transactions (account_id, type, amount, details) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, accountId);
            ps.setString(2, type);
            ps.setDouble(3, amount);
            ps.setString(4, details);
            ps.executeUpdate();
        }
    }

    // Retrieve all transactions for a given account
    public List<String> getTransactions(long accountId) throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT type, amount, timestamp, details FROM transactions WHERE account_id = ? ORDER BY timestamp DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String entry = String.format("%s %.2f on %s (%s)",
                            rs.getString("type"),
                            rs.getDouble("amount"),
                            rs.getTimestamp("timestamp"),
                            rs.getString("details"));
                    list.add(entry);
                }
            }
        }
        return list;
    }

    // Retrieve all transactions for a given customer (joins accounts -> transactions)
    public List<Transaction> findTransactionsByCustomer(long customerId) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.id, t.account_id, t.type, t.amount, t.timestamp, t.details "
                   + "FROM transactions t "
                   + "JOIN accounts a ON t.account_id = a.id "
                   + "WHERE a.customer_id = ? "
                   + "ORDER BY t.timestamp DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = new Transaction(
                        rs.getInt("id"),
                        rs.getInt("account_id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getString("details"),
                        rs.getTimestamp("timestamp").toLocalDateTime()
                    );
                    list.add(transaction);
                }
            }
        }
        return list;
    }

    // Retrieve transactions for an account identified by account number
    public List<Transaction> getTransactionsForAccount(String accountNumber) throws SQLException {
        String findSql = "SELECT id FROM accounts WHERE account_number = ?";
        String sql = "SELECT t.id, t.account_id, t.type, t.amount, t.timestamp, t.details "
                   + "FROM transactions t WHERE t.account_id = ? ORDER BY t.timestamp DESC";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement findPs = c.prepareStatement(findSql)) {
            findPs.setString(1, accountNumber);
            try (ResultSet rsFind = findPs.executeQuery()) {
                if (!rsFind.next()) {
                    return new ArrayList<>();
                }
                long accountId = rsFind.getLong("id");
                List<Transaction> list = new ArrayList<>();
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setLong(1, accountId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Transaction transaction = new Transaction(
                                rs.getInt("id"),
                                rs.getInt("account_id"),
                                rs.getString("type"),
                                rs.getDouble("amount"),
                                rs.getString("details"),
                                rs.getTimestamp("timestamp").toLocalDateTime()
                            );
                            list.add(transaction);
                        }
                    }
                }
                return list;
            }
        }
    }
}
