/*----------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *---------------------------------------------------------------------------------------*/

package com.mycompany.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // initialize embedded DB and schema
        dao.DBInit.init();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Banking System");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
