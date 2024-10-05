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
        int split = input.indexOf('+');
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) != '+') {
                tape[i + 1] = input.charAt(i);  // Skip the "+" sign
            }
        }
        head = split + 1;  // Set head after the '+' sign
    }

    private void performStep() {
        char symbol = resultTape[head];

        // Print debug info
        System.out.println("State: " + state + ", Head: " + head + ", Symbol: " + symbol);

        switch (state) {
            case 0: // Reading the first binary number
                if (symbol == '1' || symbol == '0') {
                    head++; // Move right to the next bit
                } else if (symbol == '+') {
                    state = 1; // Move to state 1 to read the second binary number
                    head++; // Move right
                } else if (symbol == '_') {
                    state = 4; // Transition to final state (both numbers were empty)
                }
                break;

            case 1: // Reading the second binary number
                if (symbol == '1' || symbol == '0') {
                    head++; // Move right to the next bit
                } else if (symbol == '_') {
                    state = 2; // Move to state 2 for addition
                    head--; // Move back to the last valid symbol read
                }
                break;

            case 2: // Preparing for addition (at the end of the second number)
                System.out.println("In State 2: symbol='" + symbol + "', head=" + head);
                if (symbol == '_') {
                    System.out.println("State 2: Encountered '_', moving to State 3");
                    head--; // Move back to the last bit of the second number
                    state = 3; // Move to state 3 to perform addition
                } else {
                    head++; // Keep moving right through the second number
                }
                break;

            case 3: // Performing binary addition
                System.out.println("In State 3: Performing addition, head=" + head);

                // Get the last bits to add
                char bit1 = (head - 1 >= 0) ? resultTape[head - 1] : '_'; // Last bit of first binary number
                char bit2 = (head >= 0) ? resultTape[head] : '_'; // Last bit of second binary number

                // If both bits are blank, we've completed the addition
                if (bit1 == '_' && bit2 == '_') {
                    System.out.println("State 3: Addition complete, moving to final state.");
                    state = 4;  // Transition to final state
                    return;  // Stop further processing
                }

                // Perform binary addition
                char sum = (bit1 == '1' && bit2 == '1') ? '0' :
                        (bit1 == '1' || bit2 == '1') ? '1' : '0'; // Calculate the sum
                resultTape[head] = sum; // Store the sum in the current position

                // Handle carry if both bits are 1
                if (bit1 == '1' && bit2 == '1') {
                    propagateCarry(head - 1); // Propagate carry if both bits were 1
                }

                head--;  // Move to the next bit to the left for further addition
                break;

            case 4: // Final state
                System.out.println("Final state reached. Turing machine halting.");
                timer.stop();  // Stop the simulation
                break;
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

    // New method to convert binary result to decimal
    private int binaryToDecimal(String binary) {
        return binary.isEmpty() ? 0 : Integer.parseInt(binary, 2);  // Convert binary to decimal, handle empty case
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


