package com.example.grand_devoir2;


import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.Serializable;
import java.util.Random;

public class Ennemi extends Character implements Serializable {
    private Objet capture;
    private int santeMax; // Maximum health
    private static final long serialVersionUID = 1L;

    // Default constructor (for deserialization or default initialization)
    public Ennemi() {
        super("", 0, 0, 0);
        this.capture = null;
        this.santeMax = 0;
    }

    public Ennemi(String nom, int attaque, int defense, int sante, Objet capture)
    {
        super(nom, attaque, defense, sante);
        this.capture=capture;
        this.santeMax = sante; // Set the max health to the initial health
    }

    public Objet getCapture() {
        return capture;
    }

    @Override
    public void damage(Character adversaire) {
        adversaire.takedamage(this.attaque);
    }
    @Override
    public void takedamage(int damage) {
        // Calculate effective damage with a minimum threshold (5% of max health)
        int effectiveDamage = Math.max(damage - (this.defense / 2), (int) (this.santeMax * 0.05));

        // Reduce health and ensure it doesn't drop below zero
        this.sante = Math.max(this.sante - effectiveDamage, 0);

        // Check if the character dies
        if (this.sante == 0) die();
    }
    public void useSpecialAbility(String ability, Joueur player) {
        switch (ability.toLowerCase()) {
            case "counterattack":
                int counterDamage = Math.max(5, player.getAttaque() / 2); // Damage is half of player's attack
                player.takedamage(counterDamage);
                System.out.println("Enemy counterattacked! Player took " + counterDamage + " damage.");
                break;

            case "area damage":
                int areaDamage = 15; // Fixed damage to player
                player.takedamage(areaDamage);
                System.out.println("Enemy used area damage! Player took " + areaDamage + " damage.");
                break;

            default:
                System.out.println("Unknown ability!");
        }
    }
    @Override
    public void die()
    {
        this.statut = false;
        System.out.println("Le caractere " + " " + nom + " a ete vaincu!");
    }

    // Validate object during deserialization
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Validate fields
        if (nom == null || attaque < 0 || defense < 0 || sante < 0 || santeMax < 0) {
            throw new InvalidObjectException("Invalid data for Ennemi.");
        }
    }

    // Reset the enemy to its initial state
    public void reset() {
        this.sante = this.santeMax;
        this.statut = true;
    }
    @Override
    public String toString() {
        return "Ennemi{" +
                "nom='" + nom + '\'' +
                ", attaque=" + attaque +
                ", defense=" + defense +
                ", sante=" + sante +
                ", santeMax=" + santeMax +
                ", capture=" + (capture != null ? capture.getNom() : "None") +
                '}';
    }
}
