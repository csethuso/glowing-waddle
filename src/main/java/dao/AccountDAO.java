package dao;

import model.Account;
import java.util.List;

public interface AccountDAO {
    Account create(Account account) throws Exception;
    Account findById(int id) throws Exception;
    List<Account> findAll() throws Exception;
}
