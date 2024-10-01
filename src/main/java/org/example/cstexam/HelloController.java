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
    private GridPane ORIGINALTAPE; // FXML component for original tape

    @FXML
    private GridPane RESULTAPE; // FXML component for result tape

    @FXML
    private TextField DecimalResult;

    @FXML
    private TextField firstBTN;

    @FXML
    private TextField secondBTN;

    private char[] originalTape = new char[20]; // Internal representation of the original tape
    private char[] resultTape = new char[20];   // Internal representation of the result tape
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
        initializeTape(originalTape, input); // Initialize internal tape with input
        initializeTape(resultTape, input);    // Initialize result tape as well

        state = 0;
        head = 10;

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (state == 3) {
                timer.stop();
                BinaryResult.setText(getResult());
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
    public void onGetResultClicked() {
        // Step 1: Validate and process binary input
        String firstBinary = firstBTN.getText();
        String secondBinary = secondBTN.getText();

        // Check if inputs are valid and not empty
        if (firstBinary.isEmpty() || secondBinary.isEmpty()) {
            showErrorMessage("Please enter both binary numbers.");
            return;
        }

        if (!isValidBinaryInput(firstBinary) || !isValidBinaryInput(secondBinary)) {
            showErrorMessage("Invalid input! Please enter only binary digits (0, 1).");
            return;
        }

        // Initialize the binary tapes and start the Turing Machine simulation
        onBinaryResultClicked();

        // Step 2: Convert binary to decimal and show the decimal result
        try {
            int decimal1 = Integer.parseInt(firstBinary, 2);  // Convert first binary number to decimal
            int decimal2 = Integer.parseInt(secondBinary, 2); // Convert second binary number to decimal
            int sum = decimal1 + decimal2;                    // Calculate the decimal sum

            // Display the decimal result
            DecimalResult.setText(decimal1 + " + " + decimal2 + " = " + sum);
        } catch (NumberFormatException ex) {
            showErrorMessage("Invalid binary input for decimal conversion.");
        }
    }

    @FXML
    public void onDecimalResultClicked() {
        try {
            int decimal1 = Integer.parseInt(firstBTN.getText(), 2);
            int decimal2 = Integer.parseInt(secondBTN.getText(), 2);
            int sum = decimal1 + decimal2;
            DecimalResult.setText(decimal1 + " + " + decimal2 + " = " + sum);
        } catch (NumberFormatException ex) {
            showErrorMessage("Invalid binary input for decimal conversion.");
        }
    }

    @FXML
    public void onClearClicked() {
        // Clear all input fields and reset the tapes
        firstBTN.setText("");
        secondBTN.setText("");
        BinaryResult.setText("");
        DecimalResult.setText("");
        Arrays.fill(originalTape, '_'); // Reset original tape
        Arrays.fill(resultTape, '_');   // Reset result tape
        drawTapes();
    }

    public void onExitClicked() {
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
            tape[i + 10] = input.charAt(i);
        }
        head = 10;
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
                    head--;
                    state = 2;
                }
                break;

            case 2:
                char firstBit = resultTape[head - 4]; // First operand bit
                char secondBit = resultTape[head];    // Second operand bit

                if (firstBit == '1' && secondBit == '1') {
                    resultTape[head] = '0'; // Set current position to '0'
                    propagateCarry(head - 1); // Handle carry
                    head--;
                } else if (firstBit == '1' && secondBit == '0' || firstBit == '0' && secondBit == '1') {
                    resultTape[head] = '1'; // Simple sum without carry
                    head--;
                } else {
                    resultTape[head] = '0'; // Both are '0'
                    head--;
                }

                if (head == 9) {
                    state = 3; // Stop when the computation is done
                }
                break;
        }
    }

    private void propagateCarry(int position) {
        // Move left and propagate the carry
        while (position >= 0 && resultTape[position] != '_') {
            if (resultTape[position] == '0') {
                resultTape[position] = '1'; // Set carry
                return; // Stop carry propagation
            } else if (resultTape[position] == '1') {
                resultTape[position] = '0'; // Flip bit and continue carry
            }
            position--;
        }

        // If we reach a blank space, place the carry there
        if (position >= 0) {
            resultTape[position] = '1'; // Place carry at empty spot
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
        finalResult = finalResult.replaceFirst("^0+(?!$)", "");

        return finalResult;
    }

    private void drawTapes() {
        drawTape(ORIGINALTAPE, originalTape, "Original Tape");
        drawTape(RESULTAPE, resultTape, "Result Tape");
    }

    private void drawTape(GridPane tapeGrid, char[] tape, String label) {
        tapeGrid.getChildren().clear();

        // Set the background color of the GridPane
        tapeGrid.setStyle("-fx-background-color: white;"); // or any desired color

        Canvas canvas = new Canvas(760, 100);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font("Monospaced", 20));
        int x = 20;
        int y = 50;
        gc.fillText(label, 20, 30);

        for (int i = 0; i < tape.length; i++) {
            gc.strokeRect(x, y, 30, 30);  // Drawing the grid
            gc.fillText(String.valueOf(tape[i]), x + 10, y + 20); // Drawing tape values
            x += 30;
        }
        gc.fillText("^", 20 + (head * 30) + 10, y + 50); // Drawing the head pointer
        tapeGrid.add(canvas, 0, 0);  // Add canvas to the GridPane
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}