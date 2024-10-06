package org.example.cstexam;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.HashMap;
import java.util.Map;

public class BACKUPCONT {

    @FXML
    private TextField firstTF;

    @FXML
    private TextField secondTF;

    @FXML
    private TextField resultTF;  // For binary result

    @FXML
    private TextField decimalTF;  // For decimal result

    @FXML
    private ScrollPane resultPANE;  // For Turing machine table

    private TableView<TuringStep> turingTable;  // Create a table for the Turing machine

    private Map<String, Map<Character, Transition>> transitionTable;  // Turing machine transition table

    @FXML
    private void initialize() {
        // Initialize the TableView for the Turing machine steps
        turingTable = new TableView<>();
        resultPANE.setContent(turingTable);  // Adding the TableView to the ScrollPane

        // Set up columns for the TableView
        TableColumn<TuringStep, String> stateCol = new TableColumn<>("Current State");
        TableColumn<TuringStep, String> readSymbolCol = new TableColumn<>("Read Symbol");
        TableColumn<TuringStep, String> writeSymbolCol = new TableColumn<>("Write Symbol");
        TableColumn<TuringStep, String> moveDirectionCol = new TableColumn<>("Move Direction");
        TableColumn<TuringStep, String> nextStateCol = new TableColumn<>("Next State");
        TableColumn<TuringStep, String> tapeTransitionCol = new TableColumn<>("Tape Transition");

        // Set up how to populate the columns
        stateCol.setCellValueFactory(new PropertyValueFactory<>("state"));
        readSymbolCol.setCellValueFactory(new PropertyValueFactory<>("action")); // You can modify this if necessary
        writeSymbolCol.setCellValueFactory(new PropertyValueFactory<>("writeSymbol"));
        moveDirectionCol.setCellValueFactory(new PropertyValueFactory<>("moveDirection"));
        nextStateCol.setCellValueFactory(new PropertyValueFactory<>("nextState"));
        tapeTransitionCol.setCellValueFactory(new PropertyValueFactory<>("tapeTransition"));

        // Add columns to the TableView
        turingTable.getColumns().addAll(stateCol, readSymbolCol, writeSymbolCol, moveDirectionCol, nextStateCol, tapeTransitionCol);

        // Expand the table to fit the ScrollPane
        turingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);  // Resize columns to fill table
        resultPANE.setFitToWidth(true);  // Ensure TableView fits the width of ScrollPane
        resultPANE.setFitToHeight(true);  // Ensure TableView fits the height of ScrollPane

