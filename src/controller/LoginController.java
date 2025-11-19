package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblMessage;

    @FXML
    private void onLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // Simple hardcoded login for demo
        if (username.equals("admin") && password.equals("1234")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DashboardView.fxml"));
                Stage stage = (Stage) txtUsername.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Bank Dashboard");
            } catch (Exception e) {
                e.printStackTrace();
                lblMessage.setText("Error loading dashboard.");
            }
        } else {
            lblMessage.setText("Invalid credentials!");
        }
    }
}
