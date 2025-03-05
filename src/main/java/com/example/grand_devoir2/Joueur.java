package com.example.grand_devoir2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.io.*;

public class Joueur extends Character implements Serializable {
    private int bois;
    private int pierre;
    private int nourriture;
    private int santeMax;
    private List<Objet> inventaire_objets;   //pentru obiectele jucatorului din inventar(lista)
    private Map<String, Integer> inventaireRessources;  // HashMap pentru stocarea resurselor
    private static final long serialVersionUID = 1L;

    // Default constructor (for serialization and initialization)
    public Joueur() {
        super("", 0, 0, 0); // Call parent constructor with default values
        this.bois = 0;
        this.pierre = 0;
        this.nourriture = 0;
        this.santeMax = 100;
        this.inventaire_objets = new ArrayList<>();
        this.inventaireRessources = new HashMap<>();
        inventaireRessources.put("bois", 0);
        inventaireRessources.put("pierre", 0);
        inventaireRessources.put("nourriture", 0);
    }

    public Joueur(String nom, int attaque, int defense, int sante)
    {
        super(nom, attaque, defense, sante);
        this.pierre=0;
        this.bois=0;
        this.nourriture=0;
        this.santeMax=100; //initialise la sante maximale
        this.inventaire_objets = new ArrayList<>();

        this.inventaireRessources = new HashMap<>();
        // Initialize the map with resource types, all set to 0 initially
        inventaireRessources.put("bois", 0);
        inventaireRessources.put("pierre", 0);
        inventaireRessources.put("nourriture", 0);
    }
    public int getBois() {
        return bois;
    }
    public int getPierre() {
        return pierre;
    }
    public int getNourriture() {
        return nourriture;
    }
    public List<Objet> getInventaireObjets() {
        return inventaire_objets;
    }
    @Override
    public int getSante() {
        return super.getSante(); // Call the parent class's getSante method
    }

    // Méthodes pour ajouter des ressources
    public void ajouterBois(int quantite) {
       bois+=quantite;
       syncInventaireRessources();
    }

    public void ajouterPierre(int quantite) {
       pierre+=quantite;
       syncInventaireRessources();
    }

    public void ajouterNourriture(int quantite) {
        nourriture+=quantite;
        syncInventaireRessources();
    }

    public void ramasser(Objet objet)
    {
        System.out.println("Vous avez ramassé : " + objet.getNom());

        // Ajouter l'objet à l'inventaire
        inventaire_objets.add(objet);

        // Appliquer les effets de l'objet au joueur
        if (objet.getBonusAttaque() > 0)
        {
            attaque += objet.getBonusAttaque();
            System.out.println("Votre attaque a augmenté de " + objet.getBonusAttaque() + " points.");
        }

        if (objet.getBonusDefense() > 0)
        {
            defense += objet.getBonusDefense();
            System.out.println("Votre défense a augmenté de " + objet.getBonusDefense() + " points.");
        }

        if (objet.getBonusSante() > 0) {
            sante += objet.getBonusSante();
            if (sante > santeMax)
            {
                sante = santeMax;
            }
            System.out.println("Votre santé a augmenté de " + objet.getBonusSante() + " points.");
        }
        afficherStatistiques();
    }

    // Méthode pour afficher les statistiques actuelles du joueur
    public void afficherStatistiques() {
        System.out.println("Statistiques actuelles de " + nom + " :");
        System.out.println("Attaque : " + attaque);
        System.out.println("Défense : " + defense);
        System.out.println("Santé : " + sante);

        if (inventaire_objets.isEmpty()) {
            System.out.println("Votre inventaire est vide.");
        } else {
            System.out.println("Inventaire des objets :");
            for (Objet obj : inventaire_objets) {
                System.out.println(obj); // This uses the toString() method of Objet
            }
        }
    }
    public void useSpecialAbility(String ability) {
        switch (ability.toLowerCase()) {
            case "heal":
                int healAmount = 20; // Example heal amount
                sante = Math.min(sante + healAmount, santeMax); // Heal without exceeding max health
                System.out.println("Player healed for " + healAmount + " health!");
                break;

            case "double attack":
                attaque *= 2; // Temporarily double attack
                System.out.println("Player's attack doubled!");
                break;

            case "shield":
                defense += 10; // Temporarily increase defense
                System.out.println("Player's defense increased by 10!");
                break;

            default:
                System.out.println("Unknown ability!");
        }
    }

    @Override
    public void damage(Character adversaire) {  //adversarl a fost atacat, lovit
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

    public void cutHealthInHalf() {
        this.sante /= 2; // Halve the health directly
        if (this.sante < 0) {
            this.sante = 0; // Ensure health doesn't go negative
        }
        System.out.println("Votre santé a été réduite de moitié : " + this.sante);
    }

    @Override
    public void die()
    {
        this.statut = false;
        System.out.println("Le caractere " + " " + nom + " est mort!");
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (nom == null || attaque < 0 || defense < 0 || sante < 0 || santeMax < 0) {
            throw new InvalidObjectException("Invalid data for Joueur.");
        }
        if (inventaire_objets == null) {
            inventaire_objets = new ArrayList<>();
        }
        if (inventaireRessources == null) {
            inventaireRessources = new HashMap<>();
        }
    }

    @Override
    public String toString() {
        return "Joueur{" +
                "nom='" + nom + '\'' +
                ", attaque=" + attaque +
                ", defense=" + defense +
                ", sante=" + sante +
                ", santeMax=" + santeMax +
                ", bois=" + bois +
                ", pierre=" + pierre +
                ", nourriture=" + nourriture +
                ", inventaire_objets=" + inventaire_objets +
                ", inventaireRessources=" + inventaireRessources +
                '}';
    }

    private void syncInventaireRessources() {
        inventaireRessources.put("bois", bois);
        inventaireRessources.put("pierre", pierre);
        inventaireRessources.put("nourriture", nourriture);
    }
}
