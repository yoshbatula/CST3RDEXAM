package org.example.cstexam;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

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

    @FXML
    private void initialize() {
        // Initialize the TableView for the Turing machine steps
        turingTable = new TableView<>();
        resultPANE.setContent(turingTable);  // Adding the TableView to the ScrollPane

        // Set up columns for the TableView
        TableColumn<TuringStep, String> stateCol = new TableColumn<>("State");
        TableColumn<TuringStep, String> tapeCol = new TableColumn<>("Tape");
        TableColumn<TuringStep, String> headCol = new TableColumn<>("Head");
        TableColumn<TuringStep, String> actionCol = new TableColumn<>("Action");

        // Set up how to populate the columns
        stateCol.setCellValueFactory(new PropertyValueFactory<>("state"));
        tapeCol.setCellValueFactory(new PropertyValueFactory<>("tape"));
        headCol.setCellValueFactory(new PropertyValueFactory<>("head"));
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));

        // Add columns to the TableView
        turingTable.getColumns().add(stateCol);
        turingTable.getColumns().add(tapeCol);
        turingTable.getColumns().add(headCol);
        turingTable.getColumns().add(actionCol);

        // Expand the table to fit the ScrollPane
        turingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);  // Resize columns to fill table
        resultPANE.setFitToWidth(true);  // Ensure TableView fits the width of ScrollPane
        resultPANE.setFitToHeight(true);  // Ensure TableView fits the height of ScrollPane
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

    // Simulate Turing machine process and populate table
    private void simulateTuringMachine(String binary1, String binary2, String result) {
        ObservableList<TuringStep> turingSteps = FXCollections.observableArrayList();

        // Example steps - You can customize this with actual Turing machine logic
        turingSteps.add(new TuringStep("q0", binary1 + " + " + binary2, "Head at LHS", "Start"));
        turingSteps.add(new TuringStep("q1", binary1, "Head at 2nd bit", "Read 1"));
        turingSteps.add(new TuringStep("q2", result, "Head at RHS", "Result computed"));

        turingTable.setItems(turingSteps);  // Set items to display in the table
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
