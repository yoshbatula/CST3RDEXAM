package org.example.cstexam;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class mainCont {

    @FXML
    private Button cfgBTN;

    @FXML
    private Button dfaBTN;

    @FXML
    private Button ndfaBTN;

    @FXML
    private Button pdaBTN;

    @FXML
    private Button tmBTN;

    @FXML
    private Button tohBTN;

    public void navigation(ActionEvent event) throws IOException {
        if (event.getSource() == cfgBTN) {

            Stage window = (Stage) cfgBTN.getScene().getWindow();
            window.close();

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CFG.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();
        } else if (event.getSource() == dfaBTN) {
            Stage window = (Stage) dfaBTN.getScene().getWindow();
            window.close();

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DFA.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();

        } else if (event.getSource() == ndfaBTN) {
            Stage window = (Stage) ndfaBTN.getScene().getWindow();
            window.close();

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NDFA.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();

        } else if (event.getSource() == pdaBTN) {
            Stage window = (Stage) pdaBTN.getScene().getWindow();
            window.close();

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PDA.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();

        } else if (event.getSource() == tmBTN) {
            Stage window = (Stage) tmBTN.getScene().getWindow();
            window.close();

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BACKUP.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();

        } else if (event.getSource() == tohBTN) {
            Stage window = (Stage) tohBTN.getScene().getWindow();
            window.close();

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TOWEROFHANOI.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();
        }
    }
}