        // Initialize the Turing machine's transition table
        initializeTransitionTable();
    }

    // Method to handle binary addition when "GET RESULT" is clicked
    @FXML
    private void handleGetResult() {
        String binary1 = firstTF.getText();
        String binary2 = secondTF.getText();

        // Check if the inputs are valid binary numbers
        if (!binary1.matches("[01]+") || !binary2.matches("[01]+")) {
            resultTF.setText("Invalid input! Enter valid binary numbers.");
            decimalTF.setText("");
        } else {
            // Perform binary addition and display the result
            String binaryResult = addBinary(binary1, binary2);
            resultTF.setText(binaryResult);

            // Convert binary result to decimal and display it
            int decimalResult = Integer.parseInt(binaryResult, 2);
            decimalTF.setText(Integer.toString(decimalResult));

            // Simulate a simple Turing machine process and populate the table
            simulateTuringMachine(binary1, binary2, binaryResult);
        }
    }

    // Method to handle clearing input fields and result
    @FXML
    private void handleClear() {
        firstTF.clear();
        secondTF.clear();
        resultTF.clear();
        decimalTF.clear();
        turingTable.getItems().clear();  // Clear the Turing machine table
    }

    // Method to handle exiting the application
    @FXML
    private void handleExit() {
        System.exit(0);
    }

    // Helper method for binary addition
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

        // q0 Transitions (process first binary number)
        transitionTable.put("q0", Map.of(
                '0', new Transition("q0", '0', 1),  // Stay in q0 if '0'
                '1', new Transition("q0", '1', 1),  // Stay in q0 if '1'
                '+', new Transition("q1", '+', 1)   // Move to q1 on '+'
        ));

        // q1 Transitions (process second binary number)
        transitionTable.put("q1", Map.of(
                '0', new Transition("q1", '0', 1),  // Stay in q1 if '0'
                '1', new Transition("q1", '1', 1),  // Stay in q1 if '1'
                'B', new Transition("q2", 'B', -1)  // Move to q2 when blank (end of second binary number)
        ));

        // q2 Transitions (move left to begin addition)
        transitionTable.put("q2", Map.of(
                '0', new Transition("q2", '0', -1),  // Move left through the second binary number
                '1', new Transition("q2", '1', -1),  // Move left through the second binary number
                '+', new Transition("q3", '+', -1)   // Move to q3 when '+' is reached (start adding)
        ));

        // q3 Transitions (perform binary addition, handle carry bit)
        transitionTable.put("q3", Map.of(
                '0', new Transition("q4", '1', -1),  // Write '1' when adding '0' and carry
                '1', new Transition("q3", '0', -1),  // Write '0' when adding '1' (carry), stay in q3
                'B', new Transition("q4", '1', 1)     // Write '1' for overflow, move right to halt
        ));

        // q4 Transitions (finalize and halt)
        transitionTable.put("q4", Map.of(
                '0', new Transition("q4", '0', 1),  // Final state, move right
                '1', new Transition("q4", '1', 1),  // Final state, move right
                'B', new Transition("q5", 'B', 0)   // Halt state
        ));

        // q5 Transitions (halt state)
        transitionTable.put("q5", Map.of(
                'B', new Transition("q5", 'B', 0)   // Stay in q5 (halt)
        ));
    }

    private void simulateTuringMachine(String binary1, String binary2, String result) {
        ObservableList<TuringStep> turingSteps = FXCollections.observableArrayList();

        // Create the initial tape with binary1 + binary2 + blank
        String tape = binary1 + "+" + binary2 + "B";  // Add blank at the end for the Turing machine to know when to stop
        char[] tapeArr = tape.toCharArray();  // Convert to char array
        int headPosition = 0;
        String state = "q0";

        // Simulate transitions step by step
        while (!state.equals("q5")) {  // Continue until halting state q5
            char currentChar = tapeArr[headPosition];
            Map<Character, Transition> transitions = transitionTable.get(state);

            if (transitions != null && transitions.containsKey(currentChar)) {
                Transition transition = transitions.get(currentChar);

                // Create a copy of the tape with the head position highlighted
                StringBuilder tapeWithHead = new StringBuilder();
                for (int i = 0; i < tapeArr.length; i++) {
                    if (i == headPosition) {
                        tapeWithHead.append("(").append(tapeArr[i]).append(")");  // Highlight head
                    } else {
                        tapeWithHead.append(tapeArr[i]);
                    }
                }

                // Log the current action details
                String action = "Read '" + currentChar + "'"; // Only show the read symbol
                String moveDirection = (transition.move == 1) ? "Right" : "Left";

                // Create the tape transition representation without "Write"
                // Indicate the change in the tape with brackets
                tapeArr[headPosition] = transition.write; // Write to the tape

                // Create the new tape state
                StringBuilder newTapeState = new StringBuilder();
                for (int i = 0; i < tapeArr.length; i++) {
                    if (i == headPosition) {
                        newTapeState.append("(").append(tapeArr[i]).append(")");  // Highlight the current head
                    } else {
                        newTapeState.append(tapeArr[i]);
                    }
                }

                // Format the tape transition output
                String tapeTransition = String.format("%s + %s -> %s",
                        binary1 + " + " + binary2 + "B",   // Initial input
                        transition.write,                   // What is written
                        newTapeState.toString()             // Current state of the tape with head highlighted
                );

                // Add a new TuringStep without the write symbol in the action
                turingSteps.add(new TuringStep(state, tapeWithHead.toString(), String.valueOf(headPosition), action,
                        String.valueOf(transition.write), moveDirection, transition.nextState, tapeTransition));

                // Move the head (left or right)
                headPosition += transition.move;

                // Transition to the next state
                state = transition.nextState;
            } else {
                break;  // No transition available, stop the machine
            }
        }

        // Add the final state before halting
        StringBuilder finalTapeWithHead = new StringBuilder();
        for (int i = 0; i < tapeArr.length; i++) {
            if (i == headPosition) {
                finalTapeWithHead.append("(").append(tapeArr[i]).append(")");  // Highlight head
            } else {
                finalTapeWithHead.append(tapeArr[i]);
            }
        }

        // Log the final state when halting
        String finalResult = new String(tapeArr).replace("B", ""); // Remove 'B' for final result
        turingSteps.add(new TuringStep(state, finalTapeWithHead.toString(), String.valueOf(headPosition), "Halt",
                String.valueOf('B'), "Stay", "q5", finalTapeWithHead.toString() + " -> Final Result: " + finalResult));

        // Set the table data
        turingTable.setItems(turingSteps);
    }
    // Class representing a transition in the Turing machine
    private static class Transition {
        String nextState;
        char write;
        int move;  // 1 for right, -1 for left

        Transition(String nextState, char write, int move) {
            this.nextState = nextState;
            this.write = write;
            this.move = move;
        }
    }

    public static class TuringStep {
        private String state;
        private String tape;
        private String head;
        private String action;
        private String writeSymbol;
        private String moveDirection;
        private String nextState;
        private String tapeTransition; // Field to show initial input

        public TuringStep(String state, String tape, String head, String action,
                          String writeSymbol, String moveDirection, String nextState, String tapeTransition) {
            this.state = state;
            this.tape = tape;
            this.head = head;
            this.action = action;
            this.writeSymbol = writeSymbol;
            this.moveDirection = moveDirection;
            this.nextState = nextState;
            this.tapeTransition = tapeTransition; // Initialize new field
        }

        // Getters for all fields
        public String getState() {
            return state;
        }

        public String getTape() {
            return tape;
        }

        public String getHead() {
            return head;
        }

        public String getAction() {
            return action;
        }

        public String getWriteSymbol() {
            return writeSymbol;
        }

        public String getMoveDirection() {
            return moveDirection;
        }

        public String getNextState() {
            return nextState;
        }

        public String getTapeTransition() {
            return tapeTransition;
        }
    }
}
