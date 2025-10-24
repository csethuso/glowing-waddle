package controller;

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

    @FXML
    public void initialize() {
        colName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        colPhone.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPhone()));
        tblCustomers.setItems(customerList);
    }

    @FXML
    private void addCustomer() {
        try {
            String name = txtFirstName.getText() + " " + txtLastName.getText();
            String address = txtAddress.getText();
            String phone = txtPhone.getText();

            Customer customer = new Customer(name, address, phone);
            bank.addCustomer(customer);
            customerList.add(customer);

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
