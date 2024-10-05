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

        // q0: Process first binary number
        transitionTable.put("q0", Map.of(
                '0', new Transition("q0", '0', 1),  // Move right on '0'
                '1', new Transition("q0", '1', 1),  // Move right on '1'
                '+', new Transition("q1", '+', 1),  // Move to q1 when '+' is encountered
                'B', new Transition("q2", 'B', -1)  // Blank detected, move to q2 (end of input)
        ));

        // q1: Process second binary number
        transitionTable.put("q1", Map.of(
                '0', new Transition("q1", '0', 1),  // Move right on '0'
                '1', new Transition("q1", '1', 1),  // Move right on '1'
                '=', new Transition("q2", '=', -1)  // Encounter '=' and move left (to start addition)
        ));

        // q2: Move left to locate '+' and start addition
        transitionTable.put("q2", Map.of(
                '0', new Transition("q2", '0', -1),  // Move left on '0'
                '1', new Transition("q2", '1', -1),  // Move left on '1'
                '+', new Transition("q3", '+', -1)   // Move to q3 when '+' is found (start addition)
        ));

        // q3: Handle binary addition (flip bits, handle carry)
        transitionTable.put("q3", Map.of(
                '0', new Transition("q4", '1', 1),  // Change '0' to '1' (handle addition), move right
                '1', new Transition("q3", '0', -1), // Change '1' to '0' and stay (carry over)
                'B', new Transition("q4", '1', 1)   // Write '1' if blank (carry over), move to q4
        ));

        // q4: Finalize result, move right across the tape
        transitionTable.put("q4", Map.of(
                '0', new Transition("q4", '0', 1),  // Move right on '0'
                '1', new Transition("q4", '1', 1),  // Move right on '1'
                '=', new Transition("q5", '=', 1),  // Move to q5 (final state) when '=' is reached
                'B', new Transition("q5", 'B', 0)   // Halt at blank (final halt state)
        ));

        // q5: Final halting state
        transitionTable.put("q5", Map.of(
                'B', new Transition("q5", 'B', 0)   // Halt when blank is reached
        ));
    }






    // Simulate Turing machine process and show tape movement with brackets around changes
    private void simulateTuringMachine(String binary1, String binary2, String result) {
        ObservableList<TuringStep> turingSteps = FXCollections.observableArrayList();

        // Create the initial tape for binary1, binary2, and result
        String tape = binary1 + "+" + binary2 + "=" + result;
        char[] tapeArr = (tape + "B").toCharArray();  // Add a blank space at the end
        int headPosition = 0;
        String state = "q0";

        // Simulate transitions step by step
        while (!state.equals("q5")) {  // Continue until halting state (q5)
            char currentChar = tapeArr[headPosition];
            Map<Character, Transition> transitions = transitionTable.get(state);

            if (transitions != null && transitions.containsKey(currentChar)) {
                Transition transition = transitions.get(currentChar);

                // Create a copy of the tape with the head position highlighted
                StringBuilder tapeWithHead = new StringBuilder(new String(tapeArr));
                tapeWithHead.insert(headPosition, '(');
                tapeWithHead.insert(headPosition + 2, ')');  // Adjust for '(' insertion

                // Log the current state, head position, and action before updating
                turingSteps.add(new TuringStep(
                        state,
                        tapeWithHead.toString(),  // Current tape state with head position highlighted
                        String.valueOf(headPosition),
                        "Read '" + currentChar + "' -> Write '" + transition.write + "' Move " + (transition.move == 1 ? "Right" : "Left")
                ));

                // Update the tape (write new character)
                tapeArr[headPosition] = transition.write;

                // Move the head (left or right)
                headPosition += transition.move;

                // Transition to the next state
                state = transition.nextState;
            } else {
                break;  // No transition available, stop the machine
            }
        }

        // Add the final state before halting
        StringBuilder finalTapeWithHead = new StringBuilder(new String(tapeArr));
        finalTapeWithHead.insert(headPosition, '(');
        finalTapeWithHead.insert(headPosition + 2, ')');  // Adjust for '(' insertion

        turingSteps.add(new TuringStep(state, finalTapeWithHead.toString(), String.valueOf(headPosition), "Halt"));

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
