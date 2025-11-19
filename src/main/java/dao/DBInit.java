package dao;

import java.sql.Connection;
import java.sql.Statement;

public class DBInit {
    public static void init() {
        try (Connection c = Database.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS customers (id INTEGER AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), address VARCHAR(255), phone VARCHAR(100))");
            s.execute("CREATE TABLE IF NOT EXISTS accounts (id INTEGER PRIMARY KEY, owner_id INTEGER, balance DOUBLE, type VARCHAR(100), employer VARCHAR(255))");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize DB", e);
        }
    }
}
