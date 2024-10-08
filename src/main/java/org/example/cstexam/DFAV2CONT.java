package org.example.cstexam;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class DFAV2CONT {

    @FXML
    private Circle circleQ0;

    @FXML
    private Circle circleq1;

    @FXML
    private Circle circleQ2;

    @FXML
    private Circle circleQ3;

    @FXML
    private Button clearBTN;

    @FXML
    private Button simulateBTN;

    @FXML
    private TextField inputTF;

    @FXML
    private Button exitbtn;


    private int currentState;

    @FXML
    public void initialize() {
        // Set the default state to q0
        currentState = 0;
        highlightState(0); // Highlight initial state q0

        simulateBTN.setOnAction(event -> {
            String input = inputTF.getText(); // Get input from text field
            simulateDFA(input); // Pass the input to the simulation
        });
        clearBTN.setOnAction(event -> clearSimulation());
    }

    // Simulate DFA process with the given input string
    @FXML
    private void simulateDFA(String input) {
        resetDFA(); // Start from initial state q0

        // Create a Timeline for step-by-step state transitions
        Timeline timeline = new Timeline();
        Duration delayBetweenSteps = Duration.seconds(1); // Delay between steps (1 second)

        for (int i = 0; i < input.length(); i++) {
            final int step = i; // This needs to be final or effectively final for lambda

            KeyFrame keyFrame = new KeyFrame(delayBetweenSteps.multiply(i + 1), event -> {
                char symbol = input.charAt(step);
                processInput(symbol); // Process each symbol in the input string
                highlightState(currentState); // Highlight the current state
            });

            timeline.getKeyFrames().add(keyFrame);
        }

        // Once the DFA finishes processing all inputs, check for acceptance
        KeyFrame finalFrame = new KeyFrame(delayBetweenSteps.multiply(input.length() + 1), event -> {
            if (currentState == 3) {
                showAlert("Accepted", "THE INPUT STRING IS ACCEPTED: " + inputTF.getText());
            } else {
                showAlert("Rejected", "THE INPUT STRING IS REJECTED: " + inputTF.getText());
            }
        });

        timeline.getKeyFrames().add(finalFrame);
        timeline.play(); // Start the timeline for smooth transitions
    }

    // Process each symbol in the DFA and update the state
    private void processInput(char symbol) {
        switch (currentState) {
            case 0:
                if (symbol == '0') {
                    // Stay in q0
                    currentState = 0;
                } else if (symbol == '1') {
                    // Move to q1
                    currentState = 1;
                }
                break;
            case 1:
                if (symbol == '0') {
                    // Move to q2
                    currentState = 2;
                } else if (symbol == '1') {
                    // Stay in q1
                    currentState = 1;
                }
                break;
            case 2:
                if (symbol == '0') {
                    // Move to q3 (Accepting state)
                    currentState = 3;
                } else if (symbol == '1') {
                    // Move back to q1
                    currentState = 1;
                }
                break;
            case 3:
                // q3 is a trap state (once reached, we remain here)
                currentState = 3;
                break;
        }
    }

    // Highlight the current state by changing its color
    private void highlightState(int state) {
        resetHighlight(); // Reset previous highlights

        // Highlight the current state
        switch (state) {
            case 0:
                circleQ0.setFill(Color.GREEN); // Highlight q0
                break;
            case 1:
                circleq1.setFill(Color.GREEN); // Highlight q1
                break;
            case 2:
                circleQ2.setFill(Color.GREEN); // Highlight q2
                break;
            case 3:
                circleQ3.setFill(Color.GREEN); // Highlight q3 (Accepting state)
                break;
        }
    }

    // Reset all circles to their default color (before highlighting)
    private void resetHighlight() {
        circleQ0.setFill(Color.LIGHTGRAY);
        circleq1.setFill(Color.LIGHTGRAY);
        circleQ2.setFill(Color.LIGHTGRAY);
        circleQ3.setFill(Color.LIGHTGRAY);
    }

    // Method to display an alert
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);  // You can remove this if you want a header
            alert.setContentText(message);
            alert.show();  // Display the alert without blocking the JavaFX thread
        });
    }

    // Clear the DFA simulation and reset to the initial state
    @FXML
    private void clearSimulation() {
        resetDFA(); // Reset the DFA simulation
    }

    // Reset the DFA and highlight the initial state q0
    private void resetDFA() {
        inputTF.setText("");
        currentState = 0; // Reset to initial state
        highlightState(currentState); // Highlight the initial state
    }

    public void exit(ActionEvent event) throws IOException {

        if (event.getSource() == exitbtn) {

            Stage window = (Stage) exitbtn.getScene().getWindow();
            window.close();

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();
        }
    }
}
