package org.example.cstexam;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
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
    private Pane resultPanel;

    @FXML
    private TextField resultTapeField;

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
            transitionAREA.setText(""); // Clear previous results
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
        transitionAREA.setText(""); // Clear the TextArea
        resultPanel.getChildren().clear(); // Clear the result panel
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
        transitionTable.get("q0").put('+', new Transition("q0", '+', 1));  // Read '+', stay in q0, move right
        transitionTable.get("q0").put('B', new Transition("q1", 'B', -1)); // Read 'B', move left, go to q1

        // State q1 transitions
        transitionTable.put("q1", new HashMap<>());
        transitionTable.get("q1").put('0', new Transition("q1", '1', -1));  // Read '0', write '1', move left
        transitionTable.get("q1").put('1', new Transition("q2", '0', -1));  // Read '1', write '0', move left
        transitionTable.get("q1").put('+', new Transition("q4", 'B', 1));   // Read '+', write 'B', move right, go to q4

        // State q2 transitions
        transitionTable.put("q2", new HashMap<>());
        transitionTable.get("q2").put('0', new Transition("q2", '0', -1));  // Read '0', stay in q2, move left
        transitionTable.get("q2").put('1', new Transition("q2", '1', -1));  // Read '1', stay in q2, move left
        transitionTable.get("q2").put('+', new Transition("q3", '+', -1));  // Read '+', move left, go to q3

        // State q3 transitions
        transitionTable.put("q3", new HashMap<>());
        transitionTable.get("q3").put('1', new Transition("q3", '0', -1));  // Read '1', write '0', move left
        transitionTable.get("q3").put('0', new Transition("q0", '1', 1));   // Read '0', write '1', move right, go to q0
        transitionTable.get("q3").put('B', new Transition("q0", '1', 1));   // Read 'B', write '1', move right, go to q0

        // State q4 transitions
        transitionTable.put("q4", new HashMap<>());
        transitionTable.get("q4").put('B', new Transition("q5", 'B', 0));   // Halt condition
        transitionTable.get("q4").put('1', new Transition("q4", '0', 1));   // Read '1', write '0', move right
        transitionTable.get("q4").put('0', new Transition("q4", '1', 1));   // Read '0', write '1', move right
    }

    @FXML
    private void simulateTuringMachine(String binary1, String binary2, String binaryResult) {
        transitionAREA.setText(""); // Clear previous transition logs

        // Create the tape as a character array
        char[] tapeArray = (binary1 + "+" + binary2 + "B").toCharArray(); // Start directly at the first number
        StringBuilder steps = new StringBuilder();
        String currentState = "q0";
        int headPosition = 0;

        while (!currentState.equals("q5")) { // Halt when in state q5
            char currentSymbol = tapeArray[headPosition];
            Transition transition = transitionTable.get(currentState).get(currentSymbol);

            if (transition == null) {
                steps.append("No transition found for state: " + currentState + " with symbol: " + currentSymbol + "\n");
                break;
            }

            // Log the transition
            steps.append("Transition: " + currentState + ", Read: " + currentSymbol + ", Write: " + transition.getWriteSymbol() + ", Move: " + (transition.getDirection() == 1 ? "R" : "L") + ", Next: " + transition.getNextState() + "  Current Tape: " + new String(tapeArray) + "\n");

            // Perform the transition
            tapeArray[headPosition] = transition.getWriteSymbol();
            currentState = transition.getNextState();
            headPosition += transition.getDirection();

            // Ensure head position is within bounds
            if (headPosition < 0) {
                tapeArray = extendTapeLeft(tapeArray);
                headPosition = 0;
            } else if (headPosition >= tapeArray.length) {
                tapeArray = extendTapeRight(tapeArray);
            }
        }

        steps.append("The input is accepted.\n"); // Indicate acceptance after halting

        // Convert the updated tape back to string and filter out valid digits
        StringBuilder filteredResult = new StringBuilder();
        for (char c : tapeArray) {
            if (c == 'B') {
                break; // Stop if we encounter 'B'
            }
            if (c == '0' || c == '1') {
                filteredResult.append(c); // Append only valid digits
            }
        }

        String resultTape = new String(filteredResult);
        transitionAREA.setText(steps.toString()); // Display steps in the text area
        resultTapeField.setText(resultTape);

        resultPanel.getChildren().clear();
        resultPanel.getChildren().add(new Text("Result Tape:"));

        // Display the result tape in resultPanel
        for (char bit : resultTape.toCharArray()) {
            Label bitLabel = new Label(String.valueOf(bit));
            bitLabel.setStyle("-fx-border-color: black; -fx-padding: 5px;");
            resultPanel.getChildren().add(bitLabel);
        }
    }

    private char[] extendTapeLeft(char[] tapeArray) {
        char[] newTape = new char[tapeArray.length + 1];
        System.arraycopy(tapeArray, 0, newTape, 1, tapeArray.length);
        newTape[0] = 'B';
        return newTape;
    }

    private char[] extendTapeRight(char[] tapeArray) {
        char[] newTape = new char[tapeArray.length + 1];
        System.arraycopy(tapeArray, 0, newTape, 0, tapeArray.length);
        newTape[newTape.length - 1] = 'B';
        return newTape;
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