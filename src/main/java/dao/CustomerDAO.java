package dao;

import model.Customer;
import util.PasswordUtil;
import java.sql.*;
import java.util.*;

public class CustomerDAO {

    // === Create or migrate table ===
    public static void createTable() throws SQLException {
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement()) {

            s.execute("""
    CREATE TABLE IF NOT EXISTS customers (
        id IDENTITY PRIMARY KEY,
        full_name VARCHAR(100),
        username VARCHAR(100) UNIQUE,
        password_hash VARCHAR(255),
        email VARCHAR(100),
        role VARCHAR(20) DEFAULT 'CUSTOMER',
        status VARCHAR(20) DEFAULT 'PENDING'
    )
""");

        }
    }

    // === Register new customer or staff ===
    public static void register(String fullName, String username, String password,
                                String email, String role) throws SQLException {

        String status = role.equalsIgnoreCase("STAFF") ? "APPROVED" : "PENDING";
        String sql = "INSERT INTO customers (full_name, username, password_hash, email, role, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, username);
            ps.setString(3, PasswordUtil.hashPassword(password));
            ps.setString(4, email);
            ps.setString(5, role);
            ps.setString(6, status);
            ps.executeUpdate();
        }
    }

    // === Find by username ===
    public static Customer findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM customers WHERE username = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToCustomer(rs);
            }
        }
        return null;
    }

    // === Login ===
    public static Customer login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM customers WHERE username = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (PasswordUtil.checkPassword(password, rs.getString("password_hash"))) {
                        return mapRowToCustomer(rs);
                    }
                }
            }
        }
        return null;
    }

    // === Pending ===
    public static List<Customer> allPending() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE status = 'PENDING' ORDER BY id";
        try (Connection c = DBConnection.getConnection();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            while (rs.next()) list.add(mapRowToCustomer(rs));
        }
        return list;
    }

    // === Approve ===
    public static void approveCustomer(long id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE customers SET status = 'APPROVED' WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // === Reject ===
    public static void rejectCustomer(long id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE customers SET status = 'REJECTED' WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // === Stats ===
    public static Map<String, Integer> stats() throws SQLException {
        String sql = """
            SELECT
              SUM(CASE WHEN status='PENDING' THEN 1 ELSE 0 END) AS pending,
              SUM(CASE WHEN status='APPROVED' THEN 1 ELSE 0 END) AS approved,
              SUM(CASE WHEN status='REJECTED' THEN 1 ELSE 0 END) AS rejected
            FROM customers
        """;
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            Map<String, Integer> map = new HashMap<>();
            if (rs.next()) {
                map.put("PENDING", rs.getInt("pending"));
                map.put("APPROVED", rs.getInt("approved"));
                map.put("REJECTED", rs.getInt("rejected"));
            }
            return map;
        }
    }

    private static Customer mapRowToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getLong("id"),
                rs.getString("full_name"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("email"),
                "APPROVED".equalsIgnoreCase(rs.getString("status"))
        );
    }
}
