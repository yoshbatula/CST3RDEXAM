package org.example.cstexam;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CFGCONT {

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
    void initialize() {
        resultBTN.setOnAction(event -> checkCFG());
        clearBTN.setOnAction(event -> clearFields());

    }

    private void checkCFG() {
        String input = inputTF.getText();

        // Check if the input contains only 'a' and 'b'
        if (!isValidInput(input)) {
            resultAREA.setText("The string \"" + input + "\" is rejected by the grammar. Only 'a' and 'b' are allowed.");
            return;
        }

        // Generate the output based on the input
        String generatedString = generateString(input);

        // Generate derivation steps and show in result area
        String derivationSteps = printDerivation(input, generatedString);
        resultAREA.setText(derivationSteps);
    }

    private boolean isValidInput(String input) {
        // Check if the input contains only 'a' and 'b'
        for (char c : input.toCharArray()) {
            if (c != 'a' && c != 'b') {
                return false;
            }
        }
        return true;
    }

    public String generateString(String w) {
        // Generate w^R (the reverse of w)
        String wR = new StringBuilder(w).reverse().toString();
        // Construct the result in the form of wcw^R
        return w + "c" + wR;
    }

    private String printDerivation(String w, String generated) {
        StringBuilder derivation = new StringBuilder("Derivation steps:\n");
        StringBuilder current = new StringBuilder("S");

        // Start with the initial S
        derivation.append(current).append("\n");

        // Build the derivation for the given string
        for (int i = 0; i < w.length(); i++) {
            char currentChar = w.charAt(i);
            current = new StringBuilder(current.toString().replaceFirst("S", currentChar + "S" + currentChar));
            derivation.append(" S => ").append(current).append("\n");
        }

        // Finally, replace S with 'c' to complete the pattern
        current = new StringBuilder(current.toString().replaceFirst("S", "c"));
        derivation.append(" S => ").append(current).append("\n");

        // Append the final result
        derivation.append(" => ").append(generated).append(" (ACCEPTED)");
        return derivation.toString();
    }

    private void clearFields() {
        inputTF.clear();
        resultAREA.clear();
    }


    public void exit(ActionEvent event) throws IOException {

        if (event.getSource() == exitBTN) {

            Stage window = (Stage) exitBTN.getScene().getWindow();
            window.close();

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();
        }
    }
}
