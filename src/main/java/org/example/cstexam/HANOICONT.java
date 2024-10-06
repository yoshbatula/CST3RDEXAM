package org.example.cstexam;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

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

    private Stack<Integer>[] towers;
    private int numberOfDisks;

    public void initialize() {
        reset();
    }

    @FXML
    public void simulateHanoi() {
        try {
            numberOfDisks = Integer.parseInt(inputTF.getText());
            if (numberOfDisks < 1 || numberOfDisks > 10) throw new NumberFormatException(); // Limit number of disks
            reset();
            drawTowers();

            // Run the solving process in a separate thread
            new Thread(() -> {
                solveHanoi(numberOfDisks, 0, 2, 1);
                Platform.runLater(() -> statusText.setText("Simulation Complete")); // Update status text after completion
            }).start(); // Run in a separate thread
        } catch (NumberFormatException e) {
            statusText.setText("Please enter a valid number of disks (1-10).");
        }
    }

    @FXML
    private void reset() {
        buildPANE.getChildren().clear();
        towers = new Stack[3];
        for (int i = 0; i < 3; i++) {
            towers[i] = new Stack<>();
        }
        statusText.setText(""); // Clear status text on reset
    }

    private void drawTowers() {
        double towerWidth = 10;
        double towerHeight = 200;
        double towerSpacing = 150;

        // Center the towers
        double centerX = buildPANE.getWidth() / 2;

        // Draw the towers
        for (int i = 0; i < 3; i++) {
            Rectangle tower = new Rectangle(towerWidth, towerHeight);
            tower.setFill(Color.GRAY);
            tower.setX(centerX + (i - 1) * towerSpacing); // Center towers relative to centerX
            tower.setY(80);
            buildPANE.getChildren().add(tower);
        }

        // Initialize disks
        for (int i = numberOfDisks; i > 0; i--) {
            towers[0].push(i);
        }
        drawDisks();
    }

    private void drawDisks() {
        buildPANE.getChildren().removeIf(node -> node instanceof Rectangle && node.getId() != null);
        for (int i = 0; i < 3; i++) {
            int[] disks = towers[i].stream().mapToInt(Integer::intValue).toArray();
            for (int j = 0; j < disks.length; j++) {
                int diskSize = disks[j];
                Rectangle disk = new Rectangle(diskSize * 20, 20);
                disk.setFill(Color.BLUE);
                disk.setId("disk" + diskSize); // Set an ID for easy removal
                disk.setX((buildPANE.getWidth() / 2) + (i - 1) * 150 - (diskSize * 20) / 2); // Center the disk on the tower
                disk.setY(300 - (20 * (j + 1))); // Stack the disks correctly
                buildPANE.getChildren().add(disk);
            }
        }
    }

    private void solveHanoi(int n, int from, int to, int aux) {
        if (n == 1) {
            moveDisk(from, to); // Move the disk directly
        } else {
            solveHanoi(n - 1, from, aux, to); // Move n-1 disks to auxiliary
            moveDisk(from, to); // Move the nth disk
            solveHanoi(n - 1, aux, to, from); // Move n-1 disks to the target
        }
    }

    private void moveDisk(int from, int to) {
        if (towers[from].isEmpty()) return;

        int disk = towers[from].pop();
        towers[to].push(disk);

        // Find the rectangle representing the disk to animate
        Rectangle diskRectangle = (Rectangle) buildPANE.lookup("#disk" + disk);
        if (diskRectangle != null) {
            // Calculate the target positions
            double targetX = (buildPANE.getWidth() / 2) + (to - 1) * 150 - (disk * 20) / 2;
            double targetY = 300 - (20 * (towers[to].size())); // Calculate Y position for the disk

            // Create a pause before the transition to make the move visible
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> {
                // Create the animation for moving the disk
                TranslateTransition transition = new TranslateTransition(Duration.seconds(1), diskRectangle);
                transition.setFromX(diskRectangle.getX());
                transition.setFromY(diskRectangle.getY());
                transition.setToX(targetX);
                transition.setToY(targetY);

                // When the transition is finished, update the status text and redraw disks
                transition.setOnFinished(event1 -> {
                    drawDisks(); // Update the visual representation of disks
                    // Update status text on the FX application thread
                    Platform.runLater(() -> {
                        statusText.setText("Moved disk " + disk + " from Tower " + (from + 1) + " to Tower " + (to + 1));
                    });
                });

                // Start the transition
                transition.play();
            });

            // Start the pause before the disk move
            pause.play();
        }
    }
}
