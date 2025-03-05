package com.example.grand_devoir2;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class BattleController {

    @FXML
    private ImageView playerImage;
    @FXML
    private ImageView enemyImage;
    @FXML
    private Label playerStats;
    @FXML
    private Label enemyStats;
    @FXML
    private Label battleLog;
    @FXML
    private ProgressBar playerHealthBar; // Health bar for the player
    @FXML
    private ProgressBar enemyHealthBar; // Health bar for the enemy
    @FXML
    private Button changeBackgroundButton;

    private Joueur player;
    private Ennemi enemy;
    private static MediaPlayer backgroundMusicPlayer;
    private static MediaPlayer attackSoundPlayer;
    @FXML
    private void onChangeBackgroundClick() {
        String enemyName = enemy.getNom(); // Use the current enemy's name
        setBattlefieldBackground(enemyName);
    }
    public void initialize(Joueur player, Ennemi enemy) {
        this.player = player;
        this.enemy = enemy;

        // Set the battlefield background
        setBattlefieldBackground(enemy.getNom());

        // Set images
        playerImage.setImage(new Image(getClass().getResourceAsStream("/images/player.png")));
        enemyImage.setImage(getEnemyImage(enemy.getNom()));

        // Initialize health bars
        updateHealthBars();

        // Style battle log
        battleLog.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white; -fx-padding: 10;");

        // Display stats
        updateStats();
    }

    private void setBattlefieldBackground(String enemyName) {
        // Map enemy names to specific backgrounds
        String backgroundFile = switch (enemyName.toLowerCase()) {
            case "flamanzila" -> "/images/forest.jpg";
            case "gerila" -> "/images/dungeon.jpg";
            case "ochila" -> "/images/desert.jpg";
            case "troll" -> "/images/mountain.jpg";
            case "goblin" -> "/images/swamp.jpg";
            case "luminae" -> "/images/castle.jpg";
            case "pasari-lati-lungila" -> "/images/sky.jpg";
            case "pyro-horn" -> "/images/volcano.jpg";
            case "sfanta_duminica" -> "/images/temple.jpg";
            case "spanul" -> "/images/cave.jpg";
            default -> "/images/default.png"; // Fallback background
        };

        Platform.runLater(() -> {
            if (battleLog.getScene() != null) {
                VBox root = (VBox) battleLog.getScene().getRoot();

                // Convert the relative path to an absolute URL
                String absoluteBackgroundUrl = getClass().getResource(backgroundFile).toExternalForm();

                String backgroundImageStyle = "-fx-background-image: url('" + absoluteBackgroundUrl + "'); " +
                        "-fx-background-size: cover;";
                root.setStyle(backgroundImageStyle);
                System.out.println("Background changed to: " + absoluteBackgroundUrl);
            } else {
                System.out.println("Scene not initialized yet, skipping background setup.");
            }
        });
    }

    private Image getEnemyImage(String enemyName) {
        return switch (enemyName.toLowerCase()) {
            case "flamanzila" -> new Image(getClass().getResourceAsStream("/images/flamanzila.png"));
            case "gerila" -> new Image(getClass().getResourceAsStream("/images/gerila.png"));
            case "ochila" -> new Image(getClass().getResourceAsStream("/images/ochila.png"));
            case "troll" -> new Image(getClass().getResourceAsStream("/images/troll.png"));
            case "goblin" -> new Image(getClass().getResourceAsStream("/images/Goblin.jpg"));
            case "luminae" -> new Image(getClass().getResourceAsStream("/images/Luminae.jpg"));
            case "pasari-lati-lungila" -> new Image(getClass().getResourceAsStream("/images/Pasari-Lati-Lungila.jpg"));
            case "pyro-horn" -> new Image(getClass().getResourceAsStream("/images/Pyro-Horn.jpg"));
            case "sfanta_duminica" -> new Image(getClass().getResourceAsStream("/images/Sfanta_Duminica.jpg"));
            case "spanul" -> new Image(getClass().getResourceAsStream("/images/Spanul.jpg"));
            default -> new Image(getClass().getResourceAsStream("/images/default.png"));
        };
    }

    private void updateStats() {
        playerStats.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: white;");
        playerStats.setText("Player:\n" +
                "Name: " + player.getNom() + "\n" +
                "Health: " + player.getSante() + "\n" +
                "Attack: " + player.getAttaque() + "\n" +
                "Defense: " + player.getDefense());

        enemyStats.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: white;");
        enemyStats.setText("Enemy:\n" +
                "Name: " + enemy.getNom() + "\n" +
                "Health: " + enemy.getSante() + "\n" +
                "Attack: " + enemy.getAttaque() + "\n" +
                "Defense: " + enemy.getDefense());
    }

    private void updateHealthBars() {
        // Calculate health bar progress for player and enemy
        double playerHealthProgress = Math.max(Math.min(player.getSante() / 100.0, 1.0), 0.0);
        double enemyHealthProgress = Math.max(Math.min(enemy.getSante() / 100.0, 1.0), 0.0);

        // Update health bars directly
        playerHealthBar.setProgress(playerHealthProgress);
        enemyHealthBar.setProgress(enemyHealthProgress);
    }

    @FXML
    private void onSpecialAbilityClick() {
        // Example: Heal the player
        player.useSpecialAbility("heal"); // Change to "double attack" or "shield" as needed
        battleLog.setText("Player used Heal! Health is now " + player.getSante());
        updateStats();

        if (enemy.getSante() > 0) {
            enemyTurn(); // Enemy takes their turn
        }
    }
    private void playAttackAnimation(ImageView attacker) {
        TranslateTransition attackAnimation = new TranslateTransition();
        attackAnimation.setNode(attacker);
        attackAnimation.setByX(30); // Move slightly to the right
        attackAnimation.setDuration(javafx.util.Duration.millis(200)); // Fast animation
        attackAnimation.setAutoReverse(true); // Return to original position
        attackAnimation.setCycleCount(2); // Move back and forth
        attackAnimation.play();
    }

    private void playSwordEffect(ImageView attacker, ImageView target) {
        // Create the sword image
        ImageView sword = new ImageView(new Image(getClass().getResourceAsStream("/images/sword.jpg")));
        sword.setFitWidth(100);
        sword.setFitHeight(50);

        // Calculate the sword's initial position near the attacker
        double attackerCenterX = attacker.getLayoutX() + attacker.getBoundsInParent().getWidth() / 2;
        double attackerCenterY = attacker.getLayoutY() + attacker.getBoundsInParent().getHeight() / 2;

        // Adjust the Y position to place the sword higher initially
        double positionOffset = 50; // Adjust to position the sword higher
        sword.setLayoutX(attackerCenterX - sword.getFitWidth() / 2); // Center the sword horizontally
        sword.setLayoutY(attackerCenterY - sword.getFitHeight() / 2 - positionOffset); // Position higher

        // Add the sword to a static pane already part of the layout
        Pane animationPane = (Pane) attacker.getScene().lookup("#animationPane");
        animationPane.getChildren().add(sword);

        // Calculate the target position
        double targetCenterX = target.getLayoutX() + target.getBoundsInParent().getWidth() / 2;
        double targetCenterY = target.getLayoutY() + target.getBoundsInParent().getHeight() / 2;

        // Create the animation
        TranslateTransition swordMove = new TranslateTransition();
        swordMove.setNode(sword);
        swordMove.setFromX(0); // Start from the current position of the sword
        swordMove.setFromY(0);
        swordMove.setToX(targetCenterX - attackerCenterX); // Move relative to the attacker's center
        swordMove.setToY(targetCenterY - attackerCenterY);
        swordMove.setDuration(javafx.util.Duration.millis(300));

        // Remove the sword after the animation completes
        swordMove.setOnFinished(e -> animationPane.getChildren().remove(sword));
        swordMove.play();
    }

    @FXML
    private void onAttackClick() {
        // Play player attack animation
        playAttackAnimation(playerImage);
        playSwordEffect(playerImage, enemyImage);

        // Player attacks enemy
        enemy.takedamage(player.getAttaque());
        battleLog.setText("Player attacks! Enemy health: " + enemy.getSante());
        updateStats();

        if (enemy.getSante() <= 0) {
            battleLog.setText(battleLog.getText() + "\nEnemy defeated!");
            endBattle(true);
            return;
        }

        // Enemy's turn to attack
        Platform.runLater(this::enemyTurn);
    }
    private void playExplosionAnimation(ImageView target) {
        ScaleTransition explosionAnimation = new ScaleTransition();
        explosionAnimation.setNode(target);
        explosionAnimation.setByX(0.1); // Increase size slightly
        explosionAnimation.setByY(0.1);
        explosionAnimation.setDuration(javafx.util.Duration.millis(100)); // Quick explosion
        explosionAnimation.setAutoReverse(true); // Return to original size
        explosionAnimation.setCycleCount(2); // Expand and shrink
        explosionAnimation.play();
    }

    private void enemyTurn() {
        if (Math.random() > 0.7) { // 30% chance to use special ability
            enemy.useSpecialAbility("counterattack", player);
            battleLog.setText(battleLog.getText() + "\nEnemy used Counterattack!");
            playExplosionAnimation(playerImage);
        } else {
            player.takedamage(enemy.getAttaque());
            battleLog.setText(battleLog.getText() + "\nEnemy attacks! Player health: " + player.getSante());
            playExplosionAnimation(playerImage); // Add explosion effect
        }
        updateStats();

        if (player.getSante() <= 0) {
            battleLog.setText(battleLog.getText() + "\nPlayer defeated!");
            endBattle(false);
        }
    }

    private void endBattle(boolean playerWon) {

        String result = playerWon ? "Victory!" : "Defeat!";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Battle Result");
        alert.setHeaderText(result);
        alert.setContentText(playerWon ? "You defeated the enemy!" : "You were defeated by the enemy.");
        alert.showAndWait();

        // Close the battle window
        Platform.runLater(() -> playerImage.getScene().getWindow().hide());
        System.out.println("Battle ended. Player won: " + playerWon);
    }
}
