package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private void openCustomers(ActionEvent event) {
        openWindow("/view/CustomerManagement.fxml", "Customer Management");
    }

    @FXML
    private void openAccounts(ActionEvent event) {
        openWindow("/view/AccountManagement.fxml", "Account Management");
    }

    @FXML
    private void openTransactions(ActionEvent event) {
        // You can create a Transactions.fxml later
        System.out.println("Transactions screen not implemented yet.");
    }

    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
