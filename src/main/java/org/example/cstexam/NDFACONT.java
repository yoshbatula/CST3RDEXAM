package org.example.cstexam;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class NDFACONT {

    @FXML
    private Circle circleQ0;  // Representing state q0
    @FXML
    private Circle circleq1;  // Representing state q1
    @FXML
    private Circle circleQ2;  // Representing state q2

    @FXML
    private Button clearBTN;
    @FXML
    private Button simulateBTN;

    @FXML
    private TextField inputTF;

    // Method to highlight the current state
    private void highlightState(Circle stateCircle, boolean active) {
        if (active) {
            stateCircle.setFill(Color.GREEN);  // Active state gets green color
        } else {
            stateCircle.setFill(Color.LIGHTGRAY);  // Reset inactive state color
        }
    }

    // Method to simulate DFA step by step
    @FXML
    public void simulateDFA() {
        String input = inputTF.getText();
        resetStates();  // Reset states to default colors
        if (input.isEmpty()) {
            return;
        }

        // Start the simulation from q0
        highlightState(circleQ0, true);

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> processInput(input, 0, "q0"));
        pause.play();
    }

    private void processInput(String input, int index, String currentState) {
        if (index >= input.length()) {
            // Check if the final state is q2 (accepting state)
            boolean isAccepted = currentState.equals("q2");
            showResultAlert(isAccepted);  // Show the result after processing the input
            return;
        }

        char currentChar = input.charAt(index);
        switch (currentState) {
            case "q0":
                if (currentChar == '0') {
                    highlightState(circleQ0, false);
                    highlightState(circleq1, true);  // Transition to q1
                    PauseTransition pauseQ1 = new PauseTransition(Duration.seconds(1));
                    pauseQ1.setOnFinished(event -> processInput(input, index + 1, "q1"));
                    pauseQ1.play();
                } else {
                    // Handle rejection for '1' or invalid input
                    showResultAlert(false);
                    highlightState(circleQ0, true);  // Remain in q0
                }
                break;
            case "q1":
                if (currentChar == '1') {
                    highlightState(circleq1, false);
                    highlightState(circleQ2, true);  // Transition to q2
                    PauseTransition pauseQ2 = new PauseTransition(Duration.seconds(1));
                    pauseQ2.setOnFinished(event -> processInput(input, index + 1, "q2"));
                    pauseQ2.play();
                } else {
                    // Handle rejection for '0' or invalid input
                    showResultAlert(false);
                    highlightState(circleq1, true);  // Remain in q1
                }
                break;
            case "q2":
                // q2 loops on both '0' and '1'
                highlightState(circleQ2, true);  // Remain in q2
                PauseTransition pauseQ2Loop = new PauseTransition(Duration.seconds(1));
                pauseQ2Loop.setOnFinished(event -> processInput(input, index + 1, "q2"));
                pauseQ2Loop.play();
                break;
            default:
                break;
        }
    }

    // Method to display an alert
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {  // Ensures this runs on the JavaFX Application Thread
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);  // No header text
            alert.setContentText(message);
            alert.show();  // Use show() instead of showAndWait()
        });
    }

    // Method to show result (accepted/rejected)
    private void showResultAlert(boolean accepted) {
        if (accepted) {
            showAlert("Accepted", "THE INPUT STRING IS ACCEPTED: " + inputTF.getText());
        } else {
            showAlert("Rejected", "THE INPUT STRING IS REJECTED: " + inputTF.getText());
        }
    }

    // Reset the colors of all states
    private void resetStates() {
        highlightState(circleQ0, false);
        highlightState(circleq1, false);
        highlightState(circleQ2, false);
    }

    @FXML
    public void clearInput() {
        inputTF.clear();
        resetStates();  // Reset colors when clearing input
    }
}
