package dao;

import model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class H2CustomerDAO implements CustomerDAO {

    @Override
    public Customer create(Customer customer) throws Exception {
        String sql = "INSERT INTO customers(name,address,phone) VALUES(?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getAddress());
            ps.setString(3, customer.getPhone());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new Customer(id, customer.getName(), customer.getAddress(), customer.getPhone());
                }
            }
        }
        throw new RuntimeException("Failed to create customer");
    }

    @Override
    public Customer findById(int id) throws Exception {
        String sql = "SELECT id,name,address,phone FROM customers WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(rs.getInt("id"), rs.getString("name"), rs.getString("address"), rs.getString("phone"));
                }
            }
        }
        return null;
    }

    @Override
    public List<Customer> findAll() throws Exception {
        String sql = "SELECT id,name,address,phone FROM customers";
        List<Customer> list = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Customer(rs.getInt("id"), rs.getString("name"), rs.getString("address"), rs.getString("phone")));
            }
        }
        return list;
    }
}
