package dao;

import model.Account;
import model.ChequeAccount;
import model.InvestmentAccount;
import model.SavingsAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class H2AccountDAO implements AccountDAO {

    @Override
    public Account create(Account account) throws Exception {
        // Use H2 MERGE statement to perform atomic upsert by id
        String sql = "MERGE INTO accounts (id, owner_id, balance, type, employer) KEY(id) VALUES (?,?,?,?,?)";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, account.getAccountId());
            ps.setInt(2, account.getOwner().getCustomerId());
            ps.setDouble(3, account.getBalance());
            ps.setString(4, account.getClass().getSimpleName());
            String emp = null;
            if (account instanceof ChequeAccount) emp = ((ChequeAccount) account).getEmployerName();
            ps.setString(5, emp);
            try {
                ps.executeUpdate();
                return account;
            } catch (java.sql.SQLException ex) {
                // fallback to update if merge/insert fails (some H2 versions may throw on duplicate)
                String upd = "UPDATE accounts SET owner_id=?, balance=?, type=?, employer=? WHERE id=?";
                try (PreparedStatement ups = c.prepareStatement(upd)) {
                    ups.setInt(1, account.getOwner().getCustomerId());
                    ups.setDouble(2, account.getBalance());
                    ups.setString(3, account.getClass().getSimpleName());
                    ups.setString(4, emp);
                    ups.setInt(5, account.getAccountId());
                    ups.executeUpdate();
                    return account;
                }
            }
        }
    }

    @Override
    public Account findById(int id) throws Exception {
        String sql = "SELECT id,owner_id,balance,type,employer FROM accounts WHERE id=?";
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int accId = rs.getInt("id");
                    int ownerId = rs.getInt("owner_id");
                    double balance = rs.getDouble("balance");
                    String type = rs.getString("type");
                    String employer = rs.getString("employer");
                    // load owner from customers table
                    model.Customer owner = DaoFactory.getCustomerDAO().findById(ownerId);
                    if (owner == null) {
                        owner = new model.Customer(ownerId, "", "", "");
                    }
                    switch (type) {
                        case "SavingsAccount":
                            return new SavingsAccount(accId, owner, balance);
                        case "ChequeAccount":
                            return new ChequeAccount(accId, owner, balance, employer);
                        case "InvestmentAccount":
                            return new InvestmentAccount(accId, owner, balance);
                        default:
                            return null;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<Account> findAll() throws Exception {
        String sql = "SELECT id,owner_id,balance,type,employer FROM accounts";
        List<Account> list = new ArrayList<>();
        try (Connection c = Database.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int accId = rs.getInt("id");
                int ownerId = rs.getInt("owner_id");
                double balance = rs.getDouble("balance");
                String type = rs.getString("type");
                String employer = rs.getString("employer");
                model.Customer owner = DaoFactory.getCustomerDAO().findById(ownerId);
                if (owner == null) {
                    owner = new model.Customer(ownerId, "", "", "");
                }
                Account acc = null;
                switch (type) {
                    case "SavingsAccount":
                        acc = new SavingsAccount(accId, owner, balance);
                        break;
                    case "ChequeAccount":
                        acc = new ChequeAccount(accId, owner, balance, employer);
                        break;
                    case "InvestmentAccount":
                        acc = new InvestmentAccount(accId, owner, balance);
                        break;
                }
                if (acc != null) list.add(acc);
            }
        }
        return list;
    }
}
