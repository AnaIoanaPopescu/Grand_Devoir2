package com.example.grand_devoir2;

import javafx.animation.PauseTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.*;

import static com.example.grand_devoir2.GestionFichiers.chargerEnnemis;

public class HelloController {

    @FXML
    private GridPane matrixGrid;
    @FXML
    private Carte carte;

    private static final int PLAYER = 6;
    private Joueur player; // Declare at the class level
    private Ennemi lastEnemy; // To store the last enemy fought

    private int playerPosX = 0;
    private int playerPosY = 0;
    // Inventory to store collected resources
    private final HashMap<String, Integer> inventory = new HashMap<>();

    @FXML
    private BorderPane rootPane; // Reference to the root container
    private MediaPlayer backgroundMusicPlayer;
    private MediaPlayer attackSoundPlayer;

    // Resource properties for real-time updates
    private IntegerProperty pierreCount = new SimpleIntegerProperty(0);
    private IntegerProperty cerealesCount = new SimpleIntegerProperty(0);
    private IntegerProperty boisCount = new SimpleIntegerProperty(0);


    @FXML
    private Label pierreLabel;

    @FXML
    private Label cerealesLabel;

    @FXML
    private Label boisLabel;

    @FXML
    private HBox resourceDisplay; // Optional, for the entire top section


