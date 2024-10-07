package org.example.cstexam;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Stack;

public class PDACONT {

    @FXML
    private Button clearBTN;

    @FXML
    private Button exitBTN;

    @FXML
    private TextField inputTF;

    @FXML
    private TextArea resultAREA;

    @FXML
    private Button resultBTN;

    @FXML
    public void initialize() {
        resultBTN.setOnAction(e -> processInput());
        clearBTN.setOnAction(e -> clearFields());
        exitBTN.setOnAction(e -> System.exit(0));
    }

    private void processInput() {
        String input = inputTF.getText().trim();
        if (input.isEmpty()) {
            resultAREA.setText("Input cannot be empty.");
            return;
        }

        String result = checkWWReverse(input);
        resultAREA.setText(result);
    }

    private String checkWWReverse(String input) {
        Stack<Character> stack = new Stack<>();
        StringBuilder processLog = new StringBuilder();
        int len = input.length();
        int i = 0;

        processLog.append("Initial Input: ").append(input).append("\n");

        // Phase 1: Pushing characters to the stack (q0 to q1)
        processLog.append("Phase 1: Pushing characters onto the stack...\n");
        while (i < len / 2) {
            char currentChar = input.charAt(i);
            stack.push(currentChar);
            processLog.append("q0 -> q1: Pushed '").append(currentChar).append("' onto the stack. Stack: ").append(stack).append("\n");
            i++;
        }

        // Handle odd-length strings: Skip the middle character (if needed)
        if (len % 2 != 0) {
            char middleChar = input.charAt(i);
            processLog.append("Odd-length string, skipping middle character '").append(middleChar).append("'\n");
            i++;
        }

        // Phase 2: Comparing characters with stack (q1 to q2)
        processLog.append("Phase 2: Popping characters from the stack and comparing...\n");
        while (i < len) {
            if (stack.isEmpty()) {
                processLog.append("q2: Stack is empty but characters are still remaining. Rejecting.\n");
                return processLog.append("Result: The string is not accepted by the PDA.\n").toString();
            }

            char expectedChar = stack.pop();
            char currentChar = input.charAt(i);
            processLog.append("q2: Popped '").append(expectedChar).append("' from the stack. Comparing with '").append(currentChar).append("' in input.\n");

            if (expectedChar != currentChar) {
                processLog.append("Mismatch: Expected '").append(expectedChar).append("', but found '").append(currentChar).append("'. Rejecting.\n");
                return processLog.append("Result: The string is not accepted by the PDA.\n").toString();
            }
            i++;
        }

        // Phase 3: Final check (q2 to q3)
        if (stack.isEmpty()) {
            processLog.append("q3: Stack is empty and all characters matched. Accepting the string.\n");
            processLog.append("Result: The string is accepted by the PDA.\n");
        } else {
            processLog.append("q3: Stack is not empty after processing all input characters. Rejecting.\n");
            processLog.append("Result: The string is not accepted by the PDA.\n");
        }

        return processLog.toString();
    }

    private void clearFields() {
        inputTF.clear();
        resultAREA.clear();
    }
}
