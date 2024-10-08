package org.example.cstexam;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class HANOICONT {

    @FXML
    private AnchorPane buildPANE;

    @FXML
    private TextField inputTF;

    @FXML
    private Button resetBTN;

    @FXML
    private Button simulateBTN;

    @FXML
    private Text statusText;

    @FXML
    private Button exitbtn;

    private Stack<Rectangle>[] towers;
    private int numDisks;
    private final double towerX[] = {100, 300, 500}; // X positions of the towers
    private int currentStep;

    // List to store move steps as pairs of (from, to)
    private List<MoveStep> moveSteps;
    private int moveStepIndex; // To track the current move step

    @FXML
    public void initialize() {
        simulateBTN.setOnAction(event -> startSimulation());
        resetBTN.setOnAction(event -> resetGame());
    }

    private void startSimulation() {
        String input = inputTF.getText();
        try {
            numDisks = Integer.parseInt(input);
            if (numDisks < 1) {
                statusText.setText("Please enter a positive integer.");
                return;
            }
            buildPANE.getChildren().clear();
            initializeTowers();
            statusText.setText("Simulation started...");
            currentStep = 0; // Reset step count

            // Generate all move steps first
            moveSteps = new ArrayList<>();
            hanoi(numDisks, 0, 2, 1);

            // Initialize move step index
            moveStepIndex = 0;

            // Disable simulate button to prevent multiple simulations
            simulateBTN.setDisable(true);
            resetBTN.setDisable(true);
            inputTF.setDisable(true);

            // Start executing the move steps
            executeNextMove();
        } catch (NumberFormatException e) {
            statusText.setText("Invalid input. Please enter a number.");
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeTowers() {
        towers = new Stack[3];
        for (int i = 0; i < 3; i++) {
            towers[i] = new Stack<>();
        }

        // Draw the towers
        for (int i = 0; i < 3; i++) {
            Line tower = new Line(towerX[i], 100, towerX[i], 300); // Tower height
            buildPANE.getChildren().add(tower);
        }

        double width = 30;
        double height = 20;

        // Initialize the disks (largest to smallest)
        for (int i = 0; i < numDisks; i++) {
            double diskWidth = width + (numDisks - i - 1) * 20;
            Rectangle disk = new Rectangle(diskWidth, height, Color.BLUE);
            disk.setLayoutX(towerX[0] - (diskWidth / 2));
            disk.setLayoutY(300 - (i + 1) * (height + 5)); // Largest disk at the bottom
            towers[0].push(disk);
            buildPANE.getChildren().add(disk);
        }
    }

    // Method to generate move steps and store them in moveSteps list
    private void hanoi(int n, int from, int to, int aux) {
        if (n == 1) {
            moveSteps.add(new MoveStep(from, to));
        } else {
            hanoi(n - 1, from, aux, to);
            moveSteps.add(new MoveStep(from, to));
            hanoi(n - 1, aux, to, from);
        }
    }

    // Execute the next move step
    private void executeNextMove() {
        if (moveStepIndex < moveSteps.size()) {
            MoveStep step = moveSteps.get(moveStepIndex);
            animateMove(step, () -> {
                moveStepIndex++;
                executeNextMove(); // Execute the next move after current one finishes
            });
        } else {
            // Simulation completed
            statusText.setText("Simulation completed in " + currentStep + " steps.");
            // Re-enable buttons and input
            simulateBTN.setDisable(false);
            resetBTN.setDisable(false);
            inputTF.setDisable(false);
        }
    }

    // Animate a single move step
    private void animateMove(MoveStep step, Runnable onFinished) {
        Rectangle disk;
        try {
            disk = getTopDisk(step.from);
        } catch (IllegalStateException e) {
            statusText.setText("Error: " + e.getMessage());
            // Re-enable buttons and input
            simulateBTN.setDisable(false);
            resetBTN.setDisable(false);
            inputTF.setDisable(false);
            return;
        }

        // Increment step count and update status
        currentStep++;
        Platform.runLater(() -> statusText.setText("Step " + currentStep + ": Move disk from Tower " + (step.from + 1) + " to Tower " + (step.to + 1)));

        // Calculate target positions
        double diskWidth = disk.getWidth();
        double targetX = towerX[step.to] - (diskWidth / 2);
        double targetY = 300 - ((towers[step.to].size()) * (disk.getHeight() + 5));

        // Create TranslateTransition
        TranslateTransition move = new TranslateTransition(Duration.seconds(0.5), disk);
        move.setToX(targetX - disk.getLayoutX());
        move.setToY(targetY - disk.getLayoutY());

        move.setOnFinished(event -> {
            // Update tower stacks
            towers[step.from].pop();
            towers[step.to].push(disk);

            // Update the disk's layout position
            disk.setLayoutX(targetX);
            disk.setLayoutY(targetY);

            // Reset translation
            disk.setTranslateX(0);
            disk.setTranslateY(0);

            // Proceed to the next move
            onFinished.run();
        });

        // Play the animation
        move.play();
    }

    // Helper method to get the top disk from a tower
    private Rectangle getTopDisk(int towerIndex) {
        if (towers[towerIndex].isEmpty()) {
            throw new IllegalStateException("Attempting to move a disk from an empty tower.");
        }
        return towers[towerIndex].peek();
    }

    private void resetGame() {
        buildPANE.getChildren().clear();
        inputTF.clear();
        statusText.setText("Game reset. Enter number of disks to start.");
        // Re-enable buttons and input in case they were disabled
        simulateBTN.setDisable(false);
        resetBTN.setDisable(false);
        inputTF.setDisable(false);
    }

    // Inner class to represent a move step
    private static class MoveStep {
        int from;
        int to;

        MoveStep(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }

    public void exit(ActionEvent event) throws IOException {

        if (event.getSource() == exitbtn) {

            Stage window = (Stage) exitbtn.getScene().getWindow();
            window.close();

            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.show();
        }
    }
}