    public void initialize() {
        if (player == null) {
            player = new Joueur("Marian", 10, 40, 100);
        }
        // Initialize inventory
        inventory.put("Bois", 0);
        inventory.put("Pierre", 0);
        inventory.put("Céréales", 0);

        // Bind labels to resource properties
        pierreLabel.textProperty().bind(pierreCount.asString());
        cerealesLabel.textProperty().bind(cerealesCount.asString());
        boisLabel.textProperty().bind(boisCount.asString());

        carte = new Carte();
        carte.setHelloController(this); // Set HelloController reference

        // Set the background image
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, false);
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(getClass().getResourceAsStream("/images/background.jpg")),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        rootPane.setBackground(new Background(backgroundImage)); // Apply to the root container
        carte = new Carte(); // Inițializează harta
        carte.getMatrix()[0][0] = PLAYER; // Poziționează jucătorul în colțul din stânga sus
        playerPosX = 0;
        playerPosY = 0;
        displayMatrix();
        matrixGrid.setFocusTraversable(true); // Permite detectarea tastelor
    }

    private void displayMatrix() {
        matrixGrid.getChildren().clear(); // Clear the existing grid
        int[][] matrix = carte.getMatrix(); // Retrieve the game matrix

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                // Load the default image for each cell based on its value
                ImageView imageView = new ImageView(getImageForCellValue(matrix[i][j]));
                imageView.setFitHeight(72); // Set image height
                imageView.setFitWidth(72); // Set image width
                matrixGrid.add(imageView, j, i); // Add the image to the grid at the correct position
            }
        }
    }

    private Image getImageForCellValue(int cellValue) {
        try {
            if (cellValue == 7) {
                return new Image(getClass().getResourceAsStream("/images/police.jpg"));
            } else if (cellValue == 8) {
                return new Image(getClass().getResourceAsStream("/images/supermarket.jpg"));
            } else if (cellValue == 9) {
                return new Image(getClass().getResourceAsStream("/images/military.jpg"));
            }

            // Handle other cell types
            switch (cellValue) {
                case 0: return new Image(getClass().getResourceAsStream("/images/empty.png"));
                case 1: return new Image(getClass().getResourceAsStream("/images/piatra.jpg"));
                case 3: return new Image(getClass().getResourceAsStream("/images/cereale.png"));
                case 4: return new Image(getClass().getResourceAsStream("/images/arbore.png"));
                case 6: return new Image(getClass().getResourceAsStream("/images/player.png"));
                case 5: return new Image(getClass().getResourceAsStream("/images/build(case 5).jpg"));
                default: return new Image(getClass().getResourceAsStream("/images/default.png"));
            }
        } catch (Exception e) {
            System.out.println("Error loading image for cell value: " + cellValue);
            return new Image(getClass().getResourceAsStream("/images/empty.png"));
        }
    }

    @FXML
    public void handleKeyInput(KeyEvent keyEvent) {
        KeyCode keyCode = keyEvent.getCode();
        System.out.println("Key pressed: " + keyCode);

        // Variables for movement direction
        int dx = 0, dy = 0;

        // Handle movement keys
        switch (keyCode) {
            case W -> dx = -1; // Move up
            case S -> dx = 1;  // Move down
            case A -> dy = -1; // Move left
            case D -> dy = 1;  // Move right
            default -> {
                // Show a message for incorrect key press
                System.out.println("Invalid key pressed. Use W, A, S, or D to move.");
                showAlertForInvalidKey(keyCode);
                return; // Exit the method for invalid keys
            }
        }

        // Move the player only if a valid key was pressed
        movePlayer(dx, dy);

        // Consume the event to prevent further propagation
        keyEvent.consume();
    }
    private void showAlertForInvalidKey(KeyCode keyCode) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Key Pressed");
        alert.setHeaderText("Unsupported Key");
        alert.setContentText("You pressed: " + keyCode.getName() + "\nPlease use W, A, S, or D to move.");
        alert.showAndWait();
    }
    private void updateBuildingImage(int x, int y) {
        int buildingType = carte.getMatrix()[x][y];

        // Determine the image path based on the building type
        String imagePath = switch (buildingType) {
            case 7 -> "/images/police.jpg";
            case 8 -> "/images/supermarket.jpg";
            case 9 -> "/images/military.jpg";
            default -> null; // No image for this type
        };

        if (imagePath != null) {
            try {
                System.out.println("Attempting to load image from path: " + imagePath);

                // Ensure the resource exists
                InputStream imageStream = Objects.requireNonNull(getClass().getResourceAsStream(imagePath),
                        "Image not found at path: " + imagePath);

                ImageView buildingImageView = new ImageView(new Image(imageStream));
                buildingImageView.setFitWidth(50);
                buildingImageView.setFitHeight(50);

                // Remove any existing node in the cell
                boolean removed = matrixGrid.getChildren().removeIf(node ->
                        GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == y &&
                                GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) == x
                );
                System.out.println("Node removed from grid: " + removed);


                // Add the image to the grid
                matrixGrid.add(buildingImageView, y, x); // Note: y = column, x = row
                matrixGrid.layout(); // Force UI refresh
                System.out.println("Building image added to grid at row: " + x + ", column: " + y);

            } catch (NullPointerException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No image path found for building type: " + buildingType);
        }
    }
    private void showOutOfBoundsMessage() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Out of Bounds");
        alert.setHeaderText("Invalid Move");
        alert.setContentText("You are trying to move outside the matrix. Please stay within bounds.");
        alert.showAndWait();
    }
    private void movePlayer(int dx, int dy) {
        int newX = playerPosX + dx;
        int newY = playerPosY + dy;
        int[][] matrix = carte.getMatrix();

        // Check if the new position is outside the matrix
        if (newX < 0 || newX >= matrix.length || newY < 0 || newY >= matrix[0].length) {
            System.out.println("You are out of bounds!");
            showOutOfBoundsMessage(); // Show the out-of-bounds message on the screen
            return; // Exit the method since the move is invalid
        } else {
            // Clear the old position only if it contains the player
            if (carte.getMatrix()[playerPosX][playerPosY] == PLAYER) {
                carte.getMatrix()[playerPosX][playerPosY] = 0;
            }

            // Update the player position
            playerPosX = newX;
            playerPosY = newY;

            // Handle special cells
            switch (matrix[playerPosX][playerPosY]) {
                case 0 -> handleEmptyCell(); // Handle empty cell (case vide)
                case 1 -> {
                    collectPierre();
                    pierreCount.set(pierreCount.get() + 2); // Update Pierre count in real time
                }
                case 2 -> {
                    startBattle();
                    matrix[playerPosX][playerPosY] = 2; // Ensure the enemy remains in the matrix
                }
                case 3 -> {
                    collectCereales();
                    cerealesCount.set(cerealesCount.get() + 2); // Update Céréales count in real time
                }
                case 4 -> {
                    collectBois();
                    boisCount.set(boisCount.get() + 2); // Update Bois count in real time
                }
                case 5 -> handleBuildableLocation();
                default -> System.out.println("No interaction for this cell.");
            }

            // Set the new position to PLAYER only if it is not a building
            if (carte.getMatrix()[playerPosX][playerPosY] < 7 || carte.getMatrix()[playerPosX][playerPosY] > 9) {
                carte.getMatrix()[playerPosX][playerPosY] = PLAYER;
            }

//            // Set the new position to PLAYER
//            matrix[playerPosX][playerPosY] = PLAYER;

            // Refresh the matrix display
            displayMatrix();

            // Debug: Print the matrix state after moving the player
            System.out.println("Matrix state after moving player:");
            for (int[] row : carte.getMatrix()) {
                System.out.println(Arrays.toString(row));
            }
            System.out.println("GridPane children count after player move: " + matrixGrid.getChildren().size());
        }
    }

    private void collectPierre() {
        inventory.put("Pierre", inventory.get("Pierre") + 2);
        System.out.println("Tu as récupéré de la pierre! Total: " + inventory.get("Pierre"));
    }
    private void startBattle() {
        System.out.println("La bataille a commencé!");
        playBackgroundMusic(); // Start background music
        if (player == null) {
            player = new Joueur("Marian", 10, 40, 100); // Initialize the player if not already done
        }

        // Load a random enemy
        Ennemi enemy = selectRandomEnemy();
        if (enemy == null) {
            System.out.println("Pas d'ennemis disponibles pour le combat!");
            return;
        }
        // Assign the current enemy to lastEnemy
        lastEnemy = enemy;

        System.out.println("Vous combattez : " + enemy.getNom() +
                " avec " + enemy.getCapture().getNom() +
                " (Attaque: " + enemy.getAttaque() +
                ", Défense: " + enemy.getDefense() +
                ", Santé: " + enemy.getSante() + ")");

        try {
            // Load the Battle FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/grand_devoir2/battle-view.fxml"));
            Parent root = loader.load();

            // Pass player and enemy to the BattleController
            BattleController controller = loader.getController();
            controller.initialize(player, enemy);

            // Declare and create a new stage for the battle
            Stage battleStage = new Stage(); // Declaration added here
            battleStage.setTitle("Battle!");

            // Set the scene with specific size
            Scene battleScene = new Scene(root, 750, 700); // Adjust size as needed
            battleStage.setScene(battleScene);

            // Override the close request
            battleStage.setOnCloseRequest(event -> {
                if (player.getSante() > 0 && lastEnemy.getSante() > 0) {
                    event.consume(); // Block the close request
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Battle In Progress");
                    alert.setHeaderText(null);
                    alert.setContentText("You cannot leave the battle until it is resolved!");
                    alert.showAndWait();
                }
            });

            // Show the battle stage and wait until it's closed
            battleStage.showAndWait(); // User must close this window to continue

            // Check the outcome after the battle
            if (player.getSante() <= 0) {
                System.out.println("Vous avez été vaincu !");
                // Stop all music after battle ends
                stopAllMusic();
                showDefeatMessage();
            } else if (lastEnemy.getSante() <= 0) {
                System.out.println("Vous avez vaincu " + lastEnemy.getNom() + "!");
                player.ramasser(lastEnemy.getCapture());
                player.afficherStatistiques();
            }
            stopAllMusic();

        } catch (IOException e) {
            System.out.println("Error loading battle view: " + e.getMessage());
        }
    }

    void playBackgroundMusic() {
        try {
            String musicFile = "src/main/resources/musique/battle_music.mp3"; // Path to the music file
            Media media = new Media(new File(musicFile).toURI().toString());
            backgroundMusicPlayer = new MediaPlayer(media);
            backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the music
            backgroundMusicPlayer.setVolume(0.2); // Set volume to 20%
            backgroundMusicPlayer.play();

            // Stop the music after 30 seconds
            PauseTransition pause = new PauseTransition(Duration.seconds(10));
            pause.setOnFinished(event -> {
                if (backgroundMusicPlayer != null) {
                    backgroundMusicPlayer.stop();
                    backgroundMusicPlayer.dispose();
                    backgroundMusicPlayer = null;
                    System.out.println("Background music stopped after 30 seconds.");
                }
            });
            pause.play();

            // Stop the sound after 30 seconds
            PauseTransition pause1 = new PauseTransition(Duration.seconds(10));
            pause.setOnFinished(event -> {
                if (attackSoundPlayer != null) {
                    attackSoundPlayer.stop();
                    attackSoundPlayer.dispose();
                    attackSoundPlayer = null;
                    System.out.println("Attack sound stopped after 30 seconds.");
                }
            });
            pause1.play();

        } catch (Exception e) {
            System.out.println("Error playing background music: " + e.getMessage());
        }
    }

    private void stopAllMusic() {
        // Path to the battle music file for debugging purposes
        String backgroundMusicPath = "src/main/resources/musique/battle_music.mp3";

        // Stop and dispose of the background music player
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.dispose();
            backgroundMusicPlayer = null;
            System.out.println("Background music stopped. File: " + backgroundMusicPath);
        }

        // Stop and dispose of the attack sound player
        if (attackSoundPlayer != null) {
            attackSoundPlayer.stop();
            attackSoundPlayer.dispose();
            attackSoundPlayer = null;
            System.out.println("Attack sound stopped. File: ");
        }
    }

    private void showDefeatMessage() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Défaite");
        alert.setHeaderText("Vous avez été vaincu !");
        alert.setContentText("Souhaitez-vous quitter le jeu ?");

        ButtonType quitButton = new ButtonType("Quitter", ButtonBar.ButtonData.YES);
        ButtonType stayButton = new ButtonType("Rester", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(quitButton, stayButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == quitButton) {

            // Save the game state before exiting
            saveGameState();
            System.exit(0); // Quit the application
        } else {
            System.out.println("Vous avez choisi de recommencer une nouvelle partie.");
            resetGame(); // Call the reset game logic
            matrixGrid.requestFocus();
        }
    }
    private void resetGame() {
        // Reinitialize the game state
        carte = new Carte(); // Reset the game map
        player = new Joueur("Marian", 10, 40, 100); // Reset the player stats
        playerPosX = 0; // Reset player position
        playerPosY = 0;
        carte.getMatrix()[playerPosX][playerPosY] = PLAYER; // Place the player in the initial position

        // Clear the inventory (optional)
        inventory.clear();
        inventory.put("Bois", 0);
        inventory.put("Pierre", 0);
        inventory.put("Céréales", 0);

        // Reset resource counters
        pierreCount.set(0);
        cerealesCount.set(0);
        boisCount.set(0);

        // Refresh the game matrix display
        displayMatrix();

        // Set focus back to the game grid for key inputs
        matrixGrid.requestFocus();

        System.out.println("La partie a été réinitialisée.");
    }
    private Ennemi selectRandomEnemy() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/com/example/grand_devoir2/ennemis.txt"))) {
            String line;
            List<Ennemi> enemies = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0];
                int attack = Integer.parseInt(parts[1]);
                int defense = Integer.parseInt(parts[2]);
                int health = Integer.parseInt(parts[3]);
                String weaponName = parts[4];

                // Fetch weapon object
                Objet weapon = loadObjectByName(weaponName);
                if (weapon != null) {
                    enemies.add(new Ennemi(name, attack, defense, health, weapon));
                }
            }

            // Choose a random enemy
            if (!enemies.isEmpty()) {
                return enemies.get(new Random().nextInt(enemies.size()));
            }
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement des ennemis : " + e.getMessage());
        }
        return null;
    }
    private Objet loadObjectByName(String objectName) {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/com/example/grand_devoir2/objets.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String name = parts[0];
                int healthBoost = Integer.parseInt(parts[1]);
                int attackBoost = Integer.parseInt(parts[2]);
                int defenseBoost = Integer.parseInt(parts[3]);

                if (name.equals(objectName)) {
                    return new Objet(name, healthBoost, attackBoost, defenseBoost);
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement des objets : " + e.getMessage());
        }
        return null;
    }

    private void collectCereales() {
        inventory.put("Céréales", inventory.get("Céréales") + 2);
        System.out.println("Vous avez collecté des céréales! Total: " + inventory.get("Céréales"));
    }
    private void collectBois() {
        inventory.put("Bois", inventory.get("Bois") + 2);
        System.out.println("Vous avez collecté du bois! Total: " + inventory.get("Bois"));
    }
    private void handleBuildableLocation() {
        System.out.println("Vous êtes sur un emplacement constructible(qui contient un batiment).\nChoisissez une action!");
        // List of building options
        List<String> buildingOptions = List.of(
                "Fontaine de Vie",
                "Monument de l'Épée",
                "Cabane de Bois",
                "Tour de Magie",
                "Cofeterie"
        );

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Fontaine de Vie", buildingOptions);
        dialog.setTitle("Construction de bâtiment");
        dialog.setHeaderText("Choisissez un bâtiment à construire :");
        dialog.setContentText("Options disponibles :");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            System.out.println("Aucun bâtiment sélectionné.");
            return; // Exit if no building was selected
        }

        String selectedBuilding = result.get();
        Batiment.Effet effet = switch (selectedBuilding) {
            case "Fontaine de Vie" -> Batiment.Effet.FONTAINE_VIE;
            case "Monument de l'Épée" -> Batiment.Effet.MONUMENT_EPEE;
            case "Cabane de Bois" -> Batiment.Effet.CABANE_BOIS;
            case "Tour de Magie" -> Batiment.Effet.TOUR_MAGIE;
            case "Cofeterie" -> Batiment.Effet.COFETERIE;
            default -> throw new IllegalStateException("Unexpected value: " + selectedBuilding);
        };

        // Create and activate the building
        Batiment batiment = new Batiment(selectedBuilding, effet, this); // Transmite referința HelloController
        batiment.activerEffet(player, carte);

        // Update the matrix to reflect the building's presence
        carte.getMatrix()[playerPosX][playerPosY] = 5; // Mark this cell as "constructed"

        // Refresh the display
        displayMatrix();

        System.out.println("Bâtiment " + selectedBuilding + " construit et effet appliqué !");
    }

    private void handleEmptyCell() {
        System.out.println("Vous pouvez construire ici, un bâtiment!");

        // Opțiuni pentru tipurile de clădiri
        List<String> choices = List.of("Police Station", "Supermarket", "Military Base");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Police Station", choices);
        dialog.setTitle("Building Selection");
        dialog.setHeaderText("Choose a building to construct:");
        dialog.setContentText("Available buildings:");

        // Maparea opțiunilor către tipurile de clădiri
        Map<String, Integer> buildingMapping = Map.of(
                "Police Station", 7,
                "Supermarket", 8,
                "Military Base", 9
        );

        // Alegerea utilizatorului
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            System.out.println("No building selected.");
            return;
        }

        // Extrage tipul clădirii
        int buildingChoice = buildingMapping.getOrDefault(result.get(), -1);

        if (buildingChoice == -1) {
            System.out.println("Invalid building choice.");
            return;
        }

        // Verifică resursele
        int currentBois = inventory.getOrDefault("Bois", 0);
        int currentPierre = inventory.getOrDefault("Pierre", 0);
        int currentCereales = inventory.getOrDefault("Cereales", 0);

        if (currentBois < 2 || currentPierre < 2) {
            System.out.println("Not enough resources to construct the building!");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Insufficient Resources");
            alert.setHeaderText("Not enough resources");
            alert.setContentText("You need at least 2 Bois and 2 Pierre to construct a building.");
            alert.showAndWait();
            return;
        }

        // Scade resursele
        inventory.put("Bois", currentBois - 2);
        inventory.put("Pierre", currentPierre - 2);
        System.out.println("Resources deducted: 2 Bois, 2 Pierre");

        // Logare înainte de actualizare
        System.out.println("Matrix state before placing building:");
        for (int[] row : carte.getMatrix()) {
            System.out.println(Arrays.toString(row));
        }

        // Actualizează matricea folosind metoda specială
        carte.setBuilding(playerPosX, playerPosY, buildingChoice);

        // Actualizează imaginea pe GridPane
        updateBuildingImage(playerPosX, playerPosY);

        // Reîmprospătează afișajul matricei
        displayMatrix();

        // Logare după actualizare
        System.out.println("Matrix state after placing building:");
        for (int[] row : carte.getMatrix()) {
            System.out.println(Arrays.toString(row));
        }
    }
    public void addResourceToInventory(String resourceName, int amount) {
        int currentAmount = inventory.getOrDefault(resourceName, 0);
        inventory.put(resourceName, currentAmount + amount);
        System.out.println("Added " + amount + " units of " + resourceName + " to the inventory.");
        System.out.println("Total " + resourceName + ": " + inventory.get(resourceName));
    }

    @FXML
    public void onResumeClick(ActionEvent actionEvent) {
        try {
            // Load the game state from file if necessary
            if (carte == null || inventory.isEmpty()) {
                loadGameState();
            }

            // Create a new stage for the resources and game state window
            Stage resourceStage = new Stage();
            resourceStage.setTitle("Resources and Game State");

            VBox resourceLayout = new VBox(10);
            resourceLayout.setPadding(new Insets(20));
            // Set the background color to light pink
            resourceLayout.setStyle("-fx-background-color: #FFC0CB;"); // Hex code for light pink

            // Set preferred size for the VBox
            resourceLayout.setPrefWidth(510); // Make the VBox wider
            resourceLayout.setPrefHeight(510); // Make the VBox taller

            // Display collected resources
            Label inventoryLabel = new Label("Resources Collected:");
            resourceLayout.getChildren().add(inventoryLabel);

            inventory.forEach((resource, count) -> {
                Label resourceLabel = new Label(resource + ": " + count);
                resourceLayout.getChildren().add(resourceLabel);
            });

            // Display player's base statistics and calculate bonuses
            if (player != null) {
                // Call the afficherStatistiques() method to log statistics to the console
                player.afficherStatistiques();

                Label playerStatsLabel = new Label("\nPlayer Statistics:");
                resourceLayout.getChildren().add(playerStatsLabel);

                // Calculate bonuses from objects in inventory
                double bonusAttack = 0;
                double bonusDefense = 0;
                double bonusHealth = 0;

                if (!player.getInventaireObjets().isEmpty()) {
                    for (Objet obj : player.getInventaireObjets()) {
                        bonusAttack += obj.getBonusAttaque();
                        bonusDefense += obj.getBonusDefense();
                        bonusHealth += obj.getBonusSante();
                    }
                }

                // Calculate final stats
                double totalAttack = player.getAttaque() + bonusAttack;
                double totalDefense = player.getDefense() + bonusDefense;
                double totalHealth = player.getSante() + bonusHealth;

                Label playerDetails = new Label(
                        "Name: " + player.getNom() + "\n" +
                                "Attack: " + player.getAttaque()   + ", Bonus: " + bonusAttack + "\n" +
                                "Defense: " + player.getDefense() +  ", Bonus: " + bonusDefense + "\n" +
                                "Health: " + player.getSante() +  ", Bonus: " + bonusHealth
                );
                resourceLayout.getChildren().add(playerDetails);
            } else {
                System.out.println("Player not initialized.");
            }

            // Display bonuses acquired
            Label bonusesLabel = new Label("\nBonuses from Fights and Buildings:");
            resourceLayout.getChildren().add(bonusesLabel);

            if (player != null && player.getInventaireObjets().isEmpty()) {
                Label noBonusesLabel = new Label("No bonuses acquired yet.");
                resourceLayout.getChildren().add(noBonusesLabel);
            } else if (player != null) {
                player.getInventaireObjets().forEach(obj -> {
                    Label objectLabel = new Label(obj.toString()); // Uses the toString() method of Objet
                    resourceLayout.getChildren().add(objectLabel);
                });
            }

            // Display last enemy fought
            if (lastEnemy != null) {
                Label enemyStatsLabel = new Label("\nLast Enemy Fought:");
                resourceLayout.getChildren().add(enemyStatsLabel);

                Label enemyDetails = new Label(
                        "Name: " + lastEnemy.getNom() + "\n" +
                                "Attack: " + lastEnemy.getAttaque() + "\n" +
                                "Defense: " + lastEnemy.getDefense() + "\n" +
                                "Health: " + lastEnemy.getSante()
                );
                resourceLayout.getChildren().add(enemyDetails);
            } else {
                System.out.println("No last enemy data available.");
            }

            // Display the last game state loaded
            Label gameStateLabel = new Label("\nLast Game State Loaded:\nfrom the file 'etat_du_jeu.txt'");
            resourceLayout.getChildren().add(gameStateLabel);

            Button backButton = new Button("Back");
            // Set the background color of the button to light blue
            backButton.setStyle(
                    "-fx-background-color: #00008B;" + // Light blue color
                            "-fx-text-fill: white;" +         // Set text color to white
                            "-fx-font-weight: bold;" +       // Make the text bold
                            "-fx-border-radius: 5;" +        // Optional: Rounded corners
                            "-fx-background-radius: 5;"      // Match the border radius
            );
            backButton.setOnAction(e -> {
                resourceStage.close(); // Close the resources window
                matrixGrid.requestFocus(); // Reset focus to matrixGrid for key inputs
                displayMatrix(); // Refresh the matrix display to ensure player's position is correct
            });
            resourceLayout.getChildren().add(backButton);

            // Show the resources window
            Scene resourceScene = new Scene(resourceLayout, 600, 650);
            resourceStage.setScene(resourceScene);
            resourceStage.show();
        } catch (Exception e) {
            System.out.println("Error loading game state: " + e.getMessage());
        }
    }
    private List<Objet> loadAllObjects(String objectsFile) {
        List<Objet> objets = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(objectsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                if (details.length == 4) {
                    String nom = details[0].trim();
                    int bonusSante = Integer.parseInt(details[1].trim());
                    int bonusAttaque = Integer.parseInt(details[2].trim());
                    int bonusDefense = Integer.parseInt(details[3].trim());

                    objets.add(new Objet(nom, bonusSante, bonusAttaque, bonusDefense));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading objects: " + e.getMessage());
        }

        return objets;
    }
    private List<Ennemi> loadAllEnemies() {
        String enemyFile = "src/main/resources/com/example/grand_devoir2/ennemis.txt"; // Path to the enemies file
        String objectsFile = "src/main/resources/com/example/grand_devoir2/objets.txt"; // Path to the objects file
        List<Objet> objets = loadAllObjects(objectsFile); // Load objects first
        return chargerEnnemis(enemyFile, objets); // Use the existing method
    }

    private void saveGameStateToFile() {
        String filePath = "game_state.txt";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            GameState gameState = new GameState();
            gameState.setMatrix(carte.getMatrix());
            gameState.setPlayerPosX(playerPosX);
            gameState.setPlayerPosY(playerPosY);
            gameState.setInventory(new HashMap<>(inventory));
            if (player != null) {
                gameState.setPlayerObjects(player.getInventaireObjets());
            }
            gameState.setEnemies(loadAllEnemies());  // Save the list of enemies

            // Save the Carte object
            gameState.setCarte(carte);

            oos.writeObject(gameState);
            System.out.println("Game state saved successfully to: " + new File(filePath).getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error saving game state: " + e.getMessage());
        }
    }
    private void restoreEnemies(List<Ennemi> enemies) {
        if (enemies == null || enemies.isEmpty()) {
            System.out.println("No enemies to restore.");
            return;
        }

        // Clear the game matrix of any previous enemy positions
        int[][] matrix = carte.getMatrix();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 2) { // Assuming '2' is the code for an enemy
                    matrix[i][j] = 0; // Reset to empty
                }
            }
        }

        // Reintegrate each enemy into the game
        for (Ennemi enemy : enemies) {
            // Log enemy details
            System.out.println("Restoring enemy: " + enemy.getNom() +
                    " | Attack: " + enemy.getAttaque() +
                    ", Defense: " + enemy.getDefense() +
                    ", Health: " + enemy.getSante() +
                    ", Capture: " + (enemy.getCapture() != null ? enemy.getCapture().getNom() : "None"));

            // Place the enemy back on the matrix if possible
            // For simplicity, use random placement in empty spots
            boolean placed = false;
            Random random = new Random();
            while (!placed) {
                int x = random.nextInt(matrix.length);
                int y = random.nextInt(matrix[0].length);

                if (matrix[x][y] == 0) { // If the cell is empty
                    matrix[x][y] = 2; // Mark the enemy's position on the matrix
                    placed = true;
                }
            }
        }

        // Refresh the game matrix display
        displayMatrix();

        System.out.println("All enemies restored to the game.");
    }
    private void loadGameStateFromFile(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("game_state.txt"))) {
            GameState gameState = (GameState) ois.readObject();

            // Restore other fields
            carte.setMatrix(gameState.getMatrix());
            playerPosX = gameState.getPlayerPosX();
            playerPosY = gameState.getPlayerPosY();
            inventory.clear();
            inventory.putAll(gameState.getInventory());

            // Synchronize IntegerProperties with restored inventory
            pierreCount.set(inventory.getOrDefault("Pierre", 0));
            cerealesCount.set(inventory.getOrDefault("Céréales", 0));
            boisCount.set(inventory.getOrDefault("Bois", 0));

            if (player != null) {
                player.getInventaireObjets().clear();
                player.getInventaireObjets().addAll(gameState.getPlayerObjects());
            }
            restoreEnemies(gameState.getEnemies()); // Restore the list of enemies

            // Restore the Carte object
            carte = gameState.getCarte();
            carte.setHelloController(this); // Reassign HelloController after deserialization

            displayMatrix(); // Refresh the display
            System.out.println("Game state loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading game state: " + e.getMessage());
        }
    }

    public void onSaveStatusClick(ActionEvent event) {
        saveGameStateToFile();
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Saved");
        alert.setHeaderText(null);
        alert.setContentText("Your game has been saved successfully!");
        alert.showAndWait();

        // Ensure the matrix remains interactive
        matrixGrid.requestFocus();
        System.out.println("Game state saved. You can continue playing.");
    }
    public void onResumeGameClick(ActionEvent event) {
        String filePath = "game_state.txt"; // Path to the saved game file
        File saveFile = new File(filePath);

        if (!saveFile.exists()) {
            // Show an error dialog if the save file does not exist
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Resume Game");
            alert.setHeaderText("No Save File Found");
            alert.setContentText("Unable to resume the game because no save file was found.");
            alert.showAndWait();
            return; // Exit the method
        }

        try {
            loadGameStateFromFile(filePath);

            // Show a success dialog
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Resume Game");
            alert.setHeaderText(null);
            alert.setContentText("Game resumed successfully!");
            alert.showAndWait();

            // Ensure the game remains interactive after resuming
            matrixGrid.requestFocus();
        } catch (Exception e) {
            // Handle any errors during loading
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Resume Game");
            alert.setHeaderText("Error Resuming Game");
            alert.setContentText("An error occurred while trying to resume the game: " + e.getMessage());
            alert.showAndWait();
        }
    }
    private void loadGameState() {
        try {
            // Open the file and read the state
            File file = new File("src/main/resources/com/example/grand_devoir2/etat_du_jeu.txt");
            if (!file.exists()) {
                System.out.println("No saved game state found.");
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int row = 0;

            // Reset the game matrix
            carte = new Carte();
            int[][] matrix = carte.getMatrix();

            // Read the matrix from the file
            while ((line = reader.readLine()) != null && row < matrix.length) {
                String[] values = line.split(" ");
                for (int col = 0; col < values.length; col++) {
                    matrix[row][col] = Integer.parseInt(values[col]);
                    if (matrix[row][col] == PLAYER) {
                        playerPosX = row; // Restore player's X position
                        playerPosY = col; // Restore player's Y position
                    }
                }
                row++;
            }

            // Read the inventory resources
            inventory.clear();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) break; // Stop when encountering an empty line
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    inventory.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                }
            }
            // Synchronize the IntegerProperty counters with the inventory
            pierreCount.set(inventory.getOrDefault("Pierre", 0));
            cerealesCount.set(inventory.getOrDefault("Céréales", 0));
            boisCount.set(inventory.getOrDefault("Bois", 0));

            // Read the inventory objects
            if (player == null) {
                player = new Joueur("Marian", 10, 40, 100); // Initialize the player if not already done
            }
            player.getInventaireObjets().clear(); // Clear existing objects in inventory

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) break; // Stop when encountering an empty line
                Objet obj = loadObjectByName(line.trim());
                if (obj != null) {
                    player.getInventaireObjets().add(obj);
                }
            }

            reader.close();
            displayMatrix(); // Refresh the matrix display
            System.out.println("Game state loaded successfully.");
        } catch (Exception e) {
            System.out.println("Error loading game state: " + e.getMessage());
        }
    }
    private void saveGameState() {
        if (player == null) {
            System.out.println("Cannot save game state: player is null.");
            return; // Exit the method if the player is not initialized
        }
        try {
            File file = new File("src/main/resources/com/example/grand_devoir2/etat_du_jeu.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            // Save the matrix
            int[][] matrix = carte.getMatrix();
            for (int[] row : matrix) {
                for (int cell : row) {
                    writer.write(cell + " ");
                }
                writer.newLine();
            }

            // Save the inventory ressources
            writer.newLine();
            inventory.forEach((resource, count) -> {
                try {
                    writer.write(resource + ": " + count);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Save the inventory objects
            writer.newLine();
            writer.write("Objects:");
            writer.newLine();
            player.getInventaireObjets().forEach(obj -> {
                try {
                    writer.write(obj.getNom());
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            writer.close();
            System.out.println("Game state saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving game state: " + e.getMessage());
        }
    }

    @FXML
    protected void onNewGameClick() {
        carte = new Carte(); // Create a new Carte instance
        playerPosX = 0;      // Reset player's X position
        playerPosY = 0;      // Reset player's Y position

        // Place the player directly on the matrix
        carte.getMatrix()[playerPosX][playerPosY] = PLAYER;

        // Reset resources to zero
        pierreCount.set(0);
        cerealesCount.set(0);
        boisCount.set(0);

        // Clear and reset the inventory map
        inventory.clear();
        inventory.put("Bois", 0);
        inventory.put("Pierre", 0);
        inventory.put("Céréales", 0);

        // Refresh the matrix display
        displayMatrix();

        // Log reset for debugging
        System.out.println("Game reset. All resources and inventory cleared.");

        // Set focus back to the matrixGrid for key input
        matrixGrid.requestFocus();
    }

    @FXML
    protected void onOptionsClick() {
        // Create a dialog for options
        Dialog<Void> optionsDialog = new Dialog<>();
        optionsDialog.setTitle("Options");
        optionsDialog.setHeaderText("Customize Settings");

        // Create a VBox to hold the options
        VBox optionsLayout = new VBox(10);
        optionsLayout.setPadding(new Insets(20));
        optionsLayout.setStyle("-fx-background-color: #f0e0e0; -fx-padding: 20; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Section 1: Player's life as hearts
        Label lifeLabel = new Label("Player's Life:");
        HBox heartsContainer = new HBox(5); // Container for the hearts with spacing
        if (player != null) {
            int life = player.getSante(); // Get player's life
            for (int i = 0; i < life / 10; i++) { // Assume each heart represents 10 life points
                Label heart = new Label("♥");
                heart.setStyle("-fx-font-size: 24px; -fx-text-fill: red;"); // Adjust size and color
                heartsContainer.getChildren().add(heart);
            }
        } else {
            heartsContainer.getChildren().add(new Label("No player data available."));
        }

        // Section 2: Background color customization
        Label colorLabel = new Label("Change Background Color:");
        ChoiceBox<String> colorChoiceBox = new ChoiceBox<>();
        colorChoiceBox.getItems().addAll("Default", "Red", "Green", "Blue", "Yellow", "Pink");
        colorChoiceBox.setValue("Default");

        Button applyColorButton = new Button("Apply Color");
        applyColorButton.setOnAction(event -> {
            String selectedColor = colorChoiceBox.getValue();
            switch (selectedColor) {
                case "Red":
                    rootPane.setStyle("-fx-background-color: red;");
                    break;
                case "Green":
                    rootPane.setStyle("-fx-background-color: green;");
                    break;
                case "Blue":
                    rootPane.setStyle("-fx-background-color: blue;");
                    break;
                case "Yellow":
                    rootPane.setStyle("-fx-background-color: yellow;");
                    break;
                case "Pink":
                    rootPane.setStyle("-fx-background-color: pink;");
                    break;
                default:
                    rootPane.setStyle(null); // Reset to default
            }
        });

        // Section 3: Music control
        Label musicLabel = new Label("Music Settings:");
        Button playMusicButton = new Button("Play Music");
        playMusicButton.setOnAction(event -> {
            String musicPath = "src/main/resources/musique/viva_la_vida.mp3"; // Path to the music file
            try {
                if (backgroundMusicPlayer != null) {
                    backgroundMusicPlayer.stop();
                    backgroundMusicPlayer.dispose();
                }
                Media media = new Media(new File(musicPath).toURI().toString());
                backgroundMusicPlayer = new MediaPlayer(media);
                backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the music
                backgroundMusicPlayer.setVolume(0.5); // Adjust volume
                backgroundMusicPlayer.play();
                System.out.println("Playing: Viva La Vida");
            } catch (Exception e) {
                System.out.println("Error playing music: " + e.getMessage());
            }
        });

        Button stopMusicButton = new Button("Stop Music");
        stopMusicButton.setOnAction(event -> {
            if (backgroundMusicPlayer != null) {
                backgroundMusicPlayer.stop();
                backgroundMusicPlayer.dispose();
                backgroundMusicPlayer = null;
                System.out.println("Music stopped.");
            }
        });

        // Add music controls to a container
        HBox musicControls = new HBox(10, playMusicButton, stopMusicButton);

        // Section 4: Inventory sorting
        Label sortLabel = new Label("Sort Inventory by:");
        Button sortInventoryButton = new Button("Sort Inventory");
        sortInventoryButton.setOnAction(event -> onSortInventoryClick());

        // Add all components to the layout
        optionsLayout.getChildren().addAll(
                lifeLabel, heartsContainer,
                colorLabel, colorChoiceBox, applyColorButton,
                musicLabel, musicControls,
                sortLabel, sortInventoryButton
        );

        // Set the dialog content and show it
        optionsDialog.getDialogPane().setContent(optionsLayout);
        optionsDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        optionsDialog.showAndWait();

        // Refocus the matrix grid to ensure key input works again
        matrixGrid.requestFocus();
    }
    @FXML
    protected void onSortInventoryClick() {
        if (player == null || player.getInventaireObjets().isEmpty()) {
            System.out.println("No items in the inventory to sort.");
            return;
        }

        // Provide sorting options to the user
        List<String> options = Arrays.asList("Attack", "Defense", "Health");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Attack", options);
        dialog.setTitle("Sort Inventory");
        dialog.setHeaderText("Choose an attribute to sort the inventory:");
        dialog.setContentText("Attribute:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String choice = result.get();
            Comparator<Objet> comparator;

            // Choose the appropriate comparator
            switch (choice) {
                case "Attack" -> comparator = new ComparateurAttaque();
                case "Defense" -> comparator = new ComparateurDefense();
                case "Health" -> comparator = new ComparateurSante();
                default -> throw new IllegalArgumentException("Invalid choice: " + choice);
            }

            // Sort the inventory
            player.getInventaireObjets().sort(comparator);
            System.out.println("Inventory sorted by: " + choice);

            // Optionally display the sorted inventory
            System.out.println("Sorted Inventory:");
            for (Objet obj : player.getInventaireObjets()) {
                System.out.println(obj);
            }
        }
    }

    @FXML
    public void onHelpClick() {
        // Create a map of frequently asked questions (FAQs) and their answers
        Map<String, String> faq = new LinkedHashMap<>();
        faq.put("Quelles ressources puis-je collecter?", "Tu peux collecter des arbres, des pierres et des céréales.");
        faq.put("Que puis-je faire avec les ressources collectées?",
                "Tu peux construire différents bâtiments comme une caserne de pompiers, une base militaire ou un supermarché.");
        faq.put("Qu'est-ce que tu as besoin pour construire un batiment (Police Station, Military Base or Supermarket) ?",
                "Tu as besoin de 2 unites de pierre et 2 unites de bois.");
        faq.put("Si tu es sur un emplacement constructible (avec des effets), que peux-tu obtenir?",
                "Selon le bâtiment construit, tu peux bénéficier d'augmentations de santé, de défense ou d'attaque.\n"
                        + "Voici les bâtiments et leurs effets :\n"
                        + "- Fontaine de Vie : Santé maximale et un objet 'Vitamines'.\n"
                        + "- Monument de l'Épée : Augmentation de l'attaque avec un objet 'Épée Magique'.\n"
                        + "- Cabane de Bois : Gagne du bois et un objet 'Hache en Bois'.\n"
                        + "- Tour de Magie : Augmente ta défense et ajoute un objet 'Bouclier Magique'.\n"
                        + "- Cofeterie : Augmente la quantité de céréales et ajoute un objet 'Bonbons Énergétiques'.");

        // Create a dialog for the Help section
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Help");
        dialog.setHeaderText("Questions fréquentes");

        // Create a VBox to display the FAQs
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #800020; -fx-padding: 15; -fx-border-radius: 5; -fx-background-radius: 5;"); // Light burgundy color

        for (Map.Entry<String, String> entry : faq.entrySet()) {
            // Create a label for the question
            Label questionLabel = new Label(entry.getKey());
            questionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

            // Create a label for the answer
            Label answerLabel = new Label(entry.getValue());
            answerLabel.setWrapText(true);
            answerLabel.setStyle("-fx-text-fill: white;");

            // Add the question and answer to the VBox
            vbox.getChildren().addAll(questionLabel, answerLabel);
        }

        // Set the content of the dialog
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE); // Add a close button

        // Show the dialog
        dialog.showAndWait();
    }

    @FXML
    protected void onExitClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Do you really want to quit?");
        alert.setContentText("Your game will be saved.");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            saveGameState();
            stopAllMusic(); // Stop music before exiting
            System.exit(0);
        } else {
            matrixGrid.requestFocus();
        }
    }
}
