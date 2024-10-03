package org.example.cstexam;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Arrays;

public class V2CONT {

    @FXML
    private TextField BinaryResult;

    @FXML
    private TextField DecimalResult;

    @FXML
    private Button EXIT;

    @FXML
    private Button getresultBTN;

    @FXML
    private Button ClearInputBTN;

    @FXML
    private GridPane ORIGINALTAPE;

    @FXML
    private GridPane RESULTAPE;

    @FXML
    private TextField firstBTN;

    @FXML
    private TextField secondBTN;

    private char[] originalTape = new char[20];
    private char[] resultTape = new char[20];
    private int state;
    private int head;

    private Timeline timer;

    public V2CONT() {
        Arrays.fill(originalTape, '_');
        Arrays.fill(resultTape, '_');
    }

    @FXML
    public void initialize() {
        drawTapes();
    }

    @FXML
    public void onBinaryResultClicked() {
        String firstBinary = firstBTN.getText();
        String secondBinary = secondBTN.getText();

        if (!isValidBinaryInput(firstBinary) || !isValidBinaryInput(secondBinary)) {
            showErrorMessage("Invalid input! Please enter only binary digits (0, 1).");
            return;
        }

        String input = firstBinary + "+" + secondBinary;
        initializeTape(originalTape, input);
        initializeTape(resultTape, input);

        state = 0;  // Start state
        head = 1;   // Set head to start at index 1

        // Start the Turing machine simulation
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (state == 3) {
                timer.stop();
                String result = getResult(); // Get binary result
                BinaryResult.setText(result);  // Show binary result
                DecimalResult.setText(String.valueOf(binaryToDecimal(result))); // Show decimal result
                drawTapes();
            } else {
                performStep();
                drawTapes();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    @FXML
    public void onClearInputClicked() {
        // Reset all fields and tapes
        firstBTN.clear();
        secondBTN.clear();
        BinaryResult.clear();
        DecimalResult.clear(); // Clear decimal result
        Arrays.fill(originalTape, '_');
        Arrays.fill(resultTape, '_');
        head = 1;
        state = 0;
        drawTapes();
    }

    @FXML
    public void onExitClicked() {
        // Close the application
        System.exit(0);
    }

    private boolean isValidBinaryInput(String input) {
        for (char c : input.toCharArray()) {
            if (c != '0' && c != '1') {
                return false;
            }
        }
        return true;
    }

    private void initializeTape(char[] tape, String input) {
        Arrays.fill(tape, '_');
        for (int i = 0; i < input.length(); i++) {
            tape[i + 1] = input.charAt(i);  // Set starting index to 1
        }
        head = 1;  // Reset the head position to 1
    }

    private void performStep() {
        char symbol = resultTape[head];
        char firstBit = (head - 4 >= 0) ? resultTape[head - 4] : '_'; // Capture the first bit for debugging.

        System.out.println("State: " + state + ", Head: " + head + ", Symbol: " + symbol +
                ", First Bit: " + firstBit);

        switch (state) {
            case 0: // Reading the first binary number
                if (symbol == '1' || symbol == '0') {
                    head++; // Move right to next bit
                } else if (symbol == '+') {
                    state = 1; // Move to state 1 to read the second binary number
                    head++; // Move right
                } else if (symbol == '_') {
                    // Stop processing if we encounter an underscore with no further numbers
                    state = 3; // Transition to final state
                }
                break;

            case 1: // Reading the second binary number
                if (symbol == '1' || symbol == '0') {
                    head++; // Move right to next bit
                } else if (symbol == '_') {
                    // When we hit the end of the second number, move to the addition state
                    state = 2; // Transition to state 2 for addition
                    head--; // Move back to the last symbol read
                }
                break;

            case 2: // Performing the addition
                if (head < 0 || resultTape[head] == '_') {
                    // If we reach the left end of the tape or hit an underscore, stop processing
                    state = 3; // Move to final state
                } else {
                    char secondBit = resultTape[head]; // Current symbol to add
                    char sum = (firstBit == '1' && secondBit == '1') ? '0' :
                            (firstBit == '1' || secondBit == '1') ? '1' : '0'; // Calculate the sum

                    resultTape[head] = sum; // Store the sum
                    // Determine carry
                    if (firstBit == '1' && secondBit == '1') {
                        propagateCarry(head - 1); // Propagate carry if needed
                    }

                    // Move left for the next bit
                    head--;
                    firstBit = (head - 4 >= 0) ? resultTape[head - 4] : '_'; // Capture the new first bit for the next iteration
                }
                break;

            case 3: // Final state
                System.out.println("Final State Reached. Stopping the Turing Machine.");
                return; // Stop execution
        }
    }

    private void propagateCarry(int position) {
        while (position >= 0 && resultTape[position] != '_') {
            if (resultTape[position] == '0') {
                resultTape[position] = '1';  // Propagate the carry
                return;
            } else if (resultTape[position] == '1') {
                resultTape[position] = '0'; // Reset carry
            }
            position--;
        }

        if (position >= 0) {
            resultTape[position] = '1'; // Add carry if there’s room
        }
    }

    private String getResult() {
        StringBuilder result = new StringBuilder();

        for (char c : resultTape) {
            if (c == '1' || c == '0') {
                result.append(c);
            }
        }

        String finalResult = result.toString();
        finalResult = finalResult.replaceFirst("^0+(?!$)", "");  // Remove leading zeros

        return finalResult.isEmpty() ? "0" : finalResult;  // Return "0" if the result is empty
    }

    // New method to convert binary result to decimal
    private int binaryToDecimal(String binary) {
        return Integer.parseInt(binary, 2);
    }

    private void drawTapes() {
        drawTape(ORIGINALTAPE, originalTape, "Original Tape");
        drawTape(RESULTAPE, resultTape, "Result Tape");
    }

    private void drawTape(GridPane tapeGrid, char[] tape, String label) {
        tapeGrid.getChildren().clear();  // Clear previous drawings
        tapeGrid.setStyle("-fx-background-color: white;");

        Canvas canvas = new Canvas(760, 100);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font("Monospaced", 20));
        int x = 20;
        int y = 50;
        gc.fillText(label, 20, 30);

        for (int i = 0; i < tape.length; i++) {
            gc.strokeRect(x, y, 30, 30);  // Draw the tape cells
            gc.fillText(String.valueOf(tape[i]), x + 10, y + 20);  // Draw the symbols
            x += 30;
        }
        gc.fillText("^", 20 + (head * 30) + 10, y + 50);  // Draw the head position
        tapeGrid.add(canvas, 0, 0);  // Add the canvas to the GridPane
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


