package org.example.cstexam;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DFACONT {

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

    private int currentState;

    @FXML
    public void initialize() {
        // Set the default state to q0
        currentState = 0;
        highlightState(0); // Highlight initial state q0

        simulateBTN.setOnAction(event -> simulateDFA("10100")); // Example input for testing
        clearBTN.setOnAction(event -> clearSimulation());
    }

    // Simulate DFA process with the given input string
    @FXML
    private void simulateDFA(String input) {
        resetDFA(); // Start from initial state q0

        for (char symbol : input.toCharArray()) {
            processInput(symbol); // Process each symbol in the input string
        }

        // Check if the final state is the accepting state (q3) after processing the input
        if (currentState == 3) {
            showAlert("Accepted", "The input string is accepted because it ends with '100'.");
        } else {
            showAlert("Rejected", "The input string is rejected because it does not end with '100'.");
        }
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
                    // Stay in q2
                    currentState = 2;
                }
                break;
            case 3:
                // q3 is a trap state (once reached, we remain here)
                currentState = 3;
                break;
        }
        // Highlight the current state after processing the input
        highlightState(currentState);
    }

    // Highlight the current state by changing its color
    private void highlightState(int state) {
        // Reset all circles to default color
        resetHighlight();

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

    // Show an alert with the result (Accepted/Rejected)
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Clear the DFA simulation and reset to the initial state
    @FXML
    private void clearSimulation() {
        currentState = 0;
        highlightState(currentState); // Highlight the initial state q0
    }

    // Reset the DFA and highlight the initial state q0
    private void resetDFA() {
        currentState = 0;
        highlightState(currentState); // Highlight the initial state
    }
}
