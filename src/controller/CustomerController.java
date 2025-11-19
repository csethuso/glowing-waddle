package controller;

import dao.DaoFactory;
import dao.CustomerDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Bank;
import model.Customer;

public class CustomerController {

    @FXML
    private TextField txtCustomerId, txtFirstName, txtLastName, txtAddress, txtPhone, txtEmail;
    @FXML
    private TableView<Customer> tblCustomers;
    @FXML
    private TableColumn<Customer, String> colName, colPhone;

    private ObservableList<Customer> customerList = FXCollections.observableArrayList();

    // Shared bank instance
    private static Bank bank = new Bank("MyBank", "001");

    private CustomerDAO cdao = DaoFactory.getCustomerDAO();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        colPhone.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPhone()));
        tblCustomers.setItems(customerList);

        // load existing customers from DB
        try {
            for (Customer c : cdao.findAll()) {
                customerList.add(c);
                bank.addCustomer(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addCustomer() {
        try {
            String name = txtFirstName.getText() + " " + txtLastName.getText();
            String address = txtAddress.getText();
            String phone = txtPhone.getText();

            Customer customer;
            String idText = txtCustomerId.getText();
            if (idText != null && !idText.isEmpty()) {
                int id = Integer.parseInt(idText);
                customer = new Customer(id, name, address, phone);
            } else {
                customer = new Customer(name, address, phone);
            }

            // persist
            Customer persisted = cdao.create(customer);
            bank.addCustomer(persisted);
            customerList.add(persisted);

            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtCustomerId.clear();
        txtFirstName.clear();
        txtLastName.clear();
        txtAddress.clear();
        txtPhone.clear();
        txtEmail.clear();
    }

    public static Bank getBank() {
        return bank;
    }
}
