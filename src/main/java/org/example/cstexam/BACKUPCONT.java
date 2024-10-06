package org.example.cstexam;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashMap;
import java.util.Map;

public class BACKUPCONT {

    @FXML
    private TextField firstTF;

    @FXML
    private TextField secondTF;

    @FXML
    private TextField resultTF;

    @FXML
    private TextField decimalTF;

    @FXML
    private ScrollPane resultPANE;

    private Map<String, Map<Character, Transition>> transitionTable;

    @FXML
    private TextArea transitionAREA;

    @FXML
    private void initialize() {
        System.out.println("Initializing controller..."); // Debugging statement
        System.out.println("transitionAREA is " + (transitionAREA == null ? "null" : "not null")); // Check if it's null
        // Initialize the Turing machine's transition table
        initializeTransitionTable();
    }

    @FXML
    private void handleGetResult() {
        String binary1 = firstTF.getText();
        String binary2 = secondTF.getText();

        if (!binary1.matches("[01]+") || !binary2.matches("[01]+")) {
            resultTF.setText("Invalid input! Enter valid binary numbers.");
            decimalTF.setText("");
            transitionAREA.clear(); // Clear previous results
        } else {
            String binaryResult = addBinary(binary1, binary2);
            resultTF.setText(binaryResult);
            int decimalResult = Integer.parseInt(binaryResult, 2);
            decimalTF.setText(Integer.toString(decimalResult));
            simulateTuringMachine(binary1, binary2, binaryResult);
        }
    }

    @FXML
    private void handleClear() {
        firstTF.clear();
        secondTF.clear();
        resultTF.clear();
        decimalTF.clear();
        transitionAREA.clear(); // Clear the TextArea
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    private String addBinary(String binary1, String binary2) {
        int i = binary1.length() - 1;
        int j = binary2.length() - 1;
        int carry = 0;
        StringBuilder result = new StringBuilder();

        while (i >= 0 || j >= 0 || carry == 1) {
            int sum = carry;
            if (i >= 0) sum += binary1.charAt(i--) - '0';
            if (j >= 0) sum += binary2.charAt(j--) - '0';

            result.append(sum % 2);
            carry = sum / 2;
        }

        return result.reverse().toString();
    }

    private void initializeTransitionTable() {
        transitionTable = new HashMap<>();

        // State q0 transitions
        transitionTable.put("q0", new HashMap<>());
        transitionTable.get("q0").put('0', new Transition("q0", '0', 1));  // Read '0', stay in q0, move right
        transitionTable.get("q0").put('1', new Transition("q0", '1', 1));  // Read '1', stay in q0, move right
        transitionTable.get("q0").put('+', new Transition("q1", '+', 1));  // Read '+', move to q1, move right

        // State q1 transitions
        transitionTable.put("q1", new HashMap<>());
        transitionTable.get("q1").put('0', new Transition("q1", '0', 1));  // Read '0', stay in q1, move right
        transitionTable.get("q1").put('1', new Transition("q1", '1', 1));  // Read '1', stay in q1, move right
        transitionTable.get("q1").put('B', new Transition("q2", 'B', -1));  // Read blank, move left, go to q2

        // State q2 transitions (Addition logic)
        transitionTable.put("q2", new HashMap<>());
        transitionTable.get("q2").put('1', new Transition("q3", '0', -1));  // Read '1', write '0', carry, move left
        transitionTable.get("q2").put('0', new Transition("q3", '1', -1));  // Read '0', write '1', move left

        // State q3 transitions
        transitionTable.put("q3", new HashMap<>());
        transitionTable.get("q3").put('+', new Transition("q4", '+', -1));  // Skip over '+', move to q4
        transitionTable.get("q3").put('1', new Transition("q4", '0', -1));  // Propagate carry, move left

        // State q4 transitions
        transitionTable.put("q4", new HashMap<>());
        transitionTable.get("q4").put('1', new Transition("q5", '0', 0));  // Handle carry and move to q5
        transitionTable.get("q4").put('0', new Transition("q5", '1', 0));  // No carry, move to q5
        transitionTable.get("q4").put('+', new Transition("q5", '+', 0));  // Skip '+' and move to q5
    }

    public void appendToTextArea(String text) {
        transitionAREA.appendText(text + "\n");
    }

    @FXML
    private void simulateTuringMachine(String binary1, String binary2, String binaryResult) {
        transitionAREA.clear(); // Clear previous transition logs

        // Create the tape as a StringBuilder
        StringBuilder tape = new StringBuilder(binary1 + "+" + binary2 + "B");

        Task<Void> task = new Task<Void>() {
            String currentState = "q0"; // Start state
            int headPosition = 0; // Head starts at the first position

            @Override
            protected Void call() {
                while (!currentState.equals("q5")) { // Run until the final state is reached
                    // Check if the head position is valid
                    if (headPosition < 0 || headPosition >= tape.length()) {
                        appendToTextArea("Head position out of bounds. Halting.");
                        break; // Exit the loop
                    }

                    // Get the current symbol under the head
                    char currentSymbol = tape.charAt(headPosition); // Get the current symbol

                    // Debugging output
                    appendToTextArea("Current State: " + currentState + ", Head Position: " + headPosition + ", Symbol: " + currentSymbol);

                    // Find the transition for the current state and symbol
                    Transition transition = transitionTable.get(currentState).get(currentSymbol);

                    if (transition == null) {
                        // No valid transition found; log an error and halt
                        appendToTextArea("No transition found for state: " + currentState + " with symbol: " + currentSymbol);
                        break; // Exit the loop
                    }

                    // Perform the transition
                    tape.setCharAt(headPosition, transition.getWriteSymbol()); // Write the symbol
                    currentState = transition.getNextState(); // Update to the next state
                    headPosition += transition.getDirection(); // Move the head

                    // Ensure head position is within bounds
                    if (headPosition < 0) {
                        tape.insert(0, 'B');
                        headPosition = 0;
                    } else if (headPosition >= tape.length()) {
                        tape.append('B');
                    }

                    // Show the current state, tape, and head position in the TextArea
                    appendToTextArea("Current State: " + currentState + ", Tape: " + tape.toString() + ", Head Position: " + headPosition);

                    try {
                        Thread.sleep(1000); // Simulate processing time
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        new Thread(task).start(); // Start the task in a new thread
    }
    public static class Transition {
        private String nextState;
        private char writeSymbol;
        private int direction;

        public Transition(String nextState, char writeSymbol, int direction) {
            this.nextState = nextState;
            this.writeSymbol = writeSymbol;
            this.direction = direction;
        }

        public String getNextState() {
            return nextState;
        }

        public char getWriteSymbol() {
            return writeSymbol;
        }

        public int getDirection() {
            return direction;
        }
    }
}
