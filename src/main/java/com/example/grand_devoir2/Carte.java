package com.example.grand_devoir2;

import java.io.Serializable;
import java.util.Random;

public class Carte implements Serializable {
    private static final long serialVersionUID = 1L;
    private int[][] matrix;

    public Carte() {
        // Initialize a 10x10 matrix with random numbers from 0 to 5
        matrix = new int[9][9];
        Random random = new Random();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                matrix[i][j] = random.nextInt(6); // Generates a number between 0 and 5
            }
        }
    }
    public void setBuilding(int x, int y, int buildingType) {
        if (buildingType < 7 || buildingType > 9) {
            System.out.println("Invalid building type. Only values 7, 8, or 9 are allowed.");
            return;
        }
        if (x >= 0 && x < matrix.length && y >= 0 && y < matrix[0].length) {
            matrix[x][y] = buildingType;
            System.out.println("Building type " + buildingType + " placed at (" + x + ", " + y + ").");
        } else {
            System.out.println("Invalid position: (" + x + ", " + y + ").");
        }
    }


    public int[][] getMatrix() {
        return matrix;
    }

    public int[][] getCarte() {
        return matrix;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }

    private HelloController helloController;

    // Set HelloController reference
    public void setHelloController(HelloController helloController) {
        this.helloController = helloController;
        System.out.println("HelloController has been set in Carte.");
    }

    public void addResource(String resourceName, int amount) {
        if (helloController != null) {
            helloController.addResourceToInventory(resourceName, amount);
        } else {
            System.out.println("HelloController is not set. Cannot add resources.");
        }
    }

    // Custom serialization logic
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject(); // Serialize non-transient fields
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject(); // Deserialize non-transient fields
        // helloController will remain null after deserialization and must be set manually if needed
    }
}
