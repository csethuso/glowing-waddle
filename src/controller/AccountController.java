package controller;

import dao.DaoFactory;
import dao.AccountDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.*;

public class AccountController {

    @FXML
    private TextField txtCustomerId, txtAccountNumber, txtOpeningBalance, txtEmployerName, txtEmployerAddr;
    @FXML
    private ChoiceBox<String> choiceAccountType;
    @FXML
    private TableView<Account> tblAccounts;
    @FXML
    private TableColumn<Account, String> colAccNo, colAccType, colBalance;

    private ObservableList<Account> accountList = FXCollections.observableArrayList();
    private Bank bank = CustomerController.getBank();

    private AccountDAO adao = DaoFactory.getAccountDAO();

    @FXML
    public void initialize() {
        choiceAccountType.setItems(FXCollections.observableArrayList("Savings", "Cheque", "Investment"));

        colAccNo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getAccountId())));
        colAccType.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getClass().getSimpleName()));
        colBalance.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.format("%.2f", c.getValue().getBalance())));

        tblAccounts.setItems(accountList);

        // load existing accounts from DB
        try {
            for (Account a : adao.findAll()) {
                accountList.add(a);
                bank.addAccount(a);
                // attach account to owner in bank if possible
                Customer owner = a.getOwner();
                if (owner != null) {
                    owner.addAccount(a);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createAccount() {
        try {
            int accId = Integer.parseInt(txtAccountNumber.getText());
            int custId = Integer.parseInt(txtCustomerId.getText());
            double openingBal = Double.parseDouble(txtOpeningBalance.getText());
            String accType = choiceAccountType.getValue();

            Customer customer = bank.findCustomer(custId);
            if (customer == null) {
                showAlert("Error", "Customer not found!");
                return;
            }

            Account newAcc = null;
            switch (accType) {
                case "Savings":
                    newAcc = new SavingsAccount(accId, customer, openingBal);
                    break;
                case "Cheque":
                    newAcc = new ChequeAccount(accId, customer, openingBal, txtEmployerName.getText());
                    break;
                case "Investment":
                    newAcc = new InvestmentAccount(accId, customer, openingBal);
                    break;
                default:
                    showAlert("Error", "Invalid account type selected!");
                    return;
            }

            // persist
            adao.create(newAcc);

            customer.addAccount(newAcc);
            bank.addAccount(newAcc);
            accountList.add(newAcc);

            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Invalid input. Please check all fields.");
        }
    }

    private void clearFields() {
        txtCustomerId.clear();
        txtAccountNumber.clear();
        txtOpeningBalance.clear();
        txtEmployerName.clear();
        txtEmployerAddr.clear();
        choiceAccountType.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
