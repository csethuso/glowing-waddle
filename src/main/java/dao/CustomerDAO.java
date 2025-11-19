package dao;

import model.Customer;
import java.util.List;

public interface CustomerDAO {
    Customer create(Customer customer) throws Exception;
    Customer findById(int id) throws Exception;
    List<Customer> findAll() throws Exception;
}
