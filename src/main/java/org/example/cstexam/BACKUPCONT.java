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
        TableColumn<TuringStep, String> stateCol = new TableColumn<>("State");
        TableColumn<TuringStep, String> tapeCol = new TableColumn<>("Tape");
        TableColumn<TuringStep, String> headCol = new TableColumn<>("Head Position");
        TableColumn<TuringStep, String> actionCol = new TableColumn<>("Action");

        // Set up how to populate the columns
        stateCol.setCellValueFactory(new PropertyValueFactory<>("state"));
        tapeCol.setCellValueFactory(new PropertyValueFactory<>("tape"));
        headCol.setCellValueFactory(new PropertyValueFactory<>("head"));
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));

        // Add columns to the TableView
        turingTable.getColumns().addAll(stateCol, tapeCol, headCol, actionCol);

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

        // q0 Transitions: Process first binary number
        transitionTable.put("q0", Map.of(
                '0', new Transition("q0", '0', 1),  // Stay in q0 if '0'
                '1', new Transition("q0", '1', 1),  // Stay in q0 if '1'
                '+', new Transition("q1", '+', 1),  // Move to q1 on '+' (after first binary)
                'B', new Transition("q2", 'B', -1)  // Move to q2 on blank (end of input)
        ));

        // q1 Transitions: Process second binary number
        transitionTable.put("q1", Map.of(
                '0', new Transition("q1", '0', 1),  // Stay in q1 if '0'
                '1', new Transition("q1", '1', 1),  // Stay in q1 if '1'
                '=', new Transition("q2", '=', -1)  // Move to q2 on '='
        ));

        // q2 Transitions: Move left to find '+' and start the addition process
        transitionTable.put("q2", Map.of(
                '0', new Transition("q2", '0', -1),  // Stay in q2 and move left on '0'
                '1', new Transition("q2", '1', -1),  // Stay in q2 and move left on '1'
                '+', new Transition("q3", '+', -1)   // Move to q3 on '+'
        ));

        // q3 Transitions: Handle carry bit during addition
        transitionTable.put("q3", Map.of(
                '0', new Transition("q4", '1', 1),  // Change '0' to '1' and move to q4
                '1', new Transition("q3", '0', -1), // Change '1' to '0' and stay in q3
                'B', new Transition("q4", '1', 1)   // Handle carry, write '1' to blank, move to q4
        ));

        // q4 Transitions: Move right to finalize result
        transitionTable.put("q4", Map.of(
                '0', new Transition("q4", '0', 1),  // Stay in q4 and move right on '0'
                '1', new Transition("q4", '1', 1),  // Stay in q4 and move right on '1'
                '=', new Transition("q5", '=', 1),  // Keep moving right after '='
                'B', new Transition("q5", 'B', 0)   // Halt on blank (q5 is the halt state)
        ));

        // q5 Transitions: Halt state
        transitionTable.put("q5", Map.of(
                'B', new Transition("q5", 'B', 0)   // Stay in q5 (halt)
        ));
    }




    // Simulate Turing machine process and populate table
    private void simulateTuringMachine(String binary1, String binary2, String result) {
        ObservableList<TuringStep> turingSteps = FXCollections.observableArrayList();

        // Creating a combined tape for binary1, binary2, and result
        String tape = binary1 + "+" + binary2 + "=" + result;
        char[] tapeArr = (tape + "B").toCharArray();  // Add blank space to tape
        int headPosition = 0;
        String state = "q0";

        // Simulate each transition
        while (!state.equals("q5")) {  // Continue until halt state (q5)
            char currentChar = tapeArr[headPosition];
            Map<Character, Transition> transitions = transitionTable.get(state);

            if (transitions != null && transitions.containsKey(currentChar)) {
                Transition transition = transitions.get(currentChar);

                // Log the current state and action before updating the tape
                turingSteps.add(new TuringStep(
                        state,
                        new String(tapeArr),
                        String.valueOf(headPosition),
                        "Read '" + currentChar + "' -> Write '" + transition.write + "' Move " + (transition.move == 1 ? "Right" : "Left")
                ));

                // Write the new value on the tape (update the tape content)
                tapeArr[headPosition] = transition.write;

                // Move the head
                headPosition += transition.move;

                // Transition to the next state
                state = transition.nextState;
            } else {
                // If no transition is available, halt the machine
                turingSteps.add(new TuringStep(state, new String(tapeArr), String.valueOf(headPosition), "Halt"));
                break;
            }
        }

        // Add the final state before halting
        turingSteps.add(new TuringStep(state, new String(tapeArr), String.valueOf(headPosition), "Halt"));

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

    // Class representing each step of the Turing machine
    public static class TuringStep {
        private String state;
        private String tape;
        private String head;
        private String action;

        public TuringStep(String state, String tape, String head, String action) {
            this.state = state;
            this.tape = tape;
            this.head = head;
            this.action = action;
        }

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
    }
}
