package com.example.grand_devoir2;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class GameState implements Serializable{
    private static final long serialVersionUID = 1L;
    private int[][] matrix; // The game matrix
    private int playerPosX; // Player's X position
    private int playerPosY; // Player's Y position
    private HashMap<String, Integer> inventory; // Inventory resources
    private List<Objet> playerObjects; // Player's inventory objects
    private List<Ennemi> enemies; // List of enemies
    private Carte carte; // The game map (added field)

    // Getters and setters
    public int[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }

    public int getPlayerPosX() {
        return playerPosX;
    }

    public void setPlayerPosX(int playerPosX) {
        this.playerPosX = playerPosX;
    }

    public int getPlayerPosY() {
        return playerPosY;
    }

    public void setPlayerPosY(int playerPosY) {
        this.playerPosY = playerPosY;
    }

    public HashMap<String, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(HashMap<String, Integer> inventory) {
        this.inventory = inventory;
    }

    public List<Objet> getPlayerObjects() {
        return playerObjects;
    }

    public void setPlayerObjects(List<Objet> playerObjects) {
        this.playerObjects = playerObjects;
    }

    public List<Ennemi> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Ennemi> enemies) {
        this.enemies = enemies;
    }
    public Carte getCarte() {
        return carte;
    }

    public void setCarte(Carte carte) {
        this.carte = carte;
    }
}
