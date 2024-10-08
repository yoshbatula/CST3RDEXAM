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

public class HelloController {

    @FXML
    private TextField BinaryResult;

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
    private TextField DecimalResult;

    @FXML
    private TextField firstBTN;

    @FXML
    private TextField secondBTN;

    private char[] originalTape = new char[20];
    private char[] resultTape = new char[20];
    private int state;
    private int head;

    private Timeline timer;

    public HelloController() {
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
                BinaryResult.setText(getResult());  // Show binary result
                drawTapes();
            } else {
                performStep();
                drawTapes();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();

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

        switch (state) {
            case 0:
                if (symbol == '1' || symbol == '0') {
                    head++;
                } else if (symbol == '+') {
                    state = 1;
                    head++;
                }
                break;

            case 1:
                if (symbol == '1' || symbol == '0') {
                    head++;
                } else if (symbol == '_') {
                    head--; // Go back to the last number
                    state = 2;
                }
                break;

            case 2:
                char firstBit = resultTape[head - 4];  // Adjust based on where you expect the bits to be
                char secondBit = resultTape[head];

                if (firstBit == '1' && secondBit == '1') {
                    resultTape[head] = '0';  // Set the result bit
                    propagateCarry(head - 1);  // Handle carry
                    head--; // Move left
                } else if (firstBit == '1' && secondBit == '0' || firstBit == '0' && secondBit == '1') {
                    resultTape[head] = '1'; // Set result to '1'
                    head--; // Move left
                } else { // Both bits are '0'
                    resultTape[head] = '0'; // Set result to '0'
                    head--; // Move left
                }

                if (head < 0) { // Check if we've reached the beginning of the tape
                    state = 3; // Final state
                }
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
